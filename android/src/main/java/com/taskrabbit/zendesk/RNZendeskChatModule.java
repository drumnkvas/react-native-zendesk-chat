package com.taskrabbit.zendesk;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.prechat.PreChatForm;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;
import com.zopim.android.sdk.prechat.EmailTranscript;

import java.lang.String;
import java.util.ArrayList;
import java.util.Objects;

public class RNZendeskChatModule extends ReactContextBaseJavaModule {
    private ReactContext mReactContext;

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
        ZopimChat.init(key)
            .build();
    }

    @ReactMethod
    public void startChat(ReadableMap options) {
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
}
