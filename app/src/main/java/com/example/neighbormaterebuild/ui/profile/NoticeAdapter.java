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
import com.bumptech.glide.request.RequestOptions;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.model.Notice;
import com.example.neighbormaterebuild.ui.chat.messages.NoticeDetailActivity;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    Context context;
    List<Notice> itemChatList;

    public NoticeAdapter(Context context, List<Notice> itemChatList) {
        this.context = context;
        this.itemChatList = itemChatList;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoticeViewHolder holder, int position) {
        final Notice item = itemChatList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getContent());
        holder.tvTimeMessage.setText(item.getPosted_at());

        if (item.getIs_read() == 1) {
            holder.imgUnread.setVisibility(View.GONE);
        }
        else {
            holder.imgUnread.setVisibility(View.VISIBLE);
        }

        try {
            Glide.with(context)
                    .load(item.getImagesPath()[0].getPath())
                    .apply(new RequestOptions().error(R.drawable.ic_image_solid_svg).override(500))
                    .into(holder.imgAvatar);
        }catch (Exception e){}

        holder.cardUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Log.e("kiemtra", item.getImages() + " ");
                Intent intent = new Intent(context, NoticeDetailActivity.class);
                intent.putExtra("notice", item);
                context.startActivity(intent);
                item.setIs_read(1);
                holder.imgUnread.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemChatList.size();
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTimeMessage;
        ImageView imgUnread, imgAvatar;
        CardView cardUserContainer;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            imgUnread = itemView.findViewById(R.id.imgUnread);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvTimeMessage = itemView.findViewById(R.id.tvTimeMessage);
            cardUserContainer = itemView.findViewById(R.id.cardUserContainer);
        }
    }

    public void setList(List<Notice> list) {
        this.itemChatList = list;
        notifyDataSetChanged();
    }

    public List<Notice> getItemChatList() {
        return itemChatList;
    }

    public void addAll(List<Notice> newList) {
        int lastIndex = itemChatList.size();
        itemChatList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }
}
