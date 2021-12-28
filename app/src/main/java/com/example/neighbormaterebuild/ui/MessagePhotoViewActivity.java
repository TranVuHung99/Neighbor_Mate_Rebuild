package com.example.neighbormaterebuild.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

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
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.network.HttpHeaders;
import com.example.neighbormaterebuild.utils.CacheImage;
import com.github.chrisbanes.photoview.PhotoView;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import java.util.HashMap;
import java.util.Map;

public class MessagePhotoViewActivity extends SwipeBackActivity {

    private static final String TAG = "MessagePhotoViewActivity";
    public final ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog(MessagePhotoViewActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_photo_view);
        setDragEdge(SwipeBackLayout.DragEdge.BOTTOM);

        Bundle bundle = getIntent().getExtras();
        String linkimg = bundle.getString("avatar_url");
        final boolean consumePoint = bundle.getBoolean("consumePoint");

        final ProgressBar progressBar = findViewById(R.id.progressLoading);
        PhotoView photoView = findViewById(R.id.photo_view);


        try {

            String avatarUrl = CacheImage.create(MessagePhotoViewActivity.this, linkimg);

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
                Glide.with(MessagePhotoViewActivity.this)
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
//                                    PointStore.getInstance().consumePoint(PointFee.viewImageChatting);
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

    }

}
