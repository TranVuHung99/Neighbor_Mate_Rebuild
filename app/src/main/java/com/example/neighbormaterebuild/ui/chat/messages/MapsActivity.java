package com.example.neighbormaterebuild.ui.chat.messages;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentActivity;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.controller.MapViewController;
import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener  {

    private GoogleMap mMap;
    private LatLng location;

    private ProgressBar loading;
    private ImageButton btnClose;

    private MapViewController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        loading = findViewById(R.id.progressLoading);

        controller = new MapViewController(Config.API_URL);

        Bundle bundle = getIntent().getExtras();
        String mLat = bundle.getString("lat");
        String mLong = bundle.getString("long");
        int uID = bundle.getInt("uID");
        String msgID = bundle.getString("msgID");
        final boolean consumePoint = bundle.getBoolean("consumePoint");

        if (mLat != null && mLong != null)
            location = new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLong));

        if (ProfileStore.getInstance().getUserLogin() != null)
            controller.openMapView(
                    ProfileStore.getInstance().getUserLogin().getTokken(),
                    uID + "",
                    msgID
            ).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    loading.setVisibility(View.GONE);
                    //Log.e("Open Map: ", response.code() + ":" + response.message());
                    //Log.e("Open Map: ", response.body() + "");
                    if (response.isSuccessful() && consumePoint) {
                        PointStore.getInstance().consumePoint(PointFee.viewLocationChatting);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    loading.setVisibility(View.GONE);
                }
            });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (location != null) {
            mMap.addMarker(new MarkerOptions().position(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnClose) {
            finish();
        }
    }
}
