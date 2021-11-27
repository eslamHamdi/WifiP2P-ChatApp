package com.eslam.wifip2pchat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

public class ChatBroadCastReciever extends BroadcastReceiver {

    private   WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private P2PChatActivity activity;





    public ChatBroadCastReciever(WifiP2pManager manager,WifiP2pManager.Channel channel, P2PChatActivity activity)
    {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
//            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
//            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//                activity.setIsWifiP2pEnabled(true);
//            } else {
//                activity.setIsWifiP2pEnabled(false);
//            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {


            if(manager != null)
            {
                manager.requestPeers(channel,activity.peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if(manager != null) {
                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {

                    // We are connected with the other device, request connection
                    // info to find group owner IP

                    manager.requestConnectionInfo(channel, activity.connectionInfoListener);

                }
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {


        }

    }
    }
