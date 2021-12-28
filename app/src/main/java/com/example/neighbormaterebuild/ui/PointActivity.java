package com.example.neighbormaterebuild.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.billing.BillingClientLifecycle;
import com.example.neighbormaterebuild.billing.BillingViewModel;
import com.example.neighbormaterebuild.comm.RandomPass;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.PointController;
import com.example.neighbormaterebuild.model.Point;
import com.example.neighbormaterebuild.model.ValidatePayment;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.network.SocketClient;
import com.example.neighbormaterebuild.service.ValidatePaymentService;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.ui.point.PointAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointActivity extends AppCompatActivity {
    public static final String TAG = "PointActivity";

    private MaterialToolbar toolbar;
    private RecyclerView recyclePointContainer;
    private List<Point> pointList;
    private RecyclerView.LayoutManager layoutManager;
    private PointController pointController;
    private PointAdapter adapter;
    private TextView totalPoint;
    private ProgressBar processLoading;

    private BillingClientLifecycle billingClientLifecycle;
    private BillingViewModel billingViewModel;

    private List<String> skus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_point);
        initPurchase();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_back_svg);
        View view = toolbar.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pointController = new PointController(Config.API_URL);

        recyclePointContainer = findViewById(R.id.recyclePointContainer);
        processLoading = findViewById(R.id.progressLoading);
        totalPoint = findViewById(R.id.totalPoint);

        layoutManager = new LinearLayoutManager(this);
        recyclePointContainer.setLayoutManager(layoutManager);
        pointList = new ArrayList<>();

        adapter = new PointAdapter(this, pointList, billingViewModel);
        recyclePointContainer.setAdapter(adapter);
        getPointPackageApi();
    }

    private void getPointPackageApi() {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", "");
        billingClientLifecycle.onBillingSetup.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                pointController.getResultPointPackage(token, "0")
                        .enqueue(new Callback<Point.PointResponse>() {
                            @Override
                            public void onResponse(Call<Point.PointResponse> call, Response<Point.PointResponse> response) {
                                //Log.d("Call request", call.request().toString());
                                Log.d("Call request header", call.request().headers().toString());
                                Log.d("Response raw header", response.headers().toString());
                                Log.d("Response raw", String.valueOf(response.raw().body()));
                                Log.d("Response code", String.valueOf(response.code()));

                                if (response.isSuccessful() && response.body()!=null) {
                                    try {
                                        PointStore.getInstance().setTotalPoint(Integer.parseInt(response.body().getData().getTotalPoint()));
                                        PointStore.getInstance().setPointPackage(response.body().getData().getPointList());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    processLoading.setVisibility(View.GONE);
                                    //Log.e(TAG, "" + response.body());
                                    totalPoint.setText(response.body().getData().getTotalPoint());

                                    pointList = response.body().getData().getPointList();
                                    adapter.setData(pointList);

                                    for (Point point : pointList) {
                                        skus.add(point.getSku());
                                    }

                                    billingClientLifecycle.querySkuDetails(skus);
                                    billingClientLifecycle.queryPurchases();

                                }
                            }

                            @Override
                            public void onFailure(Call<Point.PointResponse> call, Throwable t) {
                                //Log.d("Call request", call.request().toString());
                                //Log.d("Call request header", call.request().headers().toString());
                                t.printStackTrace();
                            }
                        });
            }
        });
    }

    private void initPurchase() {
        billingViewModel = ViewModelProviders.of(this).get(BillingViewModel.class);
        billingClientLifecycle = BillingClientLifecycle.getInstance(getApplication());
        getLifecycle().addObserver(billingClientLifecycle);

        billingClientLifecycle.purchaseUpdateEvent.observe(this, new Observer<List<Purchase>>() {
            @Override
            public void onChanged(List<Purchase> purchases) {
                if (purchases != null) {
                    //Log.e(TAG, "" + purchases);
                    validatePurchases(purchases);
                }
            }
        });

//         Launch billing flow when user clicks button to buy something.
        billingViewModel.buyEvent.observe(this, new Observer<BillingFlowParams>() {
            @Override
            public void onChanged(BillingFlowParams billingFlowParams) {
                if (billingFlowParams != null) {
                    billingClientLifecycle
                            .launchBillingFlow(PointActivity.this, billingFlowParams);
                }
            }
        });
    }

    private void validatePurchases(List<Purchase> purchaseList) {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");

        for (final Purchase purchase : purchaseList) {
            String sku = purchase.getSku();
            String purchaseToken = purchase.getPurchaseToken();
            String signatureAndroid = purchase.getSignature();
            //Log.d(TAG, "Register purchase with sku: " + sku + ", token: " + purchaseToken + ", signature" + signatureAndroid);

            String receiptData = purchase.getOriginalJson();
            //Log.d(TAG, receiptData);

            if(sku.contains("android.test")) {
                try {
                    JSONObject receipt_json = new JSONObject(receiptData);

                    String orderIDRandom = RandomPass.randomAlphaNumeric(16);
                    Log.d(TAG, "orderIDRandom:" + orderIDRandom);
                    receipt_json.put("orderId", orderIDRandom);
                    receiptData = receipt_json.toString();
                    Log.d(TAG, "receiptData:" + receiptData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JSONObject request_data = new JSONObject();

            try {
                request_data.put("dataAndroid", receiptData);
                request_data.put("signatureAndroid", signatureAndroid);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // PHUC TODO: send SOCKET payment start validate receipt:
            String logPayment = "3. start validate receipt: receipt-data:" + request_data.toString();
            SocketClient.getInstance().sendClientLogs(Config.SLUG_LOG_SOCKET, logPayment);

            Client.getClient(Config.API_URL).create(ValidatePaymentService.class)
                    .validate(token, request_data.toString()).enqueue(new Callback<ValidatePayment>() {
                @Override
                public void onResponse(@NotNull Call<ValidatePayment> call, @NotNull final Response<ValidatePayment> response) {
                    //Log.e("Call request", call.request().toString());
                    //Log.e("Validate: ", response.body().toString());

                    // PHUC TODO: send SOCKET server response receipt validation:
                    String logPayment = "4. server response receipt validation:" + response.body().toString();
                    SocketClient.getInstance().sendClientLogs(Config.SLUG_LOG_SOCKET, logPayment);

                    if (response.isSuccessful() && response.body() != null) {
                        billingClientLifecycle.handlePurchase(purchase);

                        try {
                            if (response.body().getData()!=null && response.body().getData().getPoint() > 0) {
                                //Log.e("getPoint ok", response.body().getData().getPoint() + "");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        totalPoint.setText(response.body().getData().getTotalPoint() + "");

                                        if (PointActivity.this.hasWindowFocus())
                                            new MaterialAlertDialogBuilder(new ContextThemeWrapper(PointActivity.this, R.style.AlertDialog))
                                                    .setTitle("ポイントを購入しました")
                                                    .setMessage("購入したポイント：" + response.body().getData().getPoint() + "\n" +
                                                            "合計ポイント：" + response.body().getData().getTotalPoint())
                                                    .setPositiveButton("はい", null)
                                                    .show();
                                    }
                                });
                            }
                        } catch (Exception e){}
                    }

                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    //Log.e(TAG, "validate to server fail" + t);
                    t.printStackTrace();
                }
            });
        }
    }
}
