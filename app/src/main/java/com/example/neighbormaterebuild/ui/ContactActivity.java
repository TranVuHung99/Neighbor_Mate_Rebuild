package com.example.neighbormaterebuild.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.network.SocketClient;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.google.android.material.appbar.MaterialToolbar;

public class ContactActivity extends AppCompatActivity {

    private EditText edtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(ContactActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView btnSend = findViewById(R.id.btnSend);
        edtContent = findViewById(R.id.edtContent);

        toolbar.setNavigationIcon(R.drawable.ic_back_svg);

        View view = toolbar.getChildAt(1);

        view.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                finish();
            }
        });

        //hide keyboard
        final LinearLayout actionHide = findViewById(R.id.wrapper);
        actionHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionHide.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtContent.getWindowToken(), 0);
            }
        });

        btnSend.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String text = edtContent.getText().toString();
                String supportID = Metadata.getInstance().getData().getSupporter().getId();
                //Log.e("tab", text);
                if (!text.trim().isEmpty()) {
                    //Log.e("tab", text);
                    SocketClient.getInstance().sendMessage(text.trim(), supportID, "contact");
                    AlertDialog alertDialog = new AlertDialog.Builder(ContactActivity.this)
                            .setMessage("お問い合わせのメッセージを送信しました。返信が来るまで少々お待ち下さい。")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    edtContent.setText("");
                                }
                            })
                            .create();
                    alertDialog.show();
                }
            }
        });
    }
}
