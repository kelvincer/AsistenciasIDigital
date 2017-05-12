package com.idigital.asistenciasidigital.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idigital.asistenciasidigital.R;
import com.idigital.asistenciasidigital.listener.OnItemClickListener;
import com.idigital.asistenciasidigital.model.ShortReport;
import com.idigital.asistenciasidigital.model.ShortReport2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by USUARIO on 06/04/2017.
 */

public class RecyclerReportAdapter extends RecyclerView.Adapter<RecyclerReportAdapter.CustomViewHolder> {

    List<ShortReport2> data;
    OnItemClickListener listener;


    public RecyclerReportAdapter(List<ShortReport2> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_list_item, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_list_item_2, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bindItem(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        /* @BindView(R.id.user_txv)
         TextView userTxv;
         @BindView(R.id.date_txv)
         TextView dateTxv;
         @BindView(R.id.movement_txv)
         TextView movementTxv;
         @BindView(R.id.total_time_txv)
         TextView totalTimeTxv;*/
        @BindView(R.id.name_txv)
        TextView nameTxv;
        @BindView(R.id.date_txv)
        TextView dateTxv;
        @BindView(R.id.sede_txv)
        TextView sedeTxv;
        @BindView(R.id.movement_txv)
        TextView movementTxv;
        View view;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            view = itemView;
        }

        private void bindItem(final ShortReport2 item) {

            nameTxv.setText(item.getNombre());
            dateTxv.setText(item.getFecha());
            sedeTxv.setText(item.getSede());
            movementTxv.setText(item.getMovimiento());
            /*userTxv.setText(item.getNombre());
            dateTxv.setText(item.getFecha());
            movementTxv.setText(item.getMovimientos());
            totalTimeTxv.setText(item.getTotalHoras());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });*/
        }
    }
}
