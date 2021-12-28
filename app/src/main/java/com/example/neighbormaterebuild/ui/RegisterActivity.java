package com.example.neighbormaterebuild.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.comm.RandomPass;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.MetadataController;
import com.example.neighbormaterebuild.controller.RegionController;
import com.example.neighbormaterebuild.controller.RegisterController;
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Region;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.model.Util;
import com.example.neighbormaterebuild.network.NetWorkCall;
import com.example.neighbormaterebuild.utils.CShowProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;
    private static final String LOG_TAG = "ExternalStorageDemo";

    private SpinnerAdapter spinnerAdapter;

    private MetadataController metadataController;
    private Spinner spinnerGender, spinnerAge, spinnerArea, spinnerCityRegion;
    private EditText edtDisplayname;
    private Button btnSubmitRegister;
    private RegisterController registerController;
    private RegionController regionController;
    private TextView layoutWeb;

    //get profile user
    private User user;
    private String displayname, password, sex;

    private CheckBox checkBox, checkBox2;
    private Toolbar toolbarRegister;

    private List<Util> genderData = new ArrayList<>();
    private List<Util> ageData = new ArrayList<>();
    private List<Util> areaData = new ArrayList<>();
    private List<Util> cityData = new ArrayList<>();

    private ArrayAdapter<Util> cityAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        user = new User();

        metadataController = new MetadataController(Config.API_URL);
        registerController = new RegisterController(Config.API_URL, this);
        regionController = new RegionController(Config.API_URL, this);
        try {
            onSuccessCallMetadata();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        btnSubmitRegister = findViewById(R.id.btnSubmitRegister);
        checkBox = findViewById(R.id.checkBox);
        checkBox2 = findViewById(R.id.checkBox2);
        edtDisplayname = findViewById(R.id.edtDisplayname);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerAge = findViewById(R.id.spinnerAge);
        spinnerArea = findViewById(R.id.spinnerArea);
        spinnerCityRegion = findViewById(R.id.spinnerCityRegion);
        layoutWeb = findViewById(R.id.layoutWeb);

        layoutWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, WebviewActivity.class);
                intent.putExtra("url", "security_page");
                intent.putExtra("title", "利用規約");
                startActivity(intent);
            }
        });

        List<Util> cityList = new ArrayList<>();
        cityList.add(new Util("", "市区町村", ""));
        edtDisplayname = findViewById(R.id.edtDisplayname);
        toolbarRegister = findViewById(R.id.toolbarRegister);

        toolbarRegister.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), ChangeDeviceRegisterActivity.class);
                startActivity(intent);
                return false;
            }
        });

        btnSubmitRegister.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegisterActivity.this);
                if (spinnerGender.getSelectedItem() == null || spinnerAge.getSelectedItem() == null
                        || spinnerArea.getSelectedItem() == null || spinnerCityRegion.getSelectedItem() == null
                        || spinnerArea.getSelectedItem() == null) {
                    Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                if (edtDisplayname.getText().toString().trim().isEmpty()) {
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (spinnerGender.getSelectedItem().toString().trim().equals("性別")) {
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (spinnerAge.getSelectedItem().toString().trim().equals("年代")) {
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (spinnerArea.getSelectedItem().toString().trim().equals("都道府県")) {
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (spinnerCityRegion.getSelectedItem().toString().trim().equals("市区町村")) {
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (!checkBox.isChecked()) {
                    alertBuilder.setMessage("18歳以上ですか？");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (!checkBox2.isChecked()) {
                    alertBuilder.setMessage("利用規約に同意する必要があります。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else {
                    displayname = edtDisplayname.getText().toString().trim();
                    password = RandomPass.randomAlphaNumeric(16);
                    user.setDisplayname(displayname);
                    user.setPassword(password);
                    registerController.registerUser(user);
                }
            }
        });
    }

    public void getResultMetadata() throws Exception {
        NetWorkCall netWorkCall = (NetWorkCall) new NetWorkCall().execute(metadataController.getResultMetadata());
        Metadata.setInstance(netWorkCall.get());
        onSuccessCallMetadata();
    }

    public void onSuccessCallMetadata() {
        Metadata metadata = Metadata.getInstance();
        genderData.add(new Util("", "性別", ""));
        ageData.add(new Util("", "年代", ""));
        areaData.add(new Util("", "都道府県", ""));
        cityData.add(new Util("", "市区町村", ""));


        genderData.addAll(metadata.getData().getUserProfileList().getSex());
        ArrayAdapter<Util> adapter1 = new ArrayAdapter<Util>(getApplicationContext(), R.layout.spinner_item, genderData);
        spinnerGender.setAdapter(adapter1);

        ageData.addAll(metadata.getData().getUserProfileList().getAge());
        ArrayAdapter<Util> adapter2 = new ArrayAdapter<Util>(getApplicationContext(), R.layout.spinner_item, ageData);
        spinnerAge.setAdapter(adapter2);

        SortedSet keys = new TreeSet<>(metadata.getData().getUserProfileList().getAreaHashMap().keySet());
        for (Object key : keys) {
            areaData.add(metadata.getData().getUserProfileList().getAreaHashMap().get(key));
        }
        ArrayAdapter<Util> adapter3 = new ArrayAdapter<Util>(getApplicationContext(), R.layout.spinner_item, areaData);
        spinnerArea.setAdapter(adapter3);

        cityAdapter = new ArrayAdapter<Util>(getApplicationContext(), R.layout.spinner_item, cityData);
        spinnerCityRegion.setAdapter(cityAdapter);

        spinnerGender.setOnItemSelectedListener(this);
        spinnerAge.setOnItemSelectedListener(this);
        spinnerArea.setOnItemSelectedListener(this);
        spinnerCityRegion.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0 && view != null) {
            TextView textView = (TextView) view;
            textView.setTextColor(getResources().getColor(R.color.textDefault));
        }

        switch (parent.getId()) {
            case R.id.spinnerGender:
                Util sexModel = (Util) parent.getSelectedItem();
                user.setSex(sexModel.getValue());
                break;
            case R.id.spinnerAge:
                Util ageModel = (Util) parent.getSelectedItem();
                user.setAge(ageModel.getValue());
                break;
            case R.id.spinnerArea:
                try {
                    Util areaModel = (Util) parent.getSelectedItem();
                    user.setArea(areaModel.getValue());

                    cityData.clear();
                    cityData.add(new Util("", "市区町村", ""));
                    cityAdapter.notifyDataSetChanged();

                    regionController.getRegionList(areaModel.getValue()).enqueue(new Callback<Region>() {
                        @Override
                        public void onResponse(Call<Region> call, Response<Region> response) {
                            if (response.isSuccessful()) {
                                cityData.addAll(response.body().getRegions());
                                cityAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<Region> call, Throwable t) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.spinnerCityRegion:
                Util cityModel = (Util) parent.getSelectedItem();
                user.setCity_id(cityModel.getValue());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmitRegister:
                if (edtDisplayname.getText().toString().trim().isEmpty()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (user.getSex().trim().equals("性別")) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (user.getAge().trim().equals("年代")) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (user.getArea().trim().equals("都道府県")) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (user.getCity_id().trim().equals("市区町村")) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage("全ての項目を入力してください。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (!checkBox.isChecked()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage("18歳以上ですか？");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else if (!checkBox2.isChecked()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage("利用規約に同意する必要があります。");
                    alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                } else {
                    displayname = edtDisplayname.getText().toString().trim();
                    password = RandomPass.randomAlphaNumeric(16);
                    user.setDisplayname(displayname);
                    user.setPassword(password);
                    registerController.registerUser(user);
                    break;
                }
        }
    }

    @Override
    protected void onDestroy() {
        CShowProgress.getInstance().hideProgress();
        super.onDestroy();
    }
}
