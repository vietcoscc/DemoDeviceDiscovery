package com.example.viet.demodevicediscovery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by viet on 09/08/2017.
 */

public class NetworkInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NetworkInfo> mArrNetworkInfo;

    public NetworkInfoRecyclerViewAdapter(ArrayList<NetworkInfo> arrNetworkInfo) {
        this.mArrNetworkInfo = arrNetworkInfo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_network_info_recycler_view, parent, false);
        return new NetworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NetworkViewHolder networkViewHolder = (NetworkViewHolder) holder;
        NetworkInfo networkInfo = mArrNetworkInfo.get(position);
        networkViewHolder.tvName.setText(networkInfo.getName());
        networkViewHolder.tvHost.setText(networkInfo.getHost());
        networkViewHolder.tvPort.setText(networkInfo.getPort());
    }

    @Override
    public int getItemCount() {
        return mArrNetworkInfo.size();
    }

    class NetworkViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvHost;
        TextView tvPort;

        public NetworkViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvHost = itemView.findViewById(R.id.tvHost);
            tvPort = itemView.findViewById(R.id.tvPort);
        }
    }
}
