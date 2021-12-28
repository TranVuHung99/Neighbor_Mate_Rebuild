package com.example.neighbormaterebuild.store;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.model.Point;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.ui.PointActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class PointStore {

    private static PointStore ourInstance = new PointStore();

    public static PointStore getInstance() {
        if (ourInstance == null) {
            synchronized (PointStore.class) {
                if (null == ourInstance) {
                    ourInstance = new PointStore();
                }
            }
        }
        return ourInstance;
    }

    private PointStore() {
    }

    private List<PointSetting> pointSettings;

    private List<Point> pointPackage;

    private int totalPoint;

    public PointSetting getPointUnLimit() {
        for (PointSetting item : this.pointSettings) {
            if (item.getSlug().equals(PointFee.unlimitPoint)) {
                return item;
            }
        }
        return null;
    }

    public boolean isUserEnoughPoint(@NonNull String slug) {
        //Log.e("Slug:", slug);
        for (PointSetting item : this.pointSettings) {
            if (item.getSlug().equals(slug) && this.totalPoint >= item.getPoint()) {
                //Log.e("Total Point: ", this.totalPoint + ">=" + item.getPoint());
                return true;
            }
        }
        return false;
    }

    public int getItemPointFee(@NonNull String slug) {
        //Log.e("getItemPointFee:", slug);
        for (PointSetting item : this.pointSettings) {
            if (item.getSlug().equals(slug)) {
                return item.getPoint();
            }
        }
        return 0;
    }

    public void consumePoint(@NonNull String slug) {
        Log.e("consumePoint:", slug);
        if (pointSettings != null)
            for (PointSetting item : this.pointSettings) {
                if (item.getSlug().equals(slug)) {
                    Log.e("ConsumePoint: ", this.totalPoint + "-" + item.getPoint());
                    totalPoint = totalPoint - item.getPoint();
                    Log.e("Total Point: ", this.totalPoint + "");
                }
            }
    }

    public Point getPointPackage(@NonNull String sku) {
        //Log.e("getPointPackage:", sku);
        try {
            for (Point item : this.pointPackage) {
                if (item.getSku().equals(sku)) return item;
            }
        } catch (Exception e) {

        }
        return null;
    }

    public void showDialogUserNotEnoughPoint(final Context context) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        alertBuilder.setMessage("ポイントが不足しています。ポイントを追加しますか？");
        alertBuilder.setPositiveButton("購入ページ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(context, PointActivity.class);
                context.startActivity(intent);
            }
        });
        alertBuilder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        if (((Activity) context).hasWindowFocus())
            alertBuilder.show();
    }

    public static void showDialogUnlimit(final Context context) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage("ポイントが不足しています。");
        alertBuilder.setPositiveButton("購入ページ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(context, PointActivity.class);
                context.startActivity(intent);
            }
        });
        alertBuilder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        if (((Activity) context).hasWindowFocus())
            alertBuilder.show();
    }

    public List<PointSetting> getPointSettings() {
        return pointSettings;
    }

    public void setPointSettings(List<PointSetting> pointSettings) {
        this.pointSettings = pointSettings;
    }

    public int getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }

    public List<Point> getPointPackage() {
        return pointPackage;
    }

    public void setPointPackage(List<Point> pointPackage) {
        this.pointPackage = pointPackage;
    }

    public static class NetWorkCall<T> extends AsyncTask<Call, Void, T> {
        @Override
        protected T doInBackground(Call... calls) {
            try {
                Call<T> call = calls[0];
                Response<T> response = call.clone().execute();
                return response.body();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
