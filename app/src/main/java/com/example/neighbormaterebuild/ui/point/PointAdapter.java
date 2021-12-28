package com.example.neighbormaterebuild.ui.point;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.billing.BillingViewModel;
import com.example.neighbormaterebuild.model.Point;

import java.util.List;

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.PointViewHolder>{

    Context context;
    List<Point> pointList;
    BillingViewModel billingViewModel;

    public PointAdapter(Context context, List<Point> pointList, BillingViewModel billingViewModel ) {
        this.context = context;
        this.pointList = pointList;
        this.billingViewModel = billingViewModel;
    }

    @NonNull
    @Override
    public PointAdapter.PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_point, parent, false);
        return new PointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointAdapter.PointViewHolder holder, int position) {
        final Point point = pointList.get(position);

        setImageAvatar(holder.imgPointPackage, point.getImage());
        setImageAvatar(holder.imgRank, point.getRankImage());

        holder.imgPointPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingViewModel.buy(point.getSku());
            }
        });

        int imgHeight = context.getResources().getDisplayMetrics().heightPixels;
        holder.imgPointPackage.getLayoutParams().height = (int) (0.12 * imgHeight);
    }

    @Override
    public int getItemCount() {
        return pointList.size();
    }

    public class PointViewHolder extends RecyclerView.ViewHolder {
        //TextView tvPoint, tvTitle, tvBonus;
        ImageView imgPointPackage, imgRank;

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPointPackage = itemView.findViewById(R.id.imgPointPackage);
            imgRank = itemView.findViewById(R.id.imgRanking);
        }
    }

    private void setImageAvatar(final ImageView imageAvatar, String imageUrl) {
        Glide.with(context)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageAvatar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).apply(new RequestOptions().error(R.drawable.ic_image_solid_svg))
                .into(imageAvatar);
    }

    public void setData(List<Point> list){
        this.pointList = list;
        notifyDataSetChanged();
    }
}
