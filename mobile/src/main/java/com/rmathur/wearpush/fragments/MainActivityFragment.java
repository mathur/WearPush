package com.rmathur.wearpush.fragments;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
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
import com.rmathur.wearpush.adapters.HistoryAdapter;
import com.rmathur.wearpush.models.Push;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivityFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    String TAG = MainActivityFragment.class.getSimpleName();

    Activity a;
    private HistoryAdapter adapter;

    GoogleApiClient googleClient;
    private final String WEAR_MESSAGE_PATH = "/message_path";

    List<Push> pushes;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        a = this.getActivity();
        final ListView historyList = (ListView) v.findViewById(R.id.lstHistory);

        final FloatingActionMenu floatingMenu = (FloatingActionMenu) v.findViewById(R.id.newPush);
        floatingMenu.setClosedOnTouchOutside(true);
        floatingMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //floatingMenu.toggle(true);
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
                                pushes.clear();
                                pushes = Push.listAll(Push.class);
                                Collections.sort(pushes);
                                adapter = new HistoryAdapter(getActivity(), R.layout.item_history, pushes);
                                historyList.setAdapter(adapter);
                                dialog.dismiss();
                                //floatingMenu.toggle(true);
                            }
                        }).show();
            }
        });

//        final FloatingActionButton mNewPushText = new com.github.clans.fab.FloatingActionButton(getActivity());
//        mNewPushText.setButtonSize(com.github.clans.fab.FloatingActionButton.SIZE_MINI);
//        mNewPushText.setLabelText(getString(R.string.new_push_text));
//        mNewPushText.setImageResource(android.R.drawable.ic_input_add);
//        mNewPushText.setImageTintList(ColorStateList.valueOf(Color.WHITE));
//        floatingMenu.addMenuButton(mNewPushText);
//        mNewPushText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(a)
//                        .title("Enter Text")
//                        .content("Enter the text you want to send to your Android device")
//                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
//                        .input(null, null, new MaterialDialog.InputCallback() {
//                            @Override
//                            public void onInput(MaterialDialog dialog, CharSequence input) {
//                                sendMessage(WEAR_MESSAGE_PATH, input.toString());
//                                Push push = new Push(input.toString(), new Date());
//                                push.save();
//                                pushes.clear();
//                                pushes = Push.listAll(Push.class);
//                                Collections.sort(pushes);
//                                adapter = new HistoryAdapter(getActivity(), R.layout.item_history, pushes);
//                                historyList.setAdapter(adapter);
//                                dialog.dismiss();
//                                floatingMenu.toggle(true);
//                            }
//                        }).show();
//            }
//        });

//        final FloatingActionButton mNewPushImage = new com.github.clans.fab.FloatingActionButton(getActivity());
//        mNewPushImage.setButtonSize(com.github.clans.fab.FloatingActionButton.SIZE_MINI);
//        mNewPushImage.setLabelText(getString(R.string.new_push_image));
//        mNewPushImage.setImageResource(android.R.drawable.ic_input_add);
//        mNewPushImage.setImageTintList(ColorStateList.valueOf(Color.WHITE));
//        floatingMenu.addMenuButton(mNewPushImage);
//        mNewPushImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // nothing
//            }
//        });

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Push push = pushes.get(position);
                new MaterialDialog.Builder(a)
                        .title("Push again?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                sendMessage(WEAR_MESSAGE_PATH, push.getTitle().toString());
                                Push repeatPush = new Push(push.getTitle().toString(), new Date());
                                repeatPush.save();
                                pushes.clear();
                                pushes = Push.listAll(Push.class);
                                Collections.sort(pushes);
                                adapter = new HistoryAdapter(getActivity(), R.layout.item_history, pushes);
                                historyList.setAdapter(adapter);
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        historyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Push push = pushes.get(position);
                new MaterialDialog.Builder(a)
                        .title("Delete Push?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                push.delete();
                                pushes.clear();
                                pushes = Push.listAll(Push.class);
                                Collections.sort(pushes);
                                adapter = new HistoryAdapter(getActivity(), R.layout.item_history, pushes);
                                historyList.setAdapter(adapter);
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });

        pushes = Push.listAll(Push.class);
        Collections.sort(pushes);
        adapter = new HistoryAdapter(getActivity(), R.layout.item_history, pushes);
        historyList.setAdapter(adapter);

        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder(a)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, connectionResult.toString());
    }

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
                    Log.v(TAG, "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    // Log an error
                    Log.v(TAG, "ERROR: failed to send Message");
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
