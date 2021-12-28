package com.example.neighbormaterebuild.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.controller.UserListController;
import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.ui.PhotoViewActivity;
import com.example.neighbormaterebuild.utils.CacheImage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlideImageUserFragment extends Fragment {

    private static final String TAG = "SlideImageUserFragment";
    private ImageView imgContainerSlider;

    private UserListController userListController = new UserListController(Config.API_URL);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slide_image_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgContainerSlider = view.findViewById(R.id.imgContainerSlider);
        final Bundle bundle = getArguments();
        final String linkimg = bundle.getString("avatar_url");
        final int index = bundle.getInt("index");
        final String userId = bundle.getString("userId");
        final String karaId = bundle.getString("karaId");
        final String user_code = bundle.getString("user_code");
        final String user_id = bundle.getString("user_id");

        Log.e(TAG, "User_code" + user_code + "id" + user_id);

        setImg(imgContainerSlider, linkimg);
        imgContainerSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slug;
                if (index == 0) slug = PointFee.viewImageProfile_1;
                else if (index == 1) slug = PointFee.viewImageProfile_2;
                else slug = PointFee.viewImageProfile_3;


                if (!PointStore.getInstance().isUserEnoughPoint(slug)) {
                    PointStore.getInstance().showDialogUserNotEnoughPoint(getActivity());
                    return;
                }

                userListController.viewImageProfile(userId, karaId, index + 1).enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.code() == 200) {
                            PointStore.getInstance().consumePoint(slug);
                            Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
                            intent.putExtra("avatar_url", linkimg);
                            intent.putExtra("user_code", user_code);
                            intent.putExtra("user_id", user_id);
                            startActivity(intent);
                        } else if (response.code() == 402){}
                            PointStore.getInstance().showDialogUserNotEnoughPoint(getActivity());
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void setImg(final ImageView img, String url) {
        RequestOptions sharedOptions =
                new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(R.drawable.ic_image_solid_svg);

        String avatarUrl = CacheImage.create(getContext(), url);

        if (url != null)
            Glide.with(getContext())
                    .load(avatarUrl)
                    .apply(sharedOptions)
                    .into(img);

    }
}
