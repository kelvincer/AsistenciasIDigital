package com.idigital.asistenciasidigital.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idigital.asistenciasidigital.R;
import com.idigital.asistenciasidigital.model.DetailReport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by USUARIO on 09/05/2017.
 */

public class DetailReportAdapter extends RecyclerView.Adapter<DetailReportAdapter.ViewHolder> {

    List<DetailReport> detailReports;

    public DetailReportAdapter(List<DetailReport> reports) {
        detailReports = reports;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_report_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bindItem(detailReports.get(position));
    }

    @Override
    public int getItemCount() {
        return detailReports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nameQuarter_txv)
        TextView nameQuarterTxv;
        @BindView(R.id.enter_txv)
        TextView enterTxv;
        @BindView(R.id.exit_txv)
        TextView exitTxv;
        @BindView(R.id.hora_txv)
        TextView horaTxv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(DetailReport detailReport) {

            nameQuarterTxv.setText(detailReport.getName());
            enterTxv.setText(detailReport.getIngreso());
            exitTxv.setText(detailReport.getSalida());
            horaTxv.setText(detailReport.getHorabruta());
        }
    }
}
