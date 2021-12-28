package com.example.neighbormaterebuild.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.LoginController;
import com.example.neighbormaterebuild.controller.MyPageController;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.utils.FileDeviceID;
import com.example.neighbormaterebuild.utils.FileService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeDeviceRegisterActivity extends AppCompatActivity {

    private Toolbar toolbarChangeDevice;
    private Button btnSubmit;
    private EditText edtEmail, edtPassword;
    private MyPageController myPageController;
    private LoginController loginController;
    private FileService fileService;
    private FileDeviceID fileDeviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_device_register);

//        fileService = new FileService(this);
        fileDeviceID = new FileDeviceID(this);
        initView();
    }

    private void initView() {
        toolbarChangeDevice = findViewById(R.id.toolbarChangeDevice);
        btnSubmit = findViewById(R.id.btnSubmit);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        toolbarChangeDevice.setLogo(R.drawable.ic_back_svg);

        myPageController = new MyPageController(Config.API_URL);
        loginController = new LoginController(Config.API_URL, ChangeDeviceRegisterActivity.this);

        View viewType = toolbarChangeDevice.getChildAt(1);
        viewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmail.getText().toString().trim().isEmpty() || edtPassword.getText().toString().trim().isEmpty()) {
                } else {
                    myPageController.postUserDeviceTransfer(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim()).enqueue(new Callback<User.UserTranfer>() {
                        @Override
                        public void onResponse(Call<User.UserTranfer> call, Response<User.UserTranfer> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getTranfer() != null) {
                                    //Log.e("kiemtra", response.body() + " ");
                                    try {
//                                        fileService.writeFile(HttpHeaders.getInstance().getDeviceID(), response.body().getTranfer().getUser().getUser_code(), response.body().getTranfer().getUser().getDevice_password());
                                        fileDeviceID.writeDeviceID();
                                        loginController.checkLogin(response.body().getTranfer().getUser());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (ChangeDeviceRegisterActivity.this.hasWindowFocus())
                                                new AlertDialog.Builder(ChangeDeviceRegisterActivity.this)
                                                        .setMessage("メールアドレスまたはパスワードが正しくありません。")
                                                        .setPositiveButton("閉じる", null)
                                                        .show();
                                        }
                                    });
                                }
                            }
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<User.UserTranfer>() {
//                            }.getType();
//                            User.UserTranfer errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            //Log.e("ChangeDevice::::", response.code() + " " + response.message());
                        }

                        @Override
                        public void onFailure(Call<User.UserTranfer> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}
