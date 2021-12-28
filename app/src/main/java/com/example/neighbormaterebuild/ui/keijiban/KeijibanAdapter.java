package com.example.neighbormaterebuild.ui.keijiban;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.model.AvatarUrl;
import com.example.neighbormaterebuild.model.Keijiban;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.utils.CacheImage;
import com.google.gson.Gson;

import java.util.List;

public class KeijibanAdapter extends RecyclerView.Adapter<KeijibanAdapter.ViewHolderUserCHat> {

    Context context;
    List<Keijiban> keijibanList;

    public KeijibanAdapter(Context context, List<Keijiban> keijibanList) {
        this.context = context;
        this.keijibanList = keijibanList;
    }

    @NonNull
    @Override
    public KeijibanAdapter.ViewHolderUserCHat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_vertical, parent, false);

        return new ViewHolderUserCHat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUserCHat holder, int position) {
        final Keijiban userModel = keijibanList.get(position);
        holder.tvMessage.setText(userModel.getContent());
        holder.tvUsername.setText(userModel.getDisplayname());
        holder.tvAreaName.setText(userModel.getArea_name());

        if (userModel.getThumb() != null) {
            holder.tvCamera.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvCamera.setVisibility(View.GONE);
        }

        for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getAge().size(); i++) {
            if (Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getValue().equals(userModel.getAge())) {
                holder.tvAreaName.setText(userModel.getArea_name() + " " +
                        Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getName());
                break;
            }
        }

        try {
            Gson g = new Gson();
            AvatarUrl[] a = g.fromJson(userModel.getAvatar_url(), AvatarUrl[].class);
            RequestOptions sharedOptions =
                    new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_image_solid_svg);

            String avatarUrl = CacheImage.create(context, a[0].getPath());

            if (avatarUrl != null)
                Glide.with(context)
                        .load(avatarUrl)
                        .apply(sharedOptions)
                        .into(holder.imgAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.cardUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PointStore.getInstance().isUserEnoughPoint(PointFee.viewKeijiban)) {
                    PointStore.getInstance().showDialogUserNotEnoughPoint(context);
                    return;
                }
                Intent intent = new Intent(context, KeijibanDetailActivity.class);
                intent.putExtra("keijiban", userModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keijibanList.size();
    }

    public class ViewHolderUserCHat extends RecyclerView.ViewHolder {
        TextView tvMessage, tvUsername, tvTimeMessage, tvAreaName, tvCamera;
        ImageView imgAvatar;
        CardView cardUserContainer, cardAvatar;
        public ViewHolderUserCHat(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvAreaName = itemView.findViewById(R.id.tvAreaName);
            cardUserContainer = itemView.findViewById(R.id.cardUserContainer);
            cardAvatar = itemView.findViewById(R.id.cardAvatar);
            tvCamera = itemView.findViewById(R.id.tvCamera);
        }
    }

    public void setList(List<Keijiban> list) {
        this.keijibanList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<Keijiban> newList) {
        int lastIndex = keijibanList.size();
        keijibanList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }
}
