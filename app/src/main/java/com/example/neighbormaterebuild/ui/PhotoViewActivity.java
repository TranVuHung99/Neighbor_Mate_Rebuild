package com.example.neighbormaterebuild.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.comm.ExampleBottomSheetDialog;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.controller.UserListController;
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.model.Block;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.network.HttpHeaders;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.utils.CacheImage;
import com.github.chrisbanes.photoview.PhotoView;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoViewActivity extends SwipeBackActivity implements ExampleBottomSheetDialog.BottomSheetListener {

    private static final String TAG = "PhotoViewActivity";
    public final ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog(PhotoViewActivity.this);

    private String user_code, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        setDragEdge(SwipeBackLayout.DragEdge.BOTTOM);

        Bundle bundle = getIntent().getExtras();
        String linkimg = bundle.getString("avatar_url");
        final boolean consumePoint = bundle.getBoolean("consumePoint");
        user_code = bundle.getString("user_code");
        user_id = bundle.getString("user_id");

        final ProgressBar progressBar = findViewById(R.id.progressLoading);
        PhotoView photoView = findViewById(R.id.photo_view);

        Log.e(TAG, "User code :: " + user_code + "id" + user_id);

        try {

            String avatarUrl = CacheImage.create(PhotoViewActivity.this, linkimg);

            LazyHeaders.Builder headers = new LazyHeaders.Builder();
            final HashMap<String, String> hashMap = HttpHeaders.getInstance().headers();
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null)
                    headers.addHeader(entry.getKey(), entry.getValue());
            }

            GlideUrl url = new GlideUrl(avatarUrl, headers.build());

            RequestOptions sharedOptions =
                    new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_image_solid_svg);

            if (url != null)
                Glide.with(PhotoViewActivity.this)
                        .load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                if (consumePoint) {
                                    PointStore.getInstance().consumePoint(PointFee.viewImageChatting);
                                }
                                return false;
                            }
                        })
                        .apply(sharedOptions)
                        .into(photoView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageButton btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                finish();
            }
        });

        ImageButton btnDialog = findViewById(R.id.btn_dialog_fragment);
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

    }

    @Override
    public void onButtonClickedLock(UserListController userListController) {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        userListController.userLock(token, user_id, user_code, 1).enqueue(new Callback<Block>() {
            @Override
            public void onResponse(Call<Block> call, Response<Block> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (PhotoViewActivity.this.hasWindowFocus())
                        new AlertDialog.Builder(PhotoViewActivity.this)
                                .setMessage(response.body().getMessage())
                                .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                }
            }

            @Override
            public void onFailure(Call<Block> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onButtonClickedReport(UserListController userListController, EditText edtReport, Dialog dialog) {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        userListController.userReport(token, user_id, user_code, edtReport.getText().toString().trim(), "profile").enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dialog.cancel();
                    if (PhotoViewActivity.this.hasWindowFocus())
                        new AlertDialog.Builder(PhotoViewActivity.this)
                                .setMessage(response.body().getMessage())
                                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogC, int which) {
                                        dialogC.cancel();
                                    }
                                })
                                .show();
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
