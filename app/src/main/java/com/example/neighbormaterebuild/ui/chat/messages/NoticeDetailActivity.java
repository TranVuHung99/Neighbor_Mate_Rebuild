package com.example.neighbormaterebuild.ui.chat.messages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.NoticeController;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Notice;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeDetailActivity extends AppCompatActivity {

    private static final String TAG = "NoticeDetailActivity";

    private Toolbar toolbarMessage;
    private Notice notice;
    private ImageView imgAvatar;
    private TextView txtMessage, txtTime;

    private NoticeController noticeController;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(NoticeDetailActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        notice = getIntent().getParcelableExtra("notice");

        //Log.e(TAG, notice.getImages() + "");

        noticeController = new NoticeController(Config.API_URL);

        initView();
        getDetail();
    }

    private void initView() {
        toolbarMessage = findViewById(R.id.toolbarMessage);
        toolbarMessage.setLogo(R.drawable.ic_back_svg);
        toolbarMessage.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbarMessage.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        imgAvatar = findViewById(R.id.imgAvatar);
        txtMessage = findViewById(R.id.txtMessage);
        txtTime = findViewById(R.id.txtTime);

        ConstraintLayout viewBubble = findViewById(R.id.viewBubble);
        int containerWidth = getResources().getDisplayMetrics().widthPixels;
        viewBubble.setMaxWidth((int) (0.8 * containerWidth));

        if (notice.getContent() != null) txtMessage.setText(notice.getContent());
        if (notice.getCreated_at() != null) txtTime.setText(notice.getCreated_at());

        if (notice.getImages() != null)
            Glide.with(this).load(notice.getImagesPath()[0].getPath())
                    .centerCrop()
                    .into(imgAvatar);
    }

    private void getDetail() {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        noticeController.getDetail(token, notice.getId(), notice.getNotice_done_id(), notice.getSchedule_send_id())
                .enqueue(new Callback<Notice.ResponseDetail>() {
                    @Override
                    public void onResponse(Call<Notice.ResponseDetail> call, Response<Notice.ResponseDetail> response) {
                    }

                    @Override
                    public void onFailure(Call<Notice.ResponseDetail> call, Throwable t) {

                    }
                });
    }
}
