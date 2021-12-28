package com.example.neighbormaterebuild.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.MyPageController;
import com.example.neighbormaterebuild.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeDeviceMyProfileActivity extends AppCompatActivity {

    private Toolbar toolbarChangeDevice;
    private Button btnSubmit;
    private EditText edtEmail, edtPassword;
    private MyPageController myPageController;
//    private FileService fileService;
//    private FileDeviceID fileDeviceID;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_device_my_profile);

        initView();
    }

    private void initView() {
        toolbarChangeDevice = findViewById(R.id.toolbarChangeDevice);
        btnSubmit = findViewById(R.id.btnSubmit);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        toolbarChangeDevice.setLogo(R.drawable.ic_back_svg);

        myPageController = new MyPageController(Config.API_URL);

        View viewType = toolbarChangeDevice.getChildAt(1);
        viewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        fileService = new FileService(ChangeDeviceMyProfileActivity.this);
//        fileDeviceID = new FileDeviceID(ChangeDeviceMyProfileActivity.this);
        final String[] lines;
//        if (fileDeviceID.readDeviceID() != null) {
////            String text = fileDeviceID.readDeviceID();
////            lines = text.split("\\n");
//            btnSubmit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (edtEmail.getText().toString().trim().isEmpty() || edtPassword.getText().toString().trim().isEmpty()) {
//                    } else {
//                        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
//                        String token = sharedPref.getString("token", "");
//                        myPageController.postUserDevicePrepare(edtEmail.getText().toString().trim(),
//                                edtPassword.getText().toString().trim(), "aaa", token).enqueue(new Callback<User.UserTranfer>() {
//                            @Override
//                            public void onResponse(Call<User.UserTranfer> call, final Response<User.UserTranfer> response) {
//                                if (response.isSuccessful() && response.body() != null) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (ChangeDeviceMyProfileActivity.this.hasWindowFocus())
//                                                new AlertDialog.Builder(ChangeDeviceMyProfileActivity.this)
//                                                        .setMessage(response.body().getMessage())
//                                                        .setPositiveButton("閉じる", null)
//                                                        .show();
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<User.UserTranfer> call, Throwable t) {
//
//                            }
//                        });
//                    }
//                }
//            });
//        }
//        else {
//            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i);
//            finish();
//        }
    }
}
