package com.example.neighbormaterebuild.billing;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;

import java.util.Map;

public class BillingViewModel extends AndroidViewModel {

    private MutableLiveData<Map<String, SkuDetails>> skusWithSkuDetails;

    public SingleLiveEvent<BillingFlowParams> buyEvent = new SingleLiveEvent<>();


    public BillingViewModel(@NonNull Application application) {
        super(application);
        skusWithSkuDetails = BillingClientLifecycle.getInstance(application).skusWithSkuDetails;
    }

    public void buy(String sku){
        SkuDetails skuDetails = null;
        // Create the parameters for the purchase.
        if (skusWithSkuDetails.getValue() != null) {
            skuDetails = skusWithSkuDetails.getValue().get(sku);
        }

        if (skuDetails == null) {
            Log.e("Billing", "Could not find SkuDetails to make purchase.");
            return;
        }

        BillingFlowParams.Builder billingBuilder = BillingFlowParams.newBuilder().setSkuDetails(skuDetails);

        BillingFlowParams billingParams = billingBuilder.build();

        // Send the parameters to the Activity in order to launch the billing flow.
        buyEvent.postValue(billingParams);

    }
}
