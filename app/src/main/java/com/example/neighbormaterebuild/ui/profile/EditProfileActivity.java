package com.example.neighbormaterebuild.ui.profile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.MyPageController;
import com.example.neighbormaterebuild.controller.UserListController;
import com.example.neighbormaterebuild.model.AvatarUrl;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.model.Util;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;
import com.example.neighbormaterebuild.utils.CacheImage;
import com.example.neighbormaterebuild.utils.ImageUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Toolbar toolbarEditProfile;
    private Spinner spinnerHeight, spinnerIncome, spinnerJob, spinnerStyle;
    private TextView tvDisplayname, tvSex, tvAge, tvArea;
    private EditText edtStatus;
    private MyPageController myPageController;
    private ImageView imgAvatar1, imgAvatar2, imgAvatar3;
    private ProgressBar pb_load_image_1, pb_load_image_2, pb_load_image_3;
    private Button btnSubmit;
    private HashMap<String, RequestBody> result;
    private List<MultipartBody.Part> images;
    private User user;
    private HashMap<String, String> fills;
    private UserListController userListController;
    private int checkIsImage = 0;
    private int checkIsView = 0;


    private static final int TAKE_PIC_1 = 101;
    private static final int TAKE_PIC_2 = 102;
    private static final int TAKE_PIC_3 = 103;
    private static final int REQUEST_CODE_1 = 1001;
    private static final int REQUEST_CODE_2 = 1002;
    private static final int REQUEST_CODE_3 = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(EditProfileActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        user = new User();
        fills = new HashMap<>();
        result = new HashMap<>();
        images = new ArrayList<>();
        userListController = new UserListController(Config.API_URL);
        myPageController = new MyPageController(Config.API_URL);
        initView();
    }

    private void initView() {
        imgAvatar1 = findViewById(R.id.imgAvatar1);
        imgAvatar2 = findViewById(R.id.imgAvatar2);
        imgAvatar3 = findViewById(R.id.imgAvatar3);
        edtStatus = findViewById(R.id.edtStatus);
        tvDisplayname = findViewById(R.id.tvDisplayname);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvSex = findViewById(R.id.tvSex);
        tvAge = findViewById(R.id.tvAge);
        tvArea = findViewById(R.id.tvArea);
        spinnerJob = findViewById(R.id.spinnerJob);
        spinnerHeight = findViewById(R.id.spinnerHeight);
        spinnerIncome = findViewById(R.id.spinnerIncome);
        spinnerStyle = findViewById(R.id.spinnerStyle);
        pb_load_image_1 = findViewById(R.id.pb_load_image_1);
        pb_load_image_2 = findViewById(R.id.pb_load_image_2);
        pb_load_image_3 = findViewById(R.id.pb_load_image_3);
        toolbarEditProfile = findViewById(R.id.toolbarEditProfile);
        toolbarEditProfile.setLogo(R.drawable.ic_back_svg);

        View viewType = toolbarEditProfile.getChildAt(1);
        viewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getMetadataApi(Metadata.getInstance());

        imgAvatar1.setOnClickListener(this);
        imgAvatar2.setOnClickListener(this);
        imgAvatar3.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void getMetadataApi(Metadata body) {
        try {
            if (body == null || body.getData() == null) return;
            List<Util> heightList = new ArrayList<>();
            heightList.add(new Util("", body.getData().getProfile_not_set(), ""));
            heightList.addAll(body.getData().getUserProfileList().getHeight());
            ArrayAdapter<Util> adapter2 = new ArrayAdapter<Util>(this, R.layout.spinner_item, heightList);
            spinnerHeight.setAdapter(adapter2);

            List<Util> incomeList = new ArrayList<>();
            incomeList.add(new Util("", body.getData().getProfile_not_set(), ""));
            incomeList.addAll(body.getData().getUserProfileList().getIncome());
            ArrayAdapter<Util> adapter3 = new ArrayAdapter<Util>(this, R.layout.spinner_item, incomeList);
            spinnerIncome.setAdapter(adapter3);

            List<Util> professionalList = new ArrayList<>();
            professionalList.add(new Util("", body.getData().getProfile_not_set(), ""));
            professionalList.addAll(body.getData().getUserProfileList().getJob());
            ArrayAdapter<Util> adapter4 = new ArrayAdapter<Util>(this, R.layout.spinner_item, professionalList);
            spinnerJob.setAdapter(adapter4);

            List<Util> figureList = new ArrayList<>();
            figureList.add(new Util("", body.getData().getProfile_not_set(), ""));
            figureList.addAll(body.getData().getUserProfileList().getStyle());
            ArrayAdapter<Util> adapter6 = new ArrayAdapter<Util>(this, R.layout.spinner_item, figureList);
            spinnerStyle.setAdapter(adapter6);

            spinnerHeight.setOnItemSelectedListener(this);
            spinnerIncome.setOnItemSelectedListener(this);
            spinnerJob.setOnItemSelectedListener(this);
            spinnerStyle.setOnItemSelectedListener(this);

            SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
            String token = sharedPref.getString("token", "");
            myPageController.getUserProfile(token).enqueue(new Callback<User.UserResponse>() {
                @Override
                public void onResponse(Call<User.UserResponse> call, Response<User.UserResponse> response) {
                    if (response.isSuccessful()) {
                        User userRes = response.body().getUser();
                        tvDisplayname.setText(userRes.getDisplayname());
                        Gson g = new Gson();
                        AvatarUrl[] a = g.fromJson(response.body().getUser().getImage(), AvatarUrl[].class);
                        setImage(a[0], imgAvatar1, pb_load_image_1);
                        setImage(a[1], imgAvatar2, pb_load_image_2);
                        setImage(a[2], imgAvatar3, pb_load_image_3);

                        for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getSex().size(); i++) {
                            if (Metadata.getInstance().getData().getUserProfileList().getSex().get(i).getValue().equals(userRes.getSex())) {
                                tvSex.setText(Metadata.getInstance().getData().getUserProfileList().getSex().get(i).getName());
                                break;
                            }
                        }
                        for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getAge().size(); i++) {
                            if (Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getValue().equals(userRes.getAge())) {
                                tvAge.setText(Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getName());
                                break;
                            }
                        }
                        if (userRes.getHeight() != null) {
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getHeight().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getHeight().get(i).getValue().equals(userRes.getHeight())) {
                                    spinnerHeight.setSelection(i + 1);
                                    break;
                                }
                            }
                        }
                        if (userRes.getIncome() != null) {
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getIncome().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getIncome().get(i).getValue().equals(userRes.getIncome())) {
                                    spinnerIncome.setSelection(i + 1);
                                    break;
                                }
                            }
                        }
                        if (userRes.getJob() != null) {
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getJob().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getJob().get(i).getValue().equals(userRes.getJob())) {
                                    spinnerJob.setSelection(i + 1);
                                    break;
                                }
                            }
                        }
                        if (userRes.getStyle() != null) {
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getStyle().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getStyle().get(i).getValue().equals(userRes.getStyle())) {
                                    spinnerStyle.setSelection(i + 1);
//                                Log.e("spinerKiemtra", ((TextView)spinnerStyle.getChildAt(i+1)).getText().toString());
                                    break;
                                }
                            }
                        }

