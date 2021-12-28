package com.example.neighbormaterebuild.ui.profile;

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
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.ui.user.UserDetailActivity;
import com.example.neighbormaterebuild.utils.CacheImage;

import java.util.List;

public class FootprintAdapter extends RecyclerView.Adapter<FootprintAdapter.FootprintViewHolder> {
    Context context;
    List<User> itemChatList;

    public FootprintAdapter(Context context, List<User> itemChatList) {
        this.context = context;
        this.itemChatList = itemChatList;
    }

    @NonNull
    @Override
    public FootprintAdapter.FootprintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_vertical, parent, false);
        return new FootprintAdapter.FootprintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FootprintAdapter.FootprintViewHolder holder, int position) {
        final User itemChat = itemChatList.get(position);
        holder.tvUsername.setText(itemChat.getDisplayname());
        //holder.tvTimeMessage.setVisibility(View.GONE);
        if (itemChat.getUser_status() == null || itemChat.getUser_status().isEmpty())
            holder.tvMessage.setText("よろしくお願いします。");
        else holder.tvMessage.setText(itemChat.getUser_status());

        holder.cardUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PointStore.getInstance().isUserEnoughPoint(PointFee.viewProfile)) {
                    PointStore.getInstance().showDialogUserNotEnoughPoint(context);
                    return;
                }

                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra("user", itemChat);
                context.startActivity(intent);
            }
        });

        try {
            RequestOptions sharedOptions =
                    new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_image_solid_svg);

            String avatarUrl = CacheImage.create(context, itemChat.getImageFootprint());

            if (avatarUrl != null)
                Glide.with(context)
                        .load(avatarUrl)
                        .apply(sharedOptions)
                        .into(holder.imgAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }

        for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getAge().size(); i++) {
            if (Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getValue().equals(itemChat.getAge())) {
                holder.tvAreaName.setText(itemChat.getArea_name() + " " +
                        Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getName());
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return itemChatList.size();
    }

    public class FootprintViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvAreaName, tvTimeMessage, tvMessage;
        ImageView imgAvatar;
        CardView cardUserContainer, cardAvatar;

        public FootprintViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvAreaName = itemView.findViewById(R.id.tvAreaName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            cardUserContainer = itemView.findViewById(R.id.cardUserContainer);
            cardAvatar = itemView.findViewById(R.id.cardAvatar);
        }
    }


    public void setList(List<User> list) {
        this.itemChatList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<User> newList) {
        int lastIndex = itemChatList.size();
        itemChatList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }
}
