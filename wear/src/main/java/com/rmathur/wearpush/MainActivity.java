package com.rmathur.wearpush;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private RelativeLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register the local broadcast receiver, defined in step 3.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
        setAmbientEnabled();
        mTextView = (TextView) findViewById(R.id.text);
        mMainLayout = (RelativeLayout) findViewById(R.id.background_layout);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            // Display message in UI
            mTextView.setText(message);
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        mTextView.setTextColor(Color.WHITE);
        mMainLayout.setBackgroundColor(Color.BLACK);
        mTextView.getPaint().setAntiAlias(false);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        mTextView.setTextColor(Color.BLACK);
        mMainLayout.setBackgroundColor(Color.WHITE);
        mTextView.getPaint().setAntiAlias(true);
    }
}