package com.example.neighbormaterebuild.ui.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.ui.search.complete.SearchFormFragment;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbarSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
        SearchFormFragment fragment = new SearchFormFragment();
        fragmentTransaction.add(R.id.fragmentSearchForm, fragment);
        fragmentTransaction.commit();
    }
}