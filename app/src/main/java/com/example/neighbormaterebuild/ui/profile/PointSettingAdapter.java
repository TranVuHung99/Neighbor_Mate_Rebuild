package com.example.neighbormaterebuild.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.model.PointSetting;

import java.util.List;

public class PointSettingAdapter extends BaseAdapter {
    Context context;
    List<PointSetting> list;
    private LayoutInflater layoutInflater;

    public PointSettingAdapter(Context context, List<PointSetting> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public PointSetting getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder {
        TextView tvTitle, tvPoint;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_point_setting, null);
            holder = new ViewHolder();
            holder.tvTitle = view.findViewById(R.id.tvTitle);
            holder.tvPoint = view.findViewById(R.id.tvPoint);
            view.setTag(holder);
        } else holder = (ViewHolder) view.getTag();

        PointSetting item = list.get(i);
        holder.tvTitle.setText(item.getName());
        holder.tvPoint.setText(item.getPoint() + " pt");

        return view;
    }



    public void setList(List<PointSetting> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
