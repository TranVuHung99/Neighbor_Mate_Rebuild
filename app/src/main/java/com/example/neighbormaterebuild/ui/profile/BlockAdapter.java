package com.example.neighbormaterebuild.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.controller.BlockController;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.utils.CacheImage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.FootprintViewHolder>{
    Context context;
    List<User> itemChatList;
    private BlockController controller;

    public BlockAdapter(Context context, List<User> itemChatList, BlockController controller) {
        this.context = context;
        this.itemChatList = itemChatList;
        this.controller = controller;
    }

    @NonNull
    @Override
    public BlockAdapter.FootprintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_vertical_block, parent, false);
        return new BlockAdapter.FootprintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockAdapter.FootprintViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final User user = itemChatList.get(position);

        holder.tvUsername.setText(user.getDisplayname());

        try {
            RequestOptions sharedOptions =
                    new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_image_solid_svg);

            String avatarUrl = CacheImage.create(context, user.getAvatar_url());

            if (avatarUrl != null)
                Glide.with(context)
                        .load(avatarUrl)
                        .apply(sharedOptions)
                        .into(holder.imgAvatarBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.btnUnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemChatList.remove(position);
                SharedPreferences sharedPref = context.getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
                String token = sharedPref.getString("token", "");
                controller.unLock(token, user.getId(), user.getUser_code(), "0")
                        .enqueue(new Callback<User.UserResponse>() {
                            @Override
                            public void onResponse(Call<User.UserResponse> call, Response<User.UserResponse> response) {
//                                Log.e("UnLock:", call.request().toString());
//                                Log.e("UnLock:", response.code() + ":" + response.message());
                                if (((Activity) context).hasWindowFocus()) {
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context)
                                            .setNegativeButton("はい", null);
                                    if (response.isSuccessful() && response.body() != null) {
                                        //Log.e("UnLock:", response.body().toString());
                                        alertBuilder.setMessage(response.body().getMessage() + "");
                                    } else {
                                        alertBuilder.setMessage(response.message() + "");
                                    }
                                    alertBuilder.show();
                                }

                            }

                            @Override
                            public void onFailure(Call<User.UserResponse> call, Throwable t) {

                            }
                        });
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemChatList.size();
    }

    public class FootprintViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        ImageView imgAvatarBlock;
        Button btnUnBlock;
        CardView cardAvatar;

        public FootprintViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            imgAvatarBlock = itemView.findViewById(R.id.imgAvatarBlock);
            btnUnBlock = itemView.findViewById(R.id.btnUnBlock);
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
