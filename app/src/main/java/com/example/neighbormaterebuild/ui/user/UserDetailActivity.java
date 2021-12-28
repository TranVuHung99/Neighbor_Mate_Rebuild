package com.example.neighbormaterebuild.ui.user;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.comm.ExampleBottomSheetDialog;
import com.example.neighbormaterebuild.comm.SlideAdapter;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.controller.FavoriteController;
import com.example.neighbormaterebuild.controller.MetadataController;
import com.example.neighbormaterebuild.controller.PointController;
import com.example.neighbormaterebuild.controller.UserListController;
import com.example.neighbormaterebuild.model.AvatarUrl;
import com.example.neighbormaterebuild.model.Block;
import com.example.neighbormaterebuild.model.Favorites;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Point;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.PhotoViewActivity;
import com.example.neighbormaterebuild.ui.SplashActivity;
import com.example.neighbormaterebuild.ui.chat.messages.ChatMessageActivity;
import com.example.neighbormaterebuild.utils.CacheImage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity implements ExampleBottomSheetDialog.BottomSheetListener {

    private static final String TAG = "UserDetailActivity";

    public User user;
    public String user2;
    private TextView tvAge, tvArea, tvHeight, tvStyle, tvIncome, tvJob, tvDisplayname, tvSex, tvContent, toolbar_title;
    private Toolbar toolbarUserDetail;
    private MetadataController metadataController;
    private LinearLayout layoutBtnSend;
    private ViewPager slideImageAvatar;
    private List<Fragment> fragmentList;
    private UserListController userListController;
    private CircleImageView image1, image2;
    private FavoriteController favoriteController;
    private CardView cardContainerFavo;
    private ImageButton imgbtnBack, imgbtnNext;
    private int position = 0;
    private CardView btn_bottom_sheet_dialog_fragment, cardUnlimit;
    private PointController pointController;
    private String token;
    private ImageView imgUnLimit;
    private TextView tvUnLimit;
    public String id;
    public String id2;

    public final ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog(UserDetailActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(UserDetailActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        user = getIntent().getParcelableExtra("user");
        if (user == null) {
            finish();
            return;
        }
        if (user.getId() == null) {
            id = user.getUser_id();
        } else {
            id = user.getId();
        }

        user2 = user.getUser_code();
        id2 = id;


        Log.e(TAG, "User code :: " + user.getUser_code() + "id" + id);
        userListController = new UserListController(Config.API_URL);
        favoriteController = new FavoriteController(Config.API_URL);
        pointController = new PointController(Config.API_URL);
        metadataController = new MetadataController(Config.API_URL);

        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        token = sharedPref.getString("token", "");

        initView();
    }

    private void initView() {
        imgbtnBack = findViewById(R.id.imgbtnBack);
        imgbtnNext = findViewById(R.id.imgbtnNext);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        layoutBtnSend = findViewById(R.id.layoutBtnSend);
        tvDisplayname = findViewById(R.id.tvDisplayname);
        slideImageAvatar = findViewById(R.id.slideImageAvatar);
        btn_bottom_sheet_dialog_fragment = findViewById(R.id.btn_bottom_sheet_dialog_fragment);
        cardContainerFavo = findViewById(R.id.cardContainerFavo);
        cardUnlimit = findViewById(R.id.cardUnlimit);
        tvAge = findViewById(R.id.tvAge);
        tvArea = findViewById(R.id.tvArea);
        tvHeight = findViewById(R.id.tvHeight);
        tvStyle = findViewById(R.id.tvStyle);
        tvIncome = findViewById(R.id.tvIncome);
        tvJob = findViewById(R.id.tvJob);
        tvSex = findViewById(R.id.tvSex);
        tvContent = findViewById(R.id.tvContent);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(user.getDisplayname());
        toolbarUserDetail = findViewById(R.id.toolbarUserDetail);
        toolbarUserDetail.setLogo(R.drawable.ic_back_svg);
        imgUnLimit = findViewById(R.id.imgUnLimit);
        tvUnLimit = findViewById(R.id.tvUnLimit);

        btn_bottom_sheet_dialog_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });


        layoutBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatMessageActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        cardUnlimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getUnlimit_point() != 1)
                    pointController.getResultPointSetting(token).enqueue(new Callback<PointSetting.Response>() {
                        @Override
                        public void onResponse(Call<PointSetting.Response> call, Response<PointSetting.Response> response) {
                            try {
                                if (response.isSuccessful())
                                    if (PointStore.getInstance().getPointSettings() != null && PointStore.getInstance().isUserEnoughPoint(PointFee.unlimitPoint)) {
                                        if (UserDetailActivity.this.hasWindowFocus())
                                            new android.app.AlertDialog.Builder(UserDetailActivity.this)
                                                    .setMessage(PointStore.getInstance().getPointUnLimit().getPoint() +
                                                            "pt でお相手へのメッセージ送信が永久無料で行えます。")
                                                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                            pointController.postUnLimit(token, id, user.getUser_code()).enqueue(new Callback<Point.PointResponse>() {
                                                                @Override
                                                                public void onResponse(Call<Point.PointResponse> call, Response<Point.PointResponse> response) {
                                                                    //Log.e(TAG, call.request().toString());
                                                                    //Log.e(TAG, response.body().toString());

                                                                    if (response.isSuccessful()) {
                                                                        user.setUnlimit_point(1);
                                                                        imgUnLimit.setImageResource(R.drawable.ic_unlock_svg);
                                                                        tvUnLimit.setText("リミット解除中");

                                                                        PointStore.getInstance().consumePoint(PointFee.unlimitPoint);
                                                                        if (UserDetailActivity.this.hasWindowFocus())
                                                                            new android.app.AlertDialog.Builder(UserDetailActivity.this)
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
                                        PointStore.showDialogUnlimit(UserDetailActivity.this);
                                    }
                            } catch (Exception ignored) {
                            }

                        }

                        @Override
                        public void onFailure(Call<PointSetting.Response> call, Throwable t) {

                        }
                    });

            }
        });

        View viewType = toolbarUserDetail.getChildAt(1);
        viewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pointController.getResultPointSetting(token).enqueue(new Callback<PointSetting.Response>() {
            @Override
            public void onResponse(Call<PointSetting.Response> call, Response<PointSetting.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        PointStore.getInstance().setTotalPoint(response.body().getData().getTotalPoint());
                        PointStore.getInstance().setPointSettings(response.body().getData().getPointList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                userListController.getUserProfile(token, id).enqueue(new Callback<User.UserResponse>() {
                    @Override
                    public void onResponse(Call<User.UserResponse> call, Response<User.UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                            user = response.body().getUser();

                            tvDisplayname.setText(user.getDisplayname());
                            if (user.getUser_status() != null && !user.getUser_status().isEmpty())
                                tvContent.setText(user.getUser_status());
                            else tvContent.setText("よろしくお願いします。");
                            tvArea.setText(user.getArea_name() + "\t" + user.getCity_name());

                            //Log.e("UserDetail", UserDetailActivity.this.user + "");
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getSex().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getSex().get(i).getValue().equals(user.getSex())) {
                                    tvSex.setText(Metadata.getInstance().getData().getUserProfileList().getSex().get(i).getName());
                                    break;
                                }
                            }
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getAge().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getValue().equals(user.getAge())) {
                                    tvAge.setText(Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getName());
                                    break;
                                } else {
                                    tvAge.setText(Metadata.getInstance().getData().getProfile_not_set());
                                }
                            }
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getHeight().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getHeight().get(i).getValue().equals(user.getHeight())) {
                                    tvHeight.setText(Metadata.getInstance().getData().getUserProfileList().getHeight().get(i).getName());
                                    break;
                                } else {
                                    tvHeight.setText(Metadata.getInstance().getData().getProfile_not_set());
                                }
                            }
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getJob().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getJob().get(i).getValue().equals(user.getJob())) {
                                    tvJob.setText(Metadata.getInstance().getData().getUserProfileList().getJob().get(i).getName());
                                    break;
                                } else {
                                    tvJob.setText(Metadata.getInstance().getData().getProfile_not_set());
                                }
                            }
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getIncome().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getIncome().get(i).getValue().equals(user.getIncome())) {
                                    tvIncome.setText(Metadata.getInstance().getData().getUserProfileList().getIncome().get(i).getName());
                                    break;
                                } else {
                                    tvIncome.setText(Metadata.getInstance().getData().getProfile_not_set());
                                }
                            }
                            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getStyle().size(); i++) {
                                if (Metadata.getInstance().getData().getUserProfileList().getStyle().get(i).getValue().equals(user.getStyle())) {
                                    tvStyle.setText(Metadata.getInstance().getData().getUserProfileList().getStyle().get(i).getName());
                                    break;
                                } else {
                                    tvStyle.setText(Metadata.getInstance().getData().getProfile_not_set());
                                }
                            }

                            Gson g = new Gson();
                            final AvatarUrl[] a = g.fromJson(user.getImage(), AvatarUrl[].class);
                            bindDataToSlide(a);

                            if (UserDetailActivity.this.user.getUnlimit_point() == 1) {
                                imgUnLimit.setImageResource(R.drawable.ic_unlock_svg);
                                tvUnLimit.setText("リミット解除中");
                            } else {
                                imgUnLimit.setImageResource(R.drawable.ic_lock_svg);
                                tvUnLimit.setText("リミット解除");
                            }


                            cardContainerFavo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (user.getFavorite_status().equals("0")) {
                                        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
                                        String token = sharedPref.getString("token", "");

                                        favoriteController.postFavorite(token, UserDetailActivity.this.user.getUser_code(), id, 1).enqueue(new Callback<Favorites>() {
                                            @Override
                                            public void onResponse(Call<Favorites> call, Response<Favorites> response) {
                                                if (response.isSuccessful()) {
                                                    String message = "お気に入りに追加しました";
                                                    try {
                                                        user.setFavorite_status("1");
                                                        message = response.body().getData();
                                                        if (UserDetailActivity.this.hasWindowFocus())
                                                            new AlertDialog.Builder(UserDetailActivity.this)
                                                                    .setMessage(message)
                                                                    .setPositiveButton("はい", null)
                                                                    .show();
                                                    } catch (Exception e) {
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Favorites> call, Throwable t) {

                                            }
                                        });
                                    } else {
                                        new AlertDialog.Builder(UserDetailActivity.this)
                                                .setMessage("お気に入りに追加しました")
                                                .setPositiveButton("はい", null)
                                                .show();
                                    }
                                }
                            });

                        } else if (response.code() == 411 && response.body() != null) {
                            try {
                                String message = "エラーです";
                                if (UserDetailActivity.this.hasWindowFocus()) {
                                    message = response.body().getMessage();
                                    new AlertDialog.Builder(UserDetailActivity.this)
                                            .setMessage(message)
                                            .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                    finish();
                                                }
                                            }).setCancelable(false)
                                            .show();
                                }
                            } catch (Exception e) {
                            }
                        } else {
                            if (UserDetailActivity.this.hasWindowFocus())
                                new AlertDialog.Builder(UserDetailActivity.this)
                                        .setMessage("エラーです")
                                        .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                                finish();
                                            }
                                        }).setCancelable(false)
                                        .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User.UserResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailure(Call<PointSetting.Response> call, Throwable t) {

            }
        });

        tvDisplayname.setText(user.getDisplayname());
        tvContent.setText(user.getUser_status());
        tvArea.setText(user.getArea_name() + "\t" + user.getCity_name());
    }

    private void bindDataToSlide(final AvatarUrl[] linkImg) {
        setImage(linkImg[1], image1);
        setImage(linkImg[2], image2);
        imgbtnNext.setVisibility(View.VISIBLE);
        fragmentList = new ArrayList<>();
        for (int i = 0; i < linkImg.length; i++) {
            SlideImageUserFragment slideImageUserFragment = new SlideImageUserFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            bundle.putString("avatar_url", linkImg[i].getPath());
            bundle.putString("userId", ProfileStore.getInstance().getUserLogin().getId());
            bundle.putString("karaId", user.getId());
            bundle.putString("user_code", user.getUser_code());
            bundle.putString("user_id", id);
            slideImageUserFragment.setArguments(bundle);
            fragmentList.add(slideImageUserFragment);
        }
        SlideAdapter adapter = new SlideAdapter(getSupportFragmentManager(), fragmentList);
        slideImageAvatar.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        imgbtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideImageAvatar.setCurrentItem(getPosition() + 1);
            }
        });
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideImageAvatar.setCurrentItem(getPosition() - 1);
            }
        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPosition() == 0 || getPosition() == 1) {
                    slideImageAvatar.setCurrentItem(getPosition() + 1);
                } else {
                    slideImageAvatar.setCurrentItem(getPosition() - 2);
                }
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPosition() == 0) {
                    slideImageAvatar.setCurrentItem(getPosition() + 2);
                } else {
                    slideImageAvatar.setCurrentItem(getPosition() - 1);
                }
            }
        });

        slideImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UserDetailActivity.this, PhotoViewActivity.class);
                intent.putExtra("user_code", user2);
                intent.putExtra("id", id2);
                startActivity(intent);
            }
        });

        slideImageAvatar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setImage(linkImg[1], image1);
                    setImage(linkImg[2], image2);
                    imgbtnNext.setVisibility(View.VISIBLE);
                    imgbtnBack.setVisibility(View.GONE);
                } else if (position == 1) {
                    imgbtnBack.setVisibility(View.VISIBLE);
                    imgbtnNext.setVisibility(View.VISIBLE);
                    setImage(linkImg[2], image1);
                    setImage(linkImg[0], image2);
                } else {
                    setImage(linkImg[0], image1);
                    setImage(linkImg[1], image2);
                    imgbtnNext.setVisibility(View.GONE);
                    imgbtnBack.setVisibility(View.VISIBLE);
                }

                setPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setPosition(int position) {
        this.position = position;
    }

    private int getPosition() {
        return position;
    }

    private void setImage(AvatarUrl url, CircleImageView imageView) {
        try {
            RequestOptions sharedOptions =
                    new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_image_solid_svg);

            String avatarUrl = CacheImage.create(UserDetailActivity.this, url.getPath());

            if (avatarUrl != null)
                Glide.with(UserDetailActivity.this)
                        .load(avatarUrl)
                        .apply(sharedOptions)
                        .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonClickedLock(UserListController userListController) {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        Log.e(TAG, "User code lock :: " + user.getUser_code() + id);
        userListController.userLock(token, id, user.getUser_code(), 1).enqueue(new Callback<Block>() {
            @Override
            public void onResponse(Call<Block> call, Response<Block> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (UserDetailActivity.this.hasWindowFocus())
                        new AlertDialog.Builder(UserDetailActivity.this)
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
        Log.e(TAG, "User code report :: " + user.getUser_code() + id);
        userListController.userReport(token, id, user.getUser_code(), edtReport.getText().toString().trim(), "profile").enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dialog.cancel();
                    if (UserDetailActivity.this.hasWindowFocus())
                        new AlertDialog.Builder(UserDetailActivity.this)
                                .setMessage(response.body().getMessage())
                                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogC, int which) {
                                        dialogC.cancel();
                                    }
                                })
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
