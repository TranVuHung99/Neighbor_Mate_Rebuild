package com.example.neighbormaterebuild.store;

import com.example.neighbormaterebuild.ui.chat.ChatFragment;

import io.reactivex.subjects.BehaviorSubject;

public class MessageStore {
    private static MessageStore mInstance = new MessageStore();

    public static MessageStore getInstance() {
        if (mInstance == null) {
            synchronized (MessageStore.class) {
                if (null == mInstance) {
                    mInstance = new MessageStore();
                }
            }
        }
        return mInstance;
    }

    private MessageStore() {
    }

    public BehaviorSubject<Integer> unreadAllSubject = BehaviorSubject.create();
    public BehaviorSubject<Integer> unreadMessageSubject = BehaviorSubject.create();
    public BehaviorSubject<Integer> unreadNoticeSubject = BehaviorSubject.create();
    public BehaviorSubject<Integer> unreadCampaignSubject = BehaviorSubject.create();

    public ChatFragment.OnRefreshTab listener;
}
