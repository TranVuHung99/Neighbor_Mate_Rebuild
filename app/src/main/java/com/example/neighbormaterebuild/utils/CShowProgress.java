package com.example.neighbormaterebuild.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.example.neighbormaterebuild.R;

public class CShowProgress {

    public static CShowProgress mCShowProgress;
    public Dialog mDialog;

    public CShowProgress() {
    }

    public static CShowProgress getInstance() {
        if (mCShowProgress == null) {
            mCShowProgress= new CShowProgress();
        }
        return mCShowProgress;
    }

    public void showProgress(Context mContext) {
        mDialog= new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.remove_border);
        mDialog.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        if(((Activity) mContext).hasWindowFocus())
            mDialog.show();
    }

    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
