package com.example.neighbormaterebuild.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.store.MessageStore;
import com.example.neighbormaterebuild.ui.chat.items.CampaignFragment;
import com.example.neighbormaterebuild.ui.chat.items.ChatUserFragment;
import com.example.neighbormaterebuild.ui.chat.items.NotificationFragment;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class ChatFragment extends Fragment {

    private TextView txtTitle;
    private ImageButton imgbtnRefresh;
    private TextView badgeRoster, badgeCampaign, badgeNotice;
    private Button btnRoster, btnCampaign, btnNotice;

    private Button[] btn = new Button[3];
    private Button btn_unfocus;
    private int[] btn_id = {R.id.btnRoster, R.id.btnCampaign, R.id.btnNotice};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtTitle = view.findViewById(R.id.toolbar_title);
        imgbtnRefresh = view.findViewById(R.id.imgbtnRefresh);
        badgeRoster = view.findViewById(R.id.badgeRoster);
        badgeCampaign = view.findViewById(R.id.badgeCampaign);
        badgeNotice = view.findViewById(R.id.badgeNotice);
        btnRoster = view.findViewById(R.id.btnRoster);
        btnCampaign = view.findViewById(R.id.btnCampaign);
        btnNotice = view.findViewById(R.id.btnNotice);

        for (int i = 0; i < btn.length; i++) {
            btn[i] = view.findViewById(btn_id[i]);
            btn[i].setBackgroundTintList(requireActivity().getResources().getColorStateList(R.color.colorGrey));
        }

        btn_unfocus = btn[0];

        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.pagerContainerChat, new ChatUserFragment(), "roster");
        ft.commit();
        txtTitle.setText("トーク");
        setFocus(btn_unfocus, btn[0]);
        onBadgeChangeListener();

        imgbtnRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MessageStore.getInstance().listener.clickPosition();
            }
        });

        btnRoster.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                setFocus(btn_unfocus, btn[0]);
                ft.addToBackStack(null);
                ft.replace(R.id.pagerContainerChat, new ChatUserFragment(), "roster");
                ft.commit();
                txtTitle.setText("トーク");
            }
        });

        btnNotice.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                setFocus(btn_unfocus, btn[2]);
                ft.replace(R.id.pagerContainerChat, new NotificationFragment(), "notice");
                ft.commit();
                txtTitle.setText("お知らせ");
            }
        });

        btnCampaign.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                setFocus(btn_unfocus, btn[1]);
                ft.replace(R.id.pagerContainerChat, new CampaignFragment(), "campaign");
                ft.commit();
                txtTitle.setText("キャンペーン");
            }
        });
    }

    @Override
    public void onDestroyView() {
        Fragment fragment1 = requireActivity().getSupportFragmentManager().findFragmentByTag("roster");
        Fragment fragment2 = requireActivity().getSupportFragmentManager().findFragmentByTag("campaign");
        Fragment fragment3 = requireActivity().getSupportFragmentManager().findFragmentByTag("notice");
        if(fragment1 != null) {
            fragment1.onDestroy();
        }
        if(fragment2 != null) {
            fragment2.onDestroy();
        }
        if(fragment3 != null) {
            fragment3.onDestroy();
        }
        super.onDestroyView();
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    public void onBadgeChangeListener() {
        MessageStore.getInstance().unreadMessageSubject.hide().subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                if (integer > 0) {
                    badgeRoster.setVisibility(View.VISIBLE);
                    badgeRoster.setText(integer.toString());
                } else {
                    badgeRoster.setVisibility(View.GONE);
                }
                badgeRoster.requestLayout();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        MessageStore.getInstance().unreadNoticeSubject.hide().subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                if (integer > 0) {
                    badgeNotice.setVisibility(View.VISIBLE);
                    badgeNotice.setText(integer.toString());
                } else {
                    badgeNotice.setVisibility(View.GONE);
                }
                badgeNotice.requestLayout();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        MessageStore.getInstance().unreadCampaignSubject.hide().subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                if (integer > 0) {
                    badgeCampaign.setVisibility(View.VISIBLE);
                    badgeCampaign.setText(integer.toString());
                } else {
                    badgeCampaign.setVisibility(View.GONE);
                }
                badgeCampaign.requestLayout();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorGrey));
        btn_focus.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorMain));
        this.btn_unfocus = btn_focus;
    }

    public interface OnRefreshTab {
        void clickPosition();
    }
}
