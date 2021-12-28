package com.example.neighbormaterebuild.ui.chat.items;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.controller.ChatController;
import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.chat.messages.ChatMessageActivity;
import com.example.neighbormaterebuild.utils.CacheImage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.UserCHatViewHolder> {

    Context context;
    List<User> userModelList;
    ChatController chatController;


    public ChatUserAdapter(Context context, List<User> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
        chatController = new ChatController(Config.API_URL);
    }

    @NonNull
    @Override
    public ChatUserAdapter.UserCHatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roster, parent, false);
        return new UserCHatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatUserAdapter.UserCHatViewHolder holder, int position) {
        final User userModel = userModelList.get(position);
        //Log.e("Roster: ", userModel.toString());

        holder.tvMessage.setText(userModel.getMsg_text());
        holder.tvUsername.setText(userModel.getDisplayname());

        if (userModel.getIs_pin_chat() != 0) {
            holder.imgPin.setVisibility(View.VISIBLE);
            holder.imgPinSwipe.setImageResource(R.drawable.ic_unpin);
        } else {
            holder.imgPin.setVisibility(View.GONE);
            holder.imgPinSwipe.setImageResource(R.drawable.ic_pin);
        }

        for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getAge().size(); i++) {
            if (Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getValue().equals(userModel.getAge())) {
                holder.tvAreaName.setText(userModel.getArea_name() + " " +
                        Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getName());
                break;
            }
        }

        holder.tvTimeMessage.setText(userModel.getSend_at());

        if (userModel.getIs_read() == 2) {
            holder.imgUnread.setVisibility(View.VISIBLE);
        } else holder.imgUnread.setVisibility(View.GONE);

        try {
            RequestOptions sharedOptions =
                    new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(500)
                            .error(R.drawable.ic_image_solid_svg);

            String avatarUrl = CacheImage.create(context, userModel.getAvatar_url());

            if (avatarUrl != null)
                Glide.with(context)
                        .load(avatarUrl)
                        .apply(sharedOptions)
                        .into(holder.imgAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.rowFG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((userModel.getIs_read() != 0 && !PointStore.getInstance().isUserEnoughPoint(PointFee.viewMessageText))) {
                    PointStore.getInstance().showDialogUserNotEnoughPoint(context);
                    return;
                }
                chatController.viewRoster(ProfileStore.getInstance().getUserLogin().getTokken(),
                        userModel.getUser_id(), userModel.getIs_read() == 0 ? 1 : 0).enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.code() == 200) {
                            if (userModel.getIs_read() != 0)
                                PointStore.getInstance().consumePoint(PointFee.viewMessageText);
                            Intent intent = new Intent(context, ChatMessageActivity.class);
                            intent.putExtra("user", userModel);
                            context.startActivity(intent);
                        }
                        else if (response.code() == 402)
                            PointStore.getInstance().showDialogUserNotEnoughPoint(context);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                    }
                });

                userModel.setIs_read(0);
                holder.imgUnread.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class UserCHatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvUsername, tvTimeMessage, tvAreaName;
        ImageView imgAvatar, imgUnread, imgPin, imgPinSwipe;
        CardView cardAvatar;
        LinearLayout rowFG;

        public UserCHatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimeMessage = itemView.findViewById(R.id.tvTimeMessage);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgUnread = itemView.findViewById(R.id.imgUnread);
            tvAreaName = itemView.findViewById(R.id.tvAreaName);
            rowFG = itemView.findViewById(R.id.rowFG);
            cardAvatar = itemView.findViewById(R.id.cardAvatar);
            imgPin = itemView.findViewById(R.id.imgPin);
            imgPinSwipe = itemView.findViewById(R.id.img_pin_roster);
        }
    }

    public void setList(List<User> list) {
        this.userModelList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<User> newList) {
        int lastIndex = userModelList.size();
        userModelList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }
}
