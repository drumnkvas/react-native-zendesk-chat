package com.taskrabbit.zendesk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import java.util.Map;
import java.util.TreeMap;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Callback;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.Arguments;

// import com.zendesk.logger.Logger;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.api.Chat;
import com.zopim.android.sdk.api.ZopimChatApi;
import com.zopim.android.sdk.prechat.PreChatForm;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;
import com.zopim.android.sdk.prechat.EmailTranscript;
import com.zopim.android.sdk.model.ChattingStatus;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.android.sdk.data.observers.ChattingStatusObserver;
import com.zopim.android.sdk.data.observers.ChatItemsObserver;

import java.lang.String;
import java.util.ArrayList;
import java.util.Objects;

public class RNZendeskChatModule extends ReactContextBaseJavaModule {
    private ReactContext mReactContext;
    public ChattingStatus chatStatus;

    public RNZendeskChatModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNZendeskChatModule";
    }

    @ReactMethod
    public void setVisitorInfo(ReadableMap options) {
        VisitorInfo.Builder builder = new VisitorInfo.Builder();

        if (options.hasKey("name")) {
            builder.name(options.getString("name"));
        }
        if (options.hasKey("email")) {
            builder.email(options.getString("email"));
        }
        if (options.hasKey("phone")) {
            builder.phoneNumber(options.getString("phone"));
        }

        VisitorInfo visitorData = builder.build();

        ZopimChat.setVisitorInfo(visitorData);
    }

    @ReactMethod
    public void init(String key) {
        ZopimChat.init(key);
            // .build();

        ZopimChatApi.getDataSource().addChattingStatusObserver(new ChattingStatusObserver() {
            @Override
            protected void update(ChattingStatus nextChatStatus) {
                try {
                    // Log.i("OBESERVER: CHECK CHAT STATUS = %s", nextChatStatus.name());
                    sendEvent(mReactContext, "chatStatusChanged", nextChatStatus.name());
                    chatStatus = nextChatStatus;
                } catch (IllegalViewOperationException e) {
                    // Log.i("OBESERVER: STATUS CALLBACK FAILED", "");
                }
            }
        });

        // listen for chat log events
        ChatItemsObserver chatItemsObserver = new ChatItemsObserver(mReactContext) {
            @Override
            protected void updateChatItems(final TreeMap<String, RowItem> chatItems) {
                try {
                    sendEvent(mReactContext, "chatMessagesUpdated", String.valueOf(chatItems.size()));

                    // Log.i("OBESERVER: CHAT UPDATED", "");
                    for (Map.Entry<String, RowItem> entry: chatItems.entrySet()) {
                        RowItem chatRow = entry.getValue();
                        // Log.i("UPDATED CHAT ITEMS", "Key: " + entry.getKey() + ". Value: " + chatRow.getType().name() + " STRING: " + chatRow.toString());
                    }
                } catch (IllegalViewOperationException e) {
                    // Log.i("OBESERVER: STATUS CALLBACK FAILED", "");
                }
            }
        };
        ZopimChatApi.getDataSource().addChatLogObserver(chatItemsObserver);
    }

    @ReactMethod
    public void setPushToken(String token) {
        ZopimChatApi.setPushToken(token);
        // Log.i("SET PUSH TOKEN ON ANDROID 1 = %s", token);
    }

    @ReactMethod
    public String getChattingStatus() {
        ChattingStatus chatStatus = ZopimChatApi.getDataSource().getChattingStatus();
        // Log.i("CHECK CHAT STATUS = %s", chatStatus.name());
        return chatStatus.name();
    }

    @ReactMethod
    public void startChat(ReadableMap options) {
        AppCompatActivity currentActivity = (AppCompatActivity) getCurrentActivity();

        setVisitorInfo(options);

        PreChatForm preChatForm = new PreChatForm.Builder()
            .name(PreChatForm.Field.REQUIRED)
            .department(PreChatForm.Field.REQUIRED_EDITABLE)
            .email(PreChatForm.Field.REQUIRED)
            .phoneNumber(PreChatForm.Field.REQUIRED)
            .message(PreChatForm.Field.REQUIRED)
            .build();

        ZopimChat.SessionConfig config = new ZopimChat.SessionConfig()
            .preChatForm(preChatForm)
            .emailTranscript(EmailTranscript.DISABLED);

        if (options.hasKey("department")) {
            config.department(options.getString("department"));
        }
        if (options.hasKey("tags")) {
            ArrayList<Object> tags = options.getArray("tags").toArrayList();
            ArrayList<String> strings = new ArrayList<String>(tags.size());
            for (Object tag : tags) {
                String string = Objects.toString(tag, null);
                if (string != null) {
                    strings.add(string);
                }
            }
            config.tags(strings.toArray(new String[tags.size()]));
        }
        ZopimChatActivity.startActivity(getCurrentActivity(), config);
    }

    private void sendEvent(ReactContext reactContext, String eventName, String params) {
      mReactContext
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
          .emit(eventName, params);
    }


}
