package com.example.neighbormaterebuild.ui.chat.messages;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.model.AvatarUrl;
import com.example.neighbormaterebuild.model.Message;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.HttpHeaders;
import com.example.neighbormaterebuild.network.SocketClient;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.ui.MessagePhotoViewActivity;
import com.example.neighbormaterebuild.ui.user.UserDetailActivity;
import com.example.neighbormaterebuild.utils.CacheImage;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class MessageListAdapter extends RecyclerView.Adapter{

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private List<Message> mMessageList;
    private User user;

    public MessageListAdapter(Context context, List<Message> messageList, User user) {
        this.context = context;
        this.mMessageList = messageList;
        this.user = user;
    }

    private int getID() {
        int id = 0;
        if (user.getId() != null && !user.getId().isEmpty()) id = Integer.valueOf(user.getId());
        else if (user.getUser_id() != null && !user.getUser_id().isEmpty())
            id = Integer.valueOf(user.getUser_id());

        return id;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);
        //Log.e("ChatBubble", message.getuID() + " " + getID());

        if (message.getuID() != getID()) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    private void calulate(View v) {
        int imgWidth = context.getResources().getDisplayMetrics().widthPixels;
        v.getLayoutParams().width = (int) (0.5 * imgWidth);
        v.getLayoutParams().height = (int) ((0.5 * imgWidth) * 0.75);
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_right, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_left, parent, false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            view.setLayoutParams(layoutParams);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Message message = mMessageList.get(position);

        //Log.e("ChatBubble", message + "");
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    public void addMessage(Message item) {
        mMessageList.add(item);
        notifyDataSetChanged();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, txtUnread;
        ImageView imageView;
        ConstraintLayout container, viewBubble;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            imageView = itemView.findViewById(R.id.imgViewRght);
            viewBubble = itemView.findViewById(R.id.viewBubble);
            txtUnread = itemView.findViewById(R.id.tvUnread);
            container = itemView.findViewById(R.id.container);
        }

        void bind(final Message message) {
            timeText.setText(message.time());

            if (message.getIsRead() == 0) {
                txtUnread.setVisibility(View.VISIBLE);
            } else txtUnread.setVisibility(View.GONE);

            if (message.getType() != Message.text) {
                calulate(imageView);
                int imgWidth = context.getResources().getDisplayMetrics().widthPixels;
                viewBubble.setMaxWidth((int) (0.7 * imgWidth));
            }

            switch (message.getType()) {
                case Message.text:
                    imageView.setVisibility(View.GONE);
                    messageText.setVisibility(View.VISIBLE);
                    messageText.setText(message.getMsg());
                    viewBubble.setEnabled(false);
                    break;
                case Message.image:
                    imageView.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.GONE);
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    final String mediaID = message.getParam().get("media_id").toString();
                    if (mediaID != null && !mediaID.isEmpty()) {
                        setImageAvatar(context, imageView, createImageUrlThumbnail(mediaID), false);
                        viewBubble.setEnabled(true);
                        viewBubble.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url = createImageUrlOpen(mediaID, message.getMsgID(),
                                        message.getShowed(), false);
                                Intent intent = new Intent(context, MessagePhotoViewActivity.class);
                                intent.putExtra("avatar_url", url);
                                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });
                    }
                    break;
                case Message.location:
                    imageView.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.GONE);
                    final String mLat = message.getParam().get("lat").toString();
                    final String mLong = message.getParam().get("long").toString();
                    if (mLat != null && mLong != null) {
                        setImageAvatar(context, imageView, createMapUrl(mLat, mLong), false);
                        viewBubble.setEnabled(true);
                        viewBubble.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, MapsActivity.class);
                                intent.putExtra("uID", message.getuID());
                                intent.putExtra("msgID", message.getMsgID());
                                intent.putExtra("lat", mLat);
                                intent.putExtra("long", mLong);
                                intent.putExtra("consumePoint", false);
                                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });
                    }
                    break;
            }

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        CircleImageView profileImage;
        ImageView imageView, imgCover;
        ConstraintLayout viewBubble;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            profileImage = itemView.findViewById(R.id.image_message_profile);
            imageView = itemView.findViewById(R.id.imgViewLeft);
            viewBubble = itemView.findViewById(R.id.viewBubble);
            imgCover = itemView.findViewById(R.id.imgCover);
        }

        void bind(final Message message) {
            timeText.setText(message.time());

            if (message.getType() != Message.text) {
                calulate(imageView);
                int imgWidth = context.getResources().getDisplayMetrics().widthPixels;
                viewBubble.setMaxWidth((int) (0.7 * imgWidth));
            }
            String avatarUrl = null;

            if (user.getAvatar_url() != null) {
                avatarUrl = user.getAvatar_url();
            } else {
                Gson g = new Gson();
                AvatarUrl[] a = g.fromJson(user.getImage_avata(), AvatarUrl[].class);
                avatarUrl = a[0].getPath();
            }

            try {
                RequestOptions sharedOptions =
                        new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .override(200, 200)
                                .error(R.drawable.ic_image_solid_svg);

                String avatar = CacheImage.create(context, avatarUrl);

                if (avatar != null)
                    Glide.with(context)
                            .load(avatar)
                            .apply(sharedOptions)
                            .into(profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!PointStore.getInstance().isUserEnoughPoint(PointFee.viewProfile)) {
                        PointStore.getInstance().showDialogUserNotEnoughPoint(context);
                        return;
                    }
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    //Log.e("kiemtra", user + " ");
                    intent.putExtra("user", user);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            switch (message.getType()) {
                case Message.text:
                    imgCover.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    messageText.setVisibility(View.VISIBLE);
                    messageText.setText(message.getMsg());
                    viewBubble.setEnabled(false);
                    break;
                case Message.image:
                    imageView.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.GONE);
                    final String mediaID = message.getParam().get("media_id").toString();
                    final boolean isCover = message.getShowed() == 0 && true ;/*!Metadata.getInstance().getData().getSupporter().getId().equals(getID());*/

                    if (isCover) {
                        imgCover.setVisibility(View.VISIBLE);
                        calulate(imgCover);
                    } else {
                        imgCover.setVisibility(View.GONE);
                    }
                    if (mediaID != null && !mediaID.isEmpty()) {
                        setImageAvatar(context, imageView,
                                createImageUrlThumbnail(mediaID),
                                isCover
                        );
                        viewBubble.setEnabled(true);
                        viewBubble.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isCover) {
                                    if (PointStore.getInstance().isUserEnoughPoint(PointFee.viewImageChatting)) {
                                        SocketClient.getInstance().openImage(message);
                                        String url = createImageUrlOpen(mediaID, message.getMsgID(),
                                                message.getShowed(), true);
                                        //Log.e("Open image::", url);
                                        Intent intent = new Intent(context, MessagePhotoViewActivity.class);
                                        intent.putExtra("avatar_url", url);
                                        intent.putExtra("consumePoint", true);
                                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        message.setShowed(1);
                                        imgCover.setVisibility(View.GONE);
                                        setImageAvatar(context, imageView,
                                                createImageUrlThumbnail(mediaID),
                                                false
                                        );
                                    } else {
                                        PointStore.getInstance().showDialogUserNotEnoughPoint(context);
                                    }
                                    return;
                                } else {
                                    String url = createImageUrlOpen(mediaID, message.getMsgID(),
                                            message.getShowed(), true);
                                    //Log.e("Open image::", url);
                                    Intent intent = new Intent(context, MessagePhotoViewActivity.class);
                                    intent.putExtra("avatar_url", url);
                                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }

                    break;
                case Message.location:
                    imageView.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.GONE);
                    final String mLat = message.getParam().get("lat").toString();
                    final String mLong = message.getParam().get("long").toString();
                    final boolean isCoverImage = message.getShowed() == 0 && true; /*!Metadata.getInstance().getData().getSupporter().getId().equals(getID());*/
                    if (isCoverImage) {
                        imgCover.setVisibility(View.VISIBLE);
                        calulate(imgCover);
                    }
                    if (mLat != null && mLong != null) {
                        setImageMap(context, imageView, createMapUrl(mLat, mLong), isCoverImage);
                        viewBubble.setEnabled(true);
                        viewBubble.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isCoverImage) {
                                    if (PointStore.getInstance().isUserEnoughPoint(PointFee.viewLocationChatting)) {
                                        Intent intent = new Intent(context, MapsActivity.class);
                                        intent.putExtra("uID", message.getuID());
                                        intent.putExtra("msgID", message.getMsgID());
                                        intent.putExtra("lat", mLat);
                                        intent.putExtra("long", mLong);
                                        intent.putExtra("consumePoint", true);
                                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        message.setShowed(1);
                                        imgCover.setVisibility(View.GONE);
                                        setImageMap(context, imageView, createMapUrl(mLat, mLong), false);
                                    } else {
                                        PointStore.getInstance().showDialogUserNotEnoughPoint(context);
                                    }
                                    return;
                                } else {
                                    Intent intent = new Intent(context, MapsActivity.class);
                                    intent.putExtra("uID", message.getuID());
                                    intent.putExtra("msgID", message.getMsgID());
                                    intent.putExtra("lat", mLat);
                                    intent.putExtra("long", mLong);
                                    intent.putExtra("consumePoint", false);
                                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }
                    break;
            }
        }
    }

    private String createImageUrlThumbnail(String mediaID) {
        SharedPreferences sharedPref = context.getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        return Config.API_URL + "media/stream?" +
                "id=" + mediaID +
                "&token=" + token +
                "&size=" + "medium" +
                "&thumbnail=" + true;
    }

    private String createImageUrlOpen(String mediaID, String msgID, int showed, boolean notMe) {
        SharedPreferences sharedPref = context.getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        String url = Config.API_URL + "media/stream?" +
                "id=" + mediaID +
                "&token=" + token +
                "&size=" + "large" +
                "&stream=" + true +
                "&seq=" + msgID;
        if (showed == 0 && notMe)
            url = url + "&thumbnail=" + false;
        else
            url = url + "&thumbnail=" + true;
        //Log.e("kiemtra", url);
        return url;
    }

    @NotNull
    private String createMapUrl(String mLat, String mLong) {
        return "https://maps.googleapis.com/maps/api/staticmap" +
                "?location=" + mLat + "," + mLong +
                "&zoom=15" +
                "&size=600x450" +
                "&maptype=roadmap" +
                "&markers=color:red%7C" + mLat + "," + mLong +
                "&key=" + Config.KEY_MAP_STATIC;
    }

    private void setImageAvatar(Context context, final ImageView imageAvatar, String imageUrl, boolean isCover) {
        //Log.e("Image Url:", imageUrl);

        try {
            LazyHeaders.Builder headers = new LazyHeaders.Builder();
            final HashMap<String, String> hashMap = HttpHeaders.getInstance().headers();
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null)
                    headers.addHeader(entry.getKey(), entry.getValue());
            }

            GlideUrl url = new GlideUrl(imageUrl, headers.build());

            //Log.e("Header Image:", url.getHeaders().toString());

            CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
            progressDrawable.setStrokeWidth(5f);
            progressDrawable.setCenterRadius(30f);
            progressDrawable.setColorSchemeColors(context.getColor(R.color.colorMain));
            progressDrawable.start();

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(progressDrawable)
                    .override(900, 675)
                    .centerCrop();

            if (isCover)
                Glide.with(context).load(url)
                        .transition(withCrossFade())
                        .thumbnail(0.6f)
                        .apply(options)
                        .apply(bitmapTransform(new BlurTransformation(10, 3)))
                        .into(imageAvatar);
            else Glide.with(context).load(url)
                    .transition(withCrossFade())
                    .thumbnail(0.6f)
                    .apply(options)
                    .into(imageAvatar);
        } catch (Exception ignored) {
        }
    }

    private void setImageMap(Context context, final ImageView imageAvatar, String imageUrl, boolean isCover) {
        //Log.e("Map Static Url:", imageUrl);
        try {

            GlideUrl url = new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .build());

            CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
            progressDrawable.setStrokeWidth(5f);
            progressDrawable.setCenterRadius(30f);
            progressDrawable.setColorSchemeColors(context.getColor(R.color.colorMain));
            progressDrawable.start();

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(progressDrawable)
                    .override(900, 675)
                    .centerCrop();

            if (isCover)
                Glide.with(context).load(url)
                        .transition(withCrossFade())
                        .thumbnail(0.6f)
                        .apply(options)
                        .apply(bitmapTransform(new BlurTransformation(10, 3)))
                        .into(imageAvatar);
            else
                Glide.with(context).load(url)
                        .transition(withCrossFade())
                        .thumbnail(0.6f)
                        .apply(options)
                        .into(imageAvatar);
        } catch (Exception ignored) {
        }

    }

    public void setList(List<Message> list) {
        this.mMessageList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<Message> newList) {
        int lastIndex = mMessageList.size();
        mMessageList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }
}