//                    Log.e("spinerKiemtra", ((TextView)spinnerStyle.getChildAt(0)).getText().toString());
                        if (userRes.getUser_status_tmp() != null && !userRes.getUser_status_tmp().isEmpty()) {
                            edtStatus.setText(userRes.getUser_status_tmp());
                        } else if (userRes.getUser_status() != null && !userRes.getUser_status().isEmpty()) {
                            edtStatus.setText(userRes.getUser_status());
                        }
                        checkIsView = 0;
                        //Log.e("kiemtraUsser", userRes + "");
                        tvArea.setText(userRes.getArea_name() + "\t" + userRes.getCity_name());
                    }
                }

                @Override
                public void onFailure(Call<User.UserResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
        }
    }

    private void setImage(AvatarUrl url, ImageView imageView, final ProgressBar progressBar) {
        try {
            RequestOptions sharedOptions =
                    new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_image_solid_svg);

            String avatarUrl = CacheImage.create(EditProfileActivity.this, url.getPath());

            if (avatarUrl != null)
                Glide.with(EditProfileActivity.this)
                        .load(avatarUrl)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .apply(sharedOptions)
                        .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgAvatar1:
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_1);
                break;
            case R.id.imgAvatar2:
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_2);
                break;
            case R.id.imgAvatar3:
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_3);
                break;
            case R.id.btnSubmit:
                submitForm();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Image"), TAKE_PIC_1);

        }
        if (requestCode == REQUEST_CODE_2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Image"), TAKE_PIC_2);
        }
        if (requestCode == REQUEST_CODE_3 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Image"), TAKE_PIC_3);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PIC_1 && resultCode == RESULT_OK && data != null) {
            try {
                final Uri uri = data.getData();
                final InputStream is = getContentResolver().openInputStream(uri);

                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                setImageAvatar(imgAvatar1, bitmap, pb_load_image_1);

                final File file = ImageUtil.resizeImageFile(this, uri);
                setFile1(file);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == TAKE_PIC_2 && resultCode == RESULT_OK && data != null) {
            try {
                final Uri uri = data.getData();
                final InputStream is = getContentResolver().openInputStream(uri);

                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                setImageAvatar(imgAvatar2, bitmap, pb_load_image_2);

                final File file = ImageUtil.resizeImageFile(this, uri);
                setFile2(file);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == TAKE_PIC_3 && resultCode == RESULT_OK && data != null) {
            try {
                final Uri uri = data.getData();
                final InputStream is = getContentResolver().openInputStream(uri);

                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                setImageAvatar(imgAvatar3, bitmap, pb_load_image_3);

                final File file = ImageUtil.resizeImageFile(this, uri);
                setFile3(file);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File file1, file2, file3;

    private void setFile1(File file1) {
        this.file1 = file1;
    }

    private void setFile2(File file2) {
        this.file2 = file2;
    }

    private void setFile3(File file3) {
        this.file3 = file3;
    }

    private File getFile1() {
        return file1;
    }

    private File getFile2() {
        return file2;
    }

    private File getFile3() {
        return file3;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.spinnerHeight:
                Util heightModel = (Util) parent.getItemAtPosition(position);
                user.setHeight(heightModel.getValue());
                break;
            case R.id.spinnerStyle:
                Util styleModel = (Util) parent.getItemAtPosition(position);
                user.setStyle(styleModel.getValue());
                break;
            case R.id.spinnerIncome:
                Util icomeModel = (Util) parent.getItemAtPosition(position);
                user.setIncome(icomeModel.getValue());
                break;
            case R.id.spinnerJob:
                Util jobModel = (Util) parent.getItemAtPosition(position);
                user.setJob(jobModel.getValue());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void submitForm() {
        if (!edtStatus.getText().toString().trim().isEmpty()
                && Metadata.NGWord.test(edtStatus.getText().toString().trim())) {
            new AlertDialog.Builder(EditProfileActivity.this)
                    .setMessage("投稿内容にNGワードが含まれているため、書き込めません。もう一度入力してください。")
                    .setPositiveButton("OK", null).create()
                    .show();
            return;
        }

        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        RequestBody tokenBody = RequestBody.create(MediaType.parse("text/plain"), token);
        result.put("token", tokenBody);

        if (getFile1() != null) {
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), getFile1());
            MultipartBody.Part body = MultipartBody.Part.createFormData("images[0]", getFile1().getName(), fileReqBody);
            images.add(body);
        }
        if (getFile2() != null) {
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), getFile2());
            MultipartBody.Part body = MultipartBody.Part.createFormData("images[1]", getFile2().getName(), fileReqBody);
            images.add(body);
        }
        if (getFile3() != null) {
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), getFile3());
            MultipartBody.Part body = MultipartBody.Part.createFormData("images[2]", getFile3().getName(), fileReqBody);
            images.add(body);
        }
        if (user.getHeight() != null) {
            fills.put("height", user.getHeight());
        }
        if (user.getStyle() != null) {
            fills.put("style", user.getStyle());
        }
        if (user.getIncome() != null) {
            fills.put("income", user.getIncome());
        }
        if (user.getJob() != null) {
            fills.put("job", user.getJob());
        }
        if (!edtStatus.getText().toString().trim().isEmpty()) {
            fills.put("user_status", edtStatus.getText().toString().trim());
        }

        if (images.size() > 0) {
            checkIsImage = 0;
            for (int i = 0; i < images.size(); i++) {
                userListController.userUpdateImage(result, images.get(i)).enqueue(new Callback<Report>() {
                    @Override
                    public void onResponse(Call<Report> call, Response<Report> response) {
                        if (response.isSuccessful()) {
                        }
                    }

                    @Override
                    public void onFailure(Call<Report> call, Throwable t) {
                    }
                });
            }
        }

        userListController.userUpdateProfile(token, fills).enqueue(new Callback<User.UserResponse>() {
            @Override
            public void onResponse(Call<User.UserResponse> call, Response<User.UserResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        if (EditProfileActivity.this.hasWindowFocus())
                            new AlertDialog.Builder(EditProfileActivity.this)
                                    .setMessage("プロフィールを更新しました。")
                                    .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkIsView = 0;
                                            dialog.cancel();
                                        }
                                    })
                                    .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User.UserResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (checkIsImage == 0 && checkIsView == 0) {
            finish();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditProfileActivity.this);
            alertBuilder.setMessage("プロフィールの更新はできていません。前の画面に戻りますか？");
            alertBuilder.setNegativeButton("はい", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            alertBuilder.setPositiveButton("いいえ", null);
            alertBuilder.show();
        }
    }

    private void setImageAvatar(ImageView imageAvatar, Bitmap bitmap, final ProgressBar progressBar) {
        Log.e("sadfasf", bitmap + "");
        Glide.with(EditProfileActivity.this)
                .load(bitmap)
                .override(500)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageAvatar);
    }
}
