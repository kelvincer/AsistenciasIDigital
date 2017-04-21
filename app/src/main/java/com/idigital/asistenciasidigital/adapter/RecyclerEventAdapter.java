package com.idigital.asistenciasidigital.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idigital.asistenciasidigital.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by USUARIO on 12/04/2017.
 */

public class RecyclerEventAdapter extends RecyclerView.Adapter<RecyclerEventAdapter.CustomViewHolder> {

    List<String> events;


    public RecyclerEventAdapter() {
        events = new ArrayList<>();
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bindItem(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void addNewEvent(String event) {

        events.add(0, event);
        notifyDataSetChanged();
    }

    public void clearList(){
        events.clear();
        notifyDataSetChanged();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.event_txv)
        TextView txvEvent;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindItem(String event) {

            txvEvent.setText(event);
        }
    }
}
