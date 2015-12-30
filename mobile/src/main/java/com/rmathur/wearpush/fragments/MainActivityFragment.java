package com.rmathur.wearpush.fragments;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.rmathur.wearpush.R;
import com.rmathur.wearpush.models.Push;

import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    String TAG = MainActivityFragment.class.getSimpleName();

    Activity a;

    GoogleApiClient googleClient;
    private final String WEAR_MESSAGE_PATH = "/message_path";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        a = this.getActivity();

        final FloatingActionMenu floatingMenu = (FloatingActionMenu) v.findViewById(R.id.newPush);
        floatingMenu.setClosedOnTouchOutside(true);
        floatingMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingMenu.toggle(true);
            }
        });

        final FloatingActionButton mNewPushText = new com.github.clans.fab.FloatingActionButton(getActivity());
        mNewPushText.setButtonSize(com.github.clans.fab.FloatingActionButton.SIZE_MINI);
        mNewPushText.setLabelText(getString(R.string.new_push_text));
        mNewPushText.setImageResource(android.R.drawable.ic_input_add);
        mNewPushText.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        floatingMenu.addMenuButton(mNewPushText);
        mNewPushText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(a)
                        .title("Enter Text")
                        .content("Enter the text you want to send to your Android device")
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                        .input(null, null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                sendMessage(WEAR_MESSAGE_PATH, input.toString());
                                Push push = new Push(input.toString(), new Date());
                                push.save();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        final FloatingActionButton mNewPushImage = new com.github.clans.fab.FloatingActionButton(getActivity());
        mNewPushImage.setButtonSize(com.github.clans.fab.FloatingActionButton.SIZE_MINI);
        mNewPushImage.setLabelText(getString(R.string.new_push_image));
        mNewPushImage.setImageResource(android.R.drawable.ic_input_add);
        mNewPushImage.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        floatingMenu.addMenuButton(mNewPushImage);
        mNewPushImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // nothing
            }
        });

        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder(a)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        List<Push> pushes = Push.listAll(Push.class);
        

        return v;
    }

    private void sendMessage(String path, String message) {
        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread(path, message).start();
    }

    // Connect to the data layer when the Activity starts
    @Override
    public void onStart() {
        super.onStart();
        googleClient.connect();
    }

    // Send a message when the data layer connection is successful.
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Connected to wearable device");
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    public void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    public class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "DONE");
                }
            });
        }
    }
}
