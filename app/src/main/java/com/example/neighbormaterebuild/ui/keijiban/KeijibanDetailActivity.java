package com.example.neighbormaterebuild.ui.keijiban;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.comm.ExampleBottomSheetDialog;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.controller.FavoriteController;
import com.example.neighbormaterebuild.controller.KeijibanController;
import com.example.neighbormaterebuild.controller.PointController;
import com.example.neighbormaterebuild.controller.UserListController;
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.model.AvatarUrl;
import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.model.Block;
import com.example.neighbormaterebuild.model.Favorites;
import com.example.neighbormaterebuild.model.Keijiban;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Point;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.MessagePhotoViewActivity;
import com.example.neighbormaterebuild.ui.PhotoViewActivity;
import com.example.neighbormaterebuild.ui.SplashActivity;
import com.example.neighbormaterebuild.ui.chat.messages.ChatMessageActivity;
import com.example.neighbormaterebuild.ui.user.UserDetailActivity;
import com.example.neighbormaterebuild.utils.CacheImage;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeijibanDetailActivity extends AppCompatActivity implements ExampleBottomSheetDialog.BottomSheetListener {

    private Keijiban keijiban;
    private TextView tvUsername, tvAreaName, tvMessage;
    private ImageView imgAvatar;
    private LinearLayout layoutBtnSend, layoutBtnImage;
    private Toolbar toolbarKeijibanDetail;
    private CardView cardContainerUnlimit, cardContainerFavo, btn_bottom_sheet_dialog_fragment;
    private PointController pointController;
    private FavoriteController favoriteController;
    private LinearLayout layoutProfile;
    private CardView cardAvatar;
    private UserListController userListController;
    private ImageView imgUnLimit;
    private TextView tvUnLimit;
    private KeijibanController keijibanController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(KeijibanDetailActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keijiban_detail);

        keijibanController = new KeijibanController(Config.API_URL);
        keijiban = getIntent().getParcelableExtra("keijiban");
        pointController = new PointController(Config.API_URL);
        favoriteController = new FavoriteController(Config.API_URL);
        userListController = new UserListController(Config.API_URL);
        initView();
        getKeijibanDetail();
    }

    private void getKeijibanDetail() {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", "");
        keijibanController.getKeijibanDetail(token, keijiban.getId()).enqueue(new Callback<Keijiban.KeijibanDetail>() {
            @Override
            public void onResponse(@NotNull Call<Keijiban.KeijibanDetail> call, @NotNull Response<Keijiban.KeijibanDetail> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Keijiban.KeijibanDetail.setInstance(response.body());
                        updateData();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Keijiban.KeijibanDetail> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initView() {
        tvUsername = findViewById(R.id.tvUsername);
        layoutProfile = findViewById(R.id.layoutProfile);
        tvAreaName = findViewById(R.id.tvAreaName);
        //tvTimeMessage = findViewById(R.id.tvTimeMessage);
        tvMessage = findViewById(R.id.tvMessage);
        imgAvatar = findViewById(R.id.imgAvatar);
        cardContainerFavo = findViewById(R.id.cardContainerFavo);
        layoutBtnImage = findViewById(R.id.layoutBtnImage);
        btn_bottom_sheet_dialog_fragment = findViewById(R.id.btn_bottom_sheet_dialog_fragment);
        cardContainerUnlimit = findViewById(R.id.cardContainerUnlimit);
        layoutBtnSend = findViewById(R.id.layoutBtnSend);
        cardAvatar = findViewById(R.id.cardAvatar);
        imgUnLimit = findViewById(R.id.imgUnLimit);
        tvUnLimit = findViewById(R.id.tvUnLimit);

        toolbarKeijibanDetail = findViewById(R.id.toolbarKeijibanDetail);
        toolbarKeijibanDetail.setLogo(R.drawable.ic_back_svg);
        View view = toolbarKeijibanDetail.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (keijiban != null && keijiban.getDisplayname() != null)
            tvUsername.setText(keijiban.getDisplayname());

        if (keijiban != null && keijiban.getContent() != null)
            tvMessage.setText(keijiban.getContent());
    }

    private void updateData() {
        if (Keijiban.KeijibanDetail.getInstance().getKeijiban().getUnlimit_point() == 1) {
            imgUnLimit.setImageResource(R.drawable.ic_unlock_svg);
            tvUnLimit.setText("リミット解除中");
        } else {
            imgUnLimit.setImageResource(R.drawable.ic_lock_svg);
            tvUnLimit.setText("リミット解除");
        }

        layoutBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson g = new Gson();
                AvatarUrl[] a = g.fromJson(keijiban.getAvatar_url(), AvatarUrl[].class);
                Intent intent = new Intent(KeijibanDetailActivity.this, ChatMessageActivity.class);
                User user = new User();
                user.setAvatar_url(a[0].getPath());
                user.setImage(keijiban.getAvatar_url());
                user.setId(Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_id());
                user.setDisplayname(Keijiban.KeijibanDetail.getInstance().getKeijiban().getDisplayname());
                user.setUser_code(Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_code());
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        if (keijiban.getThumb() != null) {
            layoutBtnImage.setVisibility(View.VISIBLE);
            layoutBtnImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!PointStore.getInstance().isUserEnoughPoint(PointFee.viewKeijibanImage)) {
                        PointStore.getInstance().showDialogUserNotEnoughPoint(KeijibanDetailActivity.this);
                        return;
                    }
                    keijibanController.viewImageKeijiban(ProfileStore.getInstance().getUserLogin().getTokken(),
                            Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_id())
                            .enqueue(new Callback<BaseResponse>() {
                                @Override
                                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                    if (response.code() == 200) {
                                        PointStore.getInstance().consumePoint(PointFee.viewKeijibanImage);
                                        Intent intent = new Intent(KeijibanDetailActivity.this, MessagePhotoViewActivity.class);
                                        intent.putExtra("avatar_url", keijiban.getThumb().getPath());
                                        startActivity(intent);
                                    } else if (response.code() == 402)
                                        PointStore.getInstance().showDialogUserNotEnoughPoint(KeijibanDetailActivity.this);
                                }

                                @Override
                                public void onFailure(Call<BaseResponse> call, Throwable t) {
                                }
                            });

                }
            });
        } else layoutBtnImage.setVisibility(View.GONE);

        layoutProfile.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                //Log.i("TAG", "onclick！");
                if (!PointStore.getInstance().isUserEnoughPoint(PointFee.viewProfile)) {
                    PointStore.getInstance().showDialogUserNotEnoughPoint(KeijibanDetailActivity.this);
                    return;
                }

                SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
                final String token = sharedPref.getString("token", "");
                userListController.getUserProfile(token, Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_id()).enqueue(new Callback<User.UserResponse>() {
                    @Override
                    public void onResponse(Call<User.UserResponse> call, Response<User.UserResponse> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(KeijibanDetailActivity.this, UserDetailActivity.class);
                            intent.putExtra("user", response.body().getUser());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<User.UserResponse> call, Throwable t) {

                    }
                });
            }
        });

        tvUsername.setText(Keijiban.KeijibanDetail.getInstance().getKeijiban().getDisplayname());
        try {
            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getAge().size(); i++) {
                if (Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getValue().equals(Keijiban.KeijibanDetail.getInstance().getKeijiban().getAge())) {
                    tvAreaName.setText(Keijiban.KeijibanDetail.getInstance().getKeijiban().getArea_name() + " " + Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getName());
                    break;
                }
            }
        } catch (Exception e) {
        }

