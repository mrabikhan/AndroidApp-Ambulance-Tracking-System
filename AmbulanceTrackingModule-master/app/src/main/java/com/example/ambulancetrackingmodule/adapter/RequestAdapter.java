package com.example.ambulancetrackingmodule.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ambulancetrackingmodule.R;
import com.example.ambulancetrackingmodule.activity.Patient.MapsActivity;
import com.example.ambulancetrackingmodule.interfaces.RequestItemClick;
import com.example.ambulancetrackingmodule.model.RequestModel;
import com.example.ambulancetrackingmodule.utils.Constants;
import com.example.ambulancetrackingmodule.utils.SharedPrefManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.requestViewHolder> {
    Context context;
    List<RequestModel> list;
    RequestItemClick mListeners;

    public RequestAdapter(Context context, List<RequestModel> list,RequestItemClick mListeners) {
        this.context = context;
        this.list = list;
        this.mListeners = mListeners;
    }

    @NonNull
    @Override
    public requestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_layout, parent, false);
        return new requestViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull requestViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        holder.time.setText(list.get(position).getTime());
        try {
            Geocoder gc = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gc.getFromLocation(list.get(position).getLat(), list.get(position).getLongi(), 1);
            String add = addresses.get(0).getAddressLine(0);
            holder.address.setText(add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListeners.requestItemClick(list.get(position).getLat(),list.get(position).getLongi());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class requestViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, address;

        public requestViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.p_name);
            time = itemView.findViewById(R.id.p_time);
            address = itemView.findViewById(R.id.p_add);
        }
    }
}
