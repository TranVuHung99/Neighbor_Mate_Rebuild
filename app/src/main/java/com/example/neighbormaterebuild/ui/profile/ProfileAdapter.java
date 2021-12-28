package com.example.neighbormaterebuild.ui.profile;

import android.annotation.SuppressLint;
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

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.model.Utility;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    Context context;
    List<Utility> utilityList;

    public ProfileAdapter(Context context, List<Utility> utilityList) {
        this.context = context;
        this.utilityList = utilityList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_toolbar_utility, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Utility utility = utilityList.get(position);
        holder.imgIconContainer.setImageResource(utility.getImage());
        holder.imgIconContainer.setMaxHeight(0);
        holder.tvNameContainer.setText(utility.getText());
        holder.cardContainerUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        Intent i = new Intent(context, ConfigActivity.class);
                        context.startActivity(i);
                        break;
                    case 1:
                        Intent intent1 = new Intent(context, PointSettingActivity.class);
                        context.startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(context, FavoriteActivity.class);
                        context.startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(context, FootprintActivity.class);
                        context.startActivity(intent3);
                        break;
                    case 4:
                        // Notice
                        Intent intent4 = new Intent(context, NoticeActivity.class);
                        context.startActivity(intent4);
                        break;
                    case 5:
                        // Campaign
                        Intent intent5 = new Intent(context, CampaignActivity.class);
                        context.startActivity(intent5);
                        break;
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return utilityList.size();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIconContainer;
        TextView tvNameContainer;
        CardView cardContainerUtility;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameContainer = itemView.findViewById(R.id.tvNameContainer);
            cardContainerUtility = itemView.findViewById(R.id.cardContainerUtility);
            imgIconContainer = itemView.findViewById(R.id.imgIconContainer);
        }
    }
}
