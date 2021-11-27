package com.eslam.wifip2pchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.eslam.wifip2pchat.databinding.ActivityP2pChatBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class P2PChatActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

  private   WifiP2pManager manager;
   private WifiP2pManager.Channel channel;
   private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private ActivityP2pChatBinding binding;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private String[] deviceNames;
    ArrayAdapter<String> arrayAdapter;
    private final Integer REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33;
    private final Integer REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this,R.layout.activity_p2p_chat);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new ChatBroadCastReciever(manager, channel, this);
        createIntentFilter();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, new String[]{"empty"});
        binding.devicesList.setAdapter(arrayAdapter);

       turnOn_OffWifi();
       discoverPeers();
       listItemsListener();

    }

    private void listItemsListener() {

        binding.devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice device = peers.get(position);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                manager.connect(channel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        binding.status.setText("Connected");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(P2PChatActivity.this, "Connect failed. Retry.",
                                Toast.LENGTH_SHORT).show();
                        binding.status.setText("Not Connected");

                    }
                });



            }
        });
    }

    @SuppressLint("MissingPermission")
    private void discoverPeers() {
        binding.discover.setOnClickListener(listener -> {
            if(hasLocationPermissions(this.getApplicationContext())){
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(null, "onSuccess: succeded");
                        binding.status.setText("Searching For Nearby Devices");
                    }

                    @Override
                    public void onFailure(int i) {
                        binding.status.setText("Searching Failed!!");
                    }
                });
            }else
            {
                requestPermissions();
            }

        });
    }

    private void turnOn_OffWifi() {
        binding.onOff.setOnClickListener(listener -> {

            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivityForResult(intent,1);
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


    WifiP2pManager.PeerListListener  peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                if(!wifiP2pDeviceList.getDeviceList().equals(peers))
                {
                      peers.clear();
                      peers.addAll(wifiP2pDeviceList.getDeviceList());

                      deviceNames = new String[wifiP2pDeviceList.getDeviceList().size()];

                      for(int x = 0; x < deviceNames.length; x++)
                      {
                          deviceNames[x] = peers.get(x).deviceName;
                      }

                   arrayAdapter.addAll(deviceNames);
                      arrayAdapter.notifyDataSetChanged();

                }
                if(peers.size() == 0)
                {
                    binding.status.setText("No Devices Found!!");

                }

            }
        };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            String groupOwnerAddress =  wifiP2pInfo.groupOwnerAddress.getHostAddress();
            ;

            // After the group negotiation, we can determine the group owner
            // (server).
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                binding.status.setText("Host");
            } else if (wifiP2pInfo.groupFormed) {
                // The other device acts as the peer (client). In this case,
                // you'll want to create a peer thread that connects
                // to the group owner.
                binding.status.setText("Client");
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    Boolean hasLocationPermissions(Context context) {

        boolean permissionState;


       if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
             permissionState = EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
            );

        } else {
            permissionState = EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            );
        }

       return permissionState;
    }

    void requestPermissions()
    {
        if (hasLocationPermissions(this.getApplicationContext()))
        {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        {
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this feature.",
                    REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
        } else
        {
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this feature.",
                    REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            );
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        initiateActionListener();
    }

    @SuppressLint("MissingPermission")
    private void initiateActionListener() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(null, "onSuccess: succeded");
                binding.status.setText("Searching For Nearby Devices");
            }

            @Override
            public void onFailure(int i) {
                binding.status.setText("Searching Failed!!");
                Log.e(null, "onFailure:failed " );
            }
        });
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, Collections.singletonList(perms.toString())))
        {
            new AppSettingsDialog.Builder(this).build().show();
        } else
        {
            requestPermissions();
        }

    }


}


