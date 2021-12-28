package com.example.neighbormaterebuild.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.DataSource;
//import com.bumptech.glide.load.engine.GlideException;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.MyPageController;
import com.example.neighbormaterebuild.model.AvatarUrl;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.model.Utility;
import com.example.neighbormaterebuild.ui.ChangeDeviceMyProfileActivity;
import com.example.neighbormaterebuild.ui.ContactActivity;
import com.example.neighbormaterebuild.ui.PointActivity;
import com.example.neighbormaterebuild.ui.WebviewActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    private RecyclerView recycleProfileContainer;
    private RecyclerView.LayoutManager layoutManager;
    private List<Utility> utilityList;
    private ProfileAdapter profileAdapter;
    private Button btnEditProfile, btnPoint;
    private TextView tvUsername, tvPoint, tvAreaName, tvFormChangeDevice,
            tvCompany, tvTermofService, tvPolicy, tvQuestion, tvContactUs;
    private ImageView imgAvatar;
    private MyPageController myPageController;
    private CardView cardAvatar;
    private Intent i;
    private ProgressBar pb_load_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myPageController = new MyPageController(Config.API_URL);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCompany = view.findViewById(R.id.tvCompany);
        tvTermofService = view.findViewById(R.id.tvTermofService);
        tvPolicy = view.findViewById(R.id.tvPolicy);
        tvQuestion = view.findViewById(R.id.tvQuestion);
        tvContactUs = view.findViewById(R.id.tvContactUs);
        tvFormChangeDevice = view.findViewById(R.id.tvFormChangeDevice);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnPoint = view.findViewById(R.id.btnPoint);
        tvAreaName = view.findViewById(R.id.tvAreaName);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        pb_load_image = view.findViewById(R.id.pb_load_image);
        tvPoint = view.findViewById(R.id.tvPoint);
        recycleProfileContainer = view.findViewById(R.id.recycleProfileContainer);
        cardAvatar = view.findViewById(R.id.cardAvatar);

        tvCompany.setOnClickListener(this);
        tvTermofService.setOnClickListener(this);
        tvPolicy.setOnClickListener(this);
        tvQuestion.setOnClickListener(this);
        tvContactUs.setOnClickListener(this);
        tvFormChangeDevice.setOnClickListener(this);

    }

    private void init() {
        layoutManager = new GridLayoutManager(getActivity(), 3);
        recycleProfileContainer.setLayoutManager(layoutManager);
        utilityList = new ArrayList<>();
        utilityList.add(new Utility(R.drawable.ic_setting_svg, "設定"));
        utilityList.add(new Utility(R.drawable.ic_list_svg, "ポイント表"));
        utilityList.add(new Utility(R.drawable.ic_star_svg, "お気に入り"));
        utilityList.add(new Utility(R.drawable.ic_foot_svg, "足あと"));
        utilityList.add(new Utility(R.drawable.ic_notification_svg, "お知らせ"));
        utilityList.add(new Utility(R.drawable.ic_volume_svg, "キャンペーン"));
        profileAdapter = new ProfileAdapter(getActivity(), utilityList);
        recycleProfileContainer.setAdapter(profileAdapter);

        btnEditProfile.setOnClickListener(this);
        btnPoint.setOnClickListener(this);

        tvFormChangeDevice.setOnClickListener(this);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        myPageController.getUserProfile(token).enqueue(new Callback<User.UserResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<User.UserResponse> call, Response<User.UserResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                        tvUsername.setText(response.body().getUser().getDisplayname());
                        System.out.println("Check point::: " + response.body().getUser().getPoint());
                        tvPoint.setText(response.body().getUser().getPoint());
                        for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getAge().size(); i++) {
                            if (Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getValue().equals(response.body().getUser().getAge())) {
                                tvAreaName.setText(response.body().getUser().getArea_name() + " " +
                                        Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getName());
                                break;
                            }
                        }
                        try {
                            Gson g = new Gson();
                            AvatarUrl[] a = g.fromJson(response.body().getUser().getImage(), AvatarUrl[].class);
                            if (getActivity() == null) {
                                return;
                            }
                            Glide.with(getActivity())
                                    .load(a[0].getPath())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e,
                                                                    Object model,
                                                                    Target<Drawable> target,
                                                                    boolean isFirstResource
                                        ) {
                                            pb_load_image.setVisibility(View.VISIBLE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource,
                                                                       Object model,
                                                                       Target<Drawable> target,
                                                                       DataSource dataSource,
                                                                       boolean isFirstResource
                                        ) {
                                            pb_load_image.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(imgAvatar);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int cellWidth = imgAvatar.getWidth();
                        imgAvatar.getLayoutParams().height = cellWidth;
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<User.UserResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEditProfile:
                i = new Intent(getContext(), EditProfileActivity.class);
                startActivity(i);
                break;
            case R.id.btnPoint:
                i = new Intent(getContext(), PointActivity.class);
                startActivity(i);
                break;
            case R.id.tvFormChangeDevice:
                i = new Intent(getContext(), ChangeDeviceMyProfileActivity.class);
                startActivity(i);
                break;
            case R.id.tvPolicy:
                Intent intentP = new Intent(getContext(), WebviewActivity.class);
                intentP.putExtra("url", "privacy_policy");
                intentP.putExtra("title", "プライバシーポリシー");
                startActivity(intentP);
                break;
            case R.id.tvQuestion:
                Intent intentQ = new Intent(getContext(), WebviewActivity.class);
                intentQ.putExtra("url", "help_page");
                intentQ.putExtra("title", "よくあるお問い合わせ");
                startActivity(intentQ);
                break;
            case R.id.tvContactUs:
                Intent intentC = new Intent(getContext(), ContactActivity.class);
                startActivity(intentC);
                break;
            case R.id.tvCompany:
                Intent intentCompany = new Intent(getContext(), WebviewActivity.class);
                intentCompany.putExtra("url", "company_page");
                intentCompany.putExtra("title", "会社概要");
                startActivity(intentCompany);
                break;
            case R.id.tvTermofService:
                Intent intentT = new Intent(getContext(), WebviewActivity.class);
                intentT.putExtra("url", "security_page");
                intentT.putExtra("title", "利用規約");
                startActivity(intentT);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        pb_load_image.setVisibility(View.VISIBLE);
        imgAvatar.setImageDrawable(null);
        init();
    }
}