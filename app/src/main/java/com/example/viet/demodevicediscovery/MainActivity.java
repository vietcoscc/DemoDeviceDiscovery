package com.example.viet.demodevicediscovery;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static String SERVER_SERVICE_NAME = "Server";
    public static final String SERVER_SERVICE_TYPE = "_http._tcp.";
    public static final int SERVER_PORT = 8000;

    public static String CLIENT_SERVICE_NAME = "Client";
    public static String CLIENT_SERVICE_TYPE = "_http._tcp.";
    private NsdManager mNsdManager;
    private Button mBtnRegister;
    private int mCount = 0;
    private RecyclerView mRecyclerView;
    private ArrayList<NetworkInfo> mArrNetworkInfo = new ArrayList<>();
    private NetworkInfoRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNsdManager = (NsdManager) getSystemService(NSD_SERVICE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new NetworkInfoRecyclerViewAdapter(mArrNetworkInfo);
        mRecyclerView.setAdapter(mAdapter);

        mBtnRegister = (Button) findViewById(R.id.btnRegister);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerService(SERVER_PORT + mCount);
                mCount++;
            }
        });

    }

    private void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(SERVER_SERVICE_NAME);
        serviceInfo.setServiceType(SERVER_SERVICE_TYPE);
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
    }

    private NsdManager.RegistrationListener registrationListener = new NsdManager.RegistrationListener() {
        @Override
        public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
            Log.i(TAG, "onRegistrationFailed");
            String mServiceName = nsdServiceInfo.getServiceName();
            SERVER_SERVICE_NAME = mServiceName;
        }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
            Log.i(TAG, "onUnregistrationFailed");
        }

        @Override
        public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
            Log.i(TAG, "onServiceRegistered");
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
            Log.i(TAG, "onServiceUnregistered");
        }
    };
    private NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
        @Override
        public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
            Log.i(TAG, "onResolveFailed");
        }

        @Override
        public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
            Log.i(TAG, "onServiceResolved");
            NetworkInfo networkInfo = new NetworkInfo(nsdServiceInfo.getServiceName(),
                    nsdServiceInfo.getHost().getHostName(),
                    nsdServiceInfo.getPort() + "");
            mArrNetworkInfo.add(networkInfo);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }.start();
            Log.i(TAG, nsdServiceInfo.getServiceName());
            Log.i(TAG, nsdServiceInfo.getHost().getHostAddress());
            Log.i(TAG, nsdServiceInfo.getPort() + " ");
        }
    };

    private NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {
        @Override
        public void onStartDiscoveryFailed(String s, int i) {
            Toast.makeText(MainActivity.this, "onStartDiscoveryFailed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStopDiscoveryFailed(String s, int i) {
            Toast.makeText(MainActivity.this, "onStopDiscoveryFailed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryStarted(String s) {
            Toast.makeText(MainActivity.this, "onDiscoveryStarted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryStopped(String s) {
            Toast.makeText(MainActivity.this, "onDiscoveryStopped", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceFound(NsdServiceInfo nsdServiceInfo) {

            if (!nsdServiceInfo.getServiceType().equals(CLIENT_SERVICE_TYPE)) {
                Toast.makeText(MainActivity.this, " UNknown device type", Toast.LENGTH_SHORT).show();
            } else if (nsdServiceInfo.getServiceName().equals(CLIENT_SERVICE_NAME)) {
                Toast.makeText(MainActivity.this, "Same device", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "onServiceFound Diff Machine", Toast.LENGTH_SHORT).show();

            }
            mNsdManager.resolveService(nsdServiceInfo, resolveListener);
        }

        @Override
        public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
            Toast.makeText(MainActivity.this, "onServiceLost", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onPause() {
        if (mNsdManager != null) {
            mNsdManager.unregisterService(registrationListener);
            try {
                mNsdManager.stopServiceDiscovery(discoveryListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mNsdManager != null) {
//            registerService(SERVER_PORT);
            mNsdManager.discoverServices(SERVER_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mNsdManager != null) {
            mNsdManager.unregisterService(registrationListener);
            try {
                mNsdManager.stopServiceDiscovery(discoveryListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