//        String date = null;
//        try {
//            date = FormatUtil.convertDatetoString(FormatUtil.convertStringtoDate(Keijiban.KeijibanDetail.getInstance().getKeijiban().getCreated_at()));
//            tvTimeMessage.setText(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        tvMessage.setText(Keijiban.KeijibanDetail.getInstance().getKeijiban().getContent());

        Gson g = new Gson();
        final AvatarUrl[] a = g.fromJson(keijiban.getAvatar_url(), AvatarUrl[].class);

        cardAvatar.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(KeijibanDetailActivity.this, PhotoViewActivity.class);
                intent.putExtra("avatar_url", a[0].getPath());
                startActivity(intent);
            }
        });

        try {
            RequestOptions sharedOptions =
                    new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_image_solid_svg);

            String avatarUrl = CacheImage.create(KeijibanDetailActivity.this, a[0].getPath());

            if (avatarUrl != null)
                Glide.with(KeijibanDetailActivity.this)
                        .load(avatarUrl)
                        .apply(sharedOptions)
                        .into(imgAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardContainerUnlimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
                final String token = sharedPref.getString("token", "");
                if (Keijiban.KeijibanDetail.getInstance().getKeijiban().getUnlimit_point() != 1)
                    pointController.getResultPointSetting(token).enqueue(new Callback<PointSetting.Response>() {
                        @Override
                        public void onResponse(Call<PointSetting.Response> call, Response<PointSetting.Response> response) {
                            if (response.isSuccessful()) {
                                try {
                                    PointStore.getInstance().setTotalPoint(response.body().getData().getTotalPoint());
                                    PointStore.getInstance().setPointSettings(response.body().getData().getPointList());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (PointStore.getInstance().getPointSettings() != null && PointStore.getInstance().isUserEnoughPoint(PointFee.unlimitPoint)) {
                                    if (KeijibanDetailActivity.this.hasWindowFocus())
                                        new android.app.AlertDialog.Builder(KeijibanDetailActivity.this)
                                                .setMessage(PointStore.getInstance().getPointUnLimit().getPoint() +
                                                        "pt でお相手へのメッセージ送信が永久無料で行えます。")
                                                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                        pointController.postUnLimit(token, Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_id(), Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_code()).enqueue(new Callback<Point.PointResponse>() {
                                                            @Override
                                                            public void onResponse(Call<Point.PointResponse> call, Response<Point.PointResponse> response) {
                                                                if (response.isSuccessful()) {
                                                                    PointStore.getInstance().consumePoint(PointFee.unlimitPoint);

                                                                    Keijiban.KeijibanDetail.getInstance().getKeijiban().setUnlimit_point(1);
                                                                    imgUnLimit.setImageResource(R.drawable.ic_unlock_svg);
                                                                    tvUnLimit.setText("リミット解除中");

                                                                    if (KeijibanDetailActivity.this.hasWindowFocus() && response.body() != null)
                                                                        new android.app.AlertDialog.Builder(KeijibanDetailActivity.this)
                                                                                .setTitle(response.body().getMessage())
                                                                                .setPositiveButton("はい", null)
                                                                                .show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Point.PointResponse> call, Throwable t) {

                                                            }
                                                        });
                                                    }
                                                })
                                                .setNegativeButton("いいえ", null)
                                                .show();
                                } else {
                                    PointStore.showDialogUnlimit(KeijibanDetailActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PointSetting.Response> call, Throwable t) {

                        }
                    });
            }
        });

        cardContainerFavo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
                String token = sharedPref.getString("token", "");
                if (Keijiban.KeijibanDetail.getInstance().getKeijiban() != null && (Keijiban.KeijibanDetail.getInstance().getKeijiban().getFavorite_status().equals("0"))) {
                    String userID = Keijiban.KeijibanDetail.getInstance().getKeijiban().getId();
                    if (Keijiban.KeijibanDetail.getInstance().getKeijiban().getId() == null ||
                            Keijiban.KeijibanDetail.getInstance().getKeijiban().getId().isEmpty())
                        userID = keijiban.getUser_id();
                    favoriteController.postFavorite(token, Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_code(), Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_id(), 1).enqueue(new Callback<Favorites>() {
                        @Override
                        public void onResponse(Call<Favorites> call, Response<Favorites> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (KeijibanDetailActivity.this.hasWindowFocus())
                                    new android.app.AlertDialog.Builder(KeijibanDetailActivity.this)
                                            .setMessage(response.body().getData())
                                            .setPositiveButton("はい", null)
                                            .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Favorites> call, Throwable t) {

                        }
                    });
                } else {
                    if (KeijibanDetailActivity.this.hasWindowFocus())
                        new android.app.AlertDialog.Builder(KeijibanDetailActivity.this)
                                .setMessage("お気に入りに追加しました")
                                .setPositiveButton("はい", null)
                                .show();
                }
            }
        });

        final ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog(KeijibanDetailActivity.this);

        btn_bottom_sheet_dialog_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });
    }

    @Override
    public void onButtonClickedLock(UserListController userListController) {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        userListController.userLock(token, Keijiban.KeijibanDetail.getInstance().getKeijiban().getId(), Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_code(), 1).enqueue(new Callback<Block>() {
            @Override
            public void onResponse(Call<Block> call, Response<Block> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (KeijibanDetailActivity.this.hasWindowFocus())
                        new AlertDialog.Builder(KeijibanDetailActivity.this)
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
    public void onButtonClickedReport(UserListController userListController, EditText edtReport, final Dialog dialog) {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        //Log.e("kiemtra", token);
        userListController.userReport(token, Keijiban.KeijibanDetail.getInstance().getKeijiban().getId(), Keijiban.KeijibanDetail.getInstance().getKeijiban().getUser_code(), edtReport.getText().toString().trim(), "profile").enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dialog.cancel();
                    if (KeijibanDetailActivity.this.hasWindowFocus())
                        new AlertDialog.Builder(KeijibanDetailActivity.this)
                                .setMessage(response.body().getMessage())
                                .setPositiveButton("はい", null)
                                .show();
                }
                //Log.e("kiemtra", response.code() + " " + response.message());
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
