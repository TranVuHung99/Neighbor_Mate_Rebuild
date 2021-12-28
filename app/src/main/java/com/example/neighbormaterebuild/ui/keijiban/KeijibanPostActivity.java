package com.example.neighbormaterebuild.ui.keijiban;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.controller.KeijibanController;
import com.example.neighbormaterebuild.model.Keijiban;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;
import com.example.neighbormaterebuild.utils.CShowProgress;
import com.example.neighbormaterebuild.utils.ImageUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeijibanPostActivity extends AppCompatActivity {

    private Toolbar toolbarKeijiban;
    private LinearLayout btnImageStorage, layoutSubmit;
    private ImageView imgStorage;
    private EditText tvContent;

    private static final int TAKE_PIC = 101;
    private static final int REQUEST_CODE = 100;
    private KeijibanController keijibanController;

    private CShowProgress cShowProgress;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(KeijibanPostActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keijiban_post);

        keijibanController = new KeijibanController(Config.API_URL);
        cShowProgress = CShowProgress.getInstance();
        initView();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    private void initView() {
        toolbarKeijiban = findViewById(R.id.toolbarKeijiban);
        layoutSubmit = findViewById(R.id.layoutSubmit);
        tvContent = findViewById(R.id.tvContent);
        btnImageStorage = findViewById(R.id.btnImageStorage);
        imgStorage = findViewById(R.id.imgStorage);
        toolbarKeijiban.setLogo(R.drawable.ic_back_svg);

        View view = toolbarKeijiban.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnImageStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(KeijibanPostActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        });

        //hide keyboard
        final LinearLayout actionHide = findViewById(R.id.wrapper);
        actionHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionHide.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tvContent.getWindowToken(), 0);
            }
        });

        layoutSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("kiemtra", layoutSubmit.isEnabled() + " ");
                layoutSubmit.setEnabled(false);
                if (tvContent.getText().toString().trim().isEmpty()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(KeijibanPostActivity.this);
                    alertBuilder.setMessage("掲示板内容を記入してください。");
                    alertBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                    layoutSubmit.setEnabled(true);
                } else {

                    int minusPoinText = PointStore.getInstance().getItemPointFee(PointFee.postKeijiban);
                    int minusPointImg = PointStore.getInstance().getItemPointFee(PointFee.postKeijibanImage);
                    int totalMinusPoint = minusPoinText;
                    if (getFile() != null) {
                        totalMinusPoint = totalMinusPoint + minusPointImg;
                    }
                    //Log.e("totalMinusPoint", totalMinusPoint + " ");
                    if (PointStore.getInstance().getTotalPoint() < totalMinusPoint) {
                        PointStore.getInstance().showDialogUserNotEnoughPoint(KeijibanPostActivity.this);
                        return;
                    }

                    cShowProgress.showProgress(KeijibanPostActivity.this);
                    MultipartBody.Part body = null;
                    //Log.e("kiemtra", getFile() + " ");
                    if (getFile() != null) {
                        RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), getFile());
                        body = MultipartBody.Part.createFormData("image",
                                getFile().getName(),
                                fileReqBody);
                    } else {
                        imgStorage.setImageDrawable(null);
                    }
                    SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
                    String token = sharedPref.getString("token", "");
                    RequestBody tokenBody = RequestBody.create(MediaType.parse("text/plain"), token);
                    RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), tvContent.getText().toString().trim());
                    Map<String, RequestBody> map = new HashMap<>();
                    map.put("token", tokenBody);
                    map.put("content", contentBody);
                    keijibanController.postKeijiban(body, map).enqueue(new Callback<Keijiban.KeijibanPost>() {
                        @Override
                        public void onResponse(Call<Keijiban.KeijibanPost> call, Response<Keijiban.KeijibanPost> response) {
                            if (response.isSuccessful()) {
                                tvContent.setText("");
                                imgStorage.setImageDrawable(null);
                                cShowProgress.hideProgress();
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(KeijibanPostActivity.this);
                                alertBuilder.setMessage(response.body().getMessage());
                                alertBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertBuilder.show();
                            } else if (response.code() == 402) {
                                cShowProgress.hideProgress();
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(KeijibanPostActivity.this);
                                alertBuilder.setMessage("ポイントが足りないため、掲示板を投稿できません。");
                                alertBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertBuilder.show();
                            } else if (response.code() == 400) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<Report>() {
                                }.getType();
                                Report errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                                cShowProgress.hideProgress();
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(KeijibanPostActivity.this);
                                alertBuilder.setMessage("掲示板内容を記入してください。");
                                alertBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertBuilder.show();
                            } else {
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(KeijibanPostActivity.this);
                                builder.setMessage("このファイルタイプは送信できません。")
                                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                builder.show();
                            }
                            layoutSubmit.setEnabled(true);
                            //Log.e("kiemtra", response.code() + " " + response.message());
                        }

                        @Override
                        public void onFailure(Call<Keijiban.KeijibanPost> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Image"), TAKE_PIC);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PIC && resultCode == RESULT_OK && data != null) {
            try {
                final Uri uri = data.getData();
                //final InputStream is = getContentResolver().openInputStream(uri);
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                File fileshow = new File(getPathFromUri(uri));

                final File file_resize = ImageUtil.resizeImageFile(this, uri);
                if (file_resize != null) {
                    setFile(file_resize);

                    Glide.with(KeijibanPostActivity.this)
                            .asBitmap()
                            .load(fileshow)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    imgStorage.setVisibility(View.VISIBLE);
                                    imgStorage.setImageBitmap(resource);
                                }
                            });
                } else {
                    Toast.makeText(this,
                            "このファイルタイプは送信できません。",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPathFromUri(Uri uri) {
        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            realPath = getRealPathFromURI_BelowAPI11(this, uri);
        }
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19) {
            realPath = getRealPathFromURI_API11to18(this, uri);
        }
        // SDK > 19 (Android 4.4) and up
        else {
            realPath = getRealPathFromURI_API19(this, uri);
        }
        return realPath;
    }

    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = 0;
        String result = "";
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return result;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI_API19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public String getFileNameUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private byte[] encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
//        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return b;
    }
}
