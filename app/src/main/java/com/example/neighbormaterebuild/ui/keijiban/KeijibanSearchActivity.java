package com.example.neighbormaterebuild.ui.keijiban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;
import com.example.neighbormaterebuild.ui.keijiban.complete.KeijibanSearchFormFragment;

public class KeijibanSearchActivity extends AppCompatActivity {

    private Toolbar toolbarSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(KeijibanSearchActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keijiban_search);
        initView();
    }

    private void initView() {
        toolbarSearch = findViewById(R.id.toolbarSearch);
        toolbarSearch.setLogo(R.drawable.ic_close_svg);
        View view = toolbarSearch.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        KeijibanSearchFormFragment fragment = new KeijibanSearchFormFragment();
        fragmentTransaction.add(R.id.fragmentSearchForm, fragment);
        fragmentTransaction.commit();
    }
}
