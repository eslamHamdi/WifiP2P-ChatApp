package com.eslam.wifip2pchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;

import com.eslam.wifip2pchat.databinding.ActivityP2pChatBinding;

public class P2PChatActivity extends AppCompatActivity {

  private   WifiP2pManager manager;
   private WifiP2pManager.Channel channel;
   private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private ActivityP2pChatBinding binding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this,R.layout.activity_p2p_chat);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new ChatBroadCastReciever(manager, channel, this);
        createIntentFilter();

       turnOn_OffWifi();

    }

    private void turnOn_OffWifi() {
        binding.onOff.setOnClickListener(listener -> {

        })

        ;
    }


    private  void createIntentFilter()
    {
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void setIsWifiP2pEnabled(boolean b) {
    }
}
