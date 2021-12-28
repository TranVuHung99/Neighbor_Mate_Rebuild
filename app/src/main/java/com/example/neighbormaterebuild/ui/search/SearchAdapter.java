package com.example.neighbormaterebuild.ui.search;

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
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.ui.user.UserDetailActivity;
import com.example.neighbormaterebuild.utils.CacheImage;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HORIZONTAL = 0;
    public static final int TYPE_VERTICAL = 1;

    Context context;
    List<User> userList;
    int indexOfType;

    public SearchAdapter(Context context, List<User> userList, int indexOfType) {
        this.context = context;
        this.userList = userList;
        this.indexOfType = indexOfType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HORIZONTAL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_user_horizontal, parent, false);
            return new ViewHolderHorizontalSearch(view);
        } else if (viewType == TYPE_VERTICAL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_user_vertical, parent, false);
            return new ViewHolderVerticalSearch(view);
        }
        throw new RuntimeException("Don't know this type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final User userModel = userList.get(position);
        //Log.e("UserList", userModel.toString());
        if (holder instanceof ViewHolderHorizontalSearch) {


            int imgHeight = context.getResources().getDisplayMetrics().heightPixels;
            ((ViewHolderHorizontalSearch) holder).image_view.getLayoutParams().height = (int) (0.1275 * imgHeight);
            ((ViewHolderHorizontalSearch) holder).tvMessage.setText(userModel.getUser_status());
            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getAge().size(); i++) {
                if (Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getValue().equals(userModel.getAge())) {
                    ((ViewHolderHorizontalSearch) holder).tvName.setText(userModel.getArea_name() + " " +
                            Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getName());
                    break;
                }
            }
            setImageAvatar(((ViewHolderHorizontalSearch) holder).image_view, userModel);

            ((ViewHolderHorizontalSearch) holder).cardContainerProfile.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (!PointStore.getInstance().isUserEnoughPoint(PointFee.viewProfile)) {
                        PointStore.getInstance().showDialogUserNotEnoughPoint(context);
                        return;
                    }
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", userModel);
                    context.startActivity(intent);
                }
            });
        } else if (holder instanceof ViewHolderVerticalSearch) {
            ((ViewHolderVerticalSearch) holder).tvMessage.setText(userModel.getUser_status());

            for (int i = 0; i < Metadata.getInstance().getData().getUserProfileList().getAge().size(); i++) {
                if (Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getValue().equals(userModel.getAge())) {
                    ((ViewHolderVerticalSearch) holder).tvAreaName.setText(userModel.getArea_name() + " " +
                            Metadata.getInstance().getData().getUserProfileList().getAge().get(i).getName());
                    break;
                }
            }
            ((ViewHolderVerticalSearch) holder).tvUsername.setText(userModel.getDisplayname());

            setImageAvatar(((ViewHolderVerticalSearch) holder).image_view, userModel);
            ((ViewHolderVerticalSearch) holder).cardUserContainer.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (!PointStore.getInstance().isUserEnoughPoint(PointFee.viewProfile)) {
                        PointStore.getInstance().showDialogUserNotEnoughPoint(context);
                        return;
                    }
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", userModel);
                    context.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private static class ViewHolderHorizontalSearch extends RecyclerView.ViewHolder {
        TextView tvName, tvMessage;
        ImageView image_view;
        CardView cardContainerProfile;

        public ViewHolderHorizontalSearch(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMessage = itemView.findViewById(R.id.tvMessageHor);
            image_view = itemView.findViewById(R.id.imgAvatar);
            cardContainerProfile = itemView.findViewById(R.id.cardContainerProfile);
        }
    }

    private static class ViewHolderVerticalSearch extends RecyclerView.ViewHolder {
        TextView tvMessage, tvUsername, tvAreaName;
        ImageView image_view;
        CardView cardUserContainer, cardAvatar;

        public ViewHolderVerticalSearch(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvAreaName = itemView.findViewById(R.id.tvAreaName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            image_view = itemView.findViewById(R.id.imgAvatar);
            cardUserContainer = itemView.findViewById(R.id.cardUserContainer);
            cardAvatar = itemView.findViewById(R.id.cardAvatar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return indexOfType == 0 ? TYPE_HORIZONTAL : TYPE_VERTICAL;
    }


    private void setImageAvatar(ImageView imageAvatar, User user) {
        try {
            RequestOptions sharedOptions =
                    new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_image_solid_svg);

            String url = CacheImage.create(context, user.getAvatar_url());

            if (url != null)
                Glide.with(context)
                        .load(url)
                        .apply(sharedOptions)
                        .into(imageAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setList(List<User> list) {
        this.userList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<User> newList) {
        int lastIndex = userList.size();
        userList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }
}

