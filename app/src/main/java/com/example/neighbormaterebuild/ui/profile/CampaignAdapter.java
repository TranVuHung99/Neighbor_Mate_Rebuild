package com.example.neighbormaterebuild.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.model.Campaign;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.CampaignService;
import com.example.neighbormaterebuild.ui.CampaignViewActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder>{

    Context context;
    List<Campaign> list;

    public CampaignAdapter(Context context, List<Campaign> itemChatList) {
        this.context = context;
        this.list = itemChatList;
    }

    @NonNull
    @Override
    public CampaignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new CampaignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CampaignViewHolder holder, int position) {
        final Campaign item = list.get(position);
        holder.tvTitle.setText(item.getHeader());
        holder.tvMessage.setText(item.getTitle());
        if (item.getReceived_time() != null && !item.getReceived_time().isEmpty())
            holder.tvTimeMessage.setText(item.getReceived_time());
        else holder.tvTimeMessage.setVisibility(View.GONE);

        if (item.getIs_read().equals("1")) {
            holder.imgUnread.setVisibility(View.GONE);
        }
        else {
            holder.imgUnread.setVisibility(View.VISIBLE);
        }
        if (item.getAvatar_url() == null || item.getAvatar_url().isEmpty()) {
            holder.imgAvatar.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));
        } else
            Glide.with(context)
                    .load(item.getAvatar_url())
                    .apply(new RequestOptions().error(R.drawable.ic_image_solid_svg))
                    .into(holder.imgAvatar);
        holder.cardUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
                String token = sharedPref.getString("token", "");
                Client.getClient(Config.API_URL).create(CampaignService.class)
                        .getDetail(token, item.getId()).enqueue(new Callback<Campaign.ResponseDetail>() {
                    @Override
                    public void onResponse(Call<Campaign.ResponseDetail> call, Response<Campaign.ResponseDetail> response) {
//                        Log.e("Campaign", response.message());
//                        Log.e("Campaign", response.code() + "");
//                        Log.e("Campaign", response.headers().toString());
//                        Log.e("Campaign", response.body().getUrl());

                        if (response.isSuccessful() && response.body().getUrl() != null) {
                            Intent intentCompany = new Intent(context, CampaignViewActivity.class);
                            intentCompany.putExtra("url", response.body().getUrl());
                            intentCompany.putExtra("title", item.getHeader());
                            context.startActivity(intentCompany);
                            holder.imgUnread.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        //Log.e("Campaign", "" + call.request());
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CampaignViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTimeMessage;
        ImageView imgUnread, imgAvatar;
        CardView cardUserContainer;

        public CampaignViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            imgUnread = itemView.findViewById(R.id.imgUnread);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvTimeMessage = itemView.findViewById(R.id.tvTimeMessage);
            cardUserContainer = itemView.findViewById(R.id.cardUserContainer);
        }
    }

    public void setList(List<Campaign> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Campaign> getList() {
        return list;
    }

    public void addAll(List<Campaign> newList) {
        int lastIndex = list.size();
        list.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }
}
