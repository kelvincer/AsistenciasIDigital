package com.idigital.asistenciasidigital.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idigital.asistenciasidigital.PreferenceManager;
import com.idigital.asistenciasidigital.R;
import com.idigital.asistenciasidigital.model.Report;
import com.idigital.asistenciasidigital.response.ReportResponse;
import com.idigital.asistenciasidigital.util.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by USUARIO on 06/04/2017.
 */

public class RecyclerReportAdapter extends RecyclerView.Adapter<RecyclerReportAdapter.CustomViewHolder> {

    List<Report> data;

    public RecyclerReportAdapter(List<Report> data) {
        this.data = data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_list_item, parent, false);
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

        @BindView(R.id.txv_user)
        TextView txvUser;
        @BindView(R.id.txv_date)
        TextView txvDate;
        @BindView(R.id.txv_movement)
        TextView txvMovement;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindItem(Report item) {

            txvUser.setText(getUserFullName());
            txvDate.setText(item.getDateAdd());
            txvMovement.setText(item.getMovement());
        }

        private String getUserFullName() {

            PreferenceManager preferenceManager = new PreferenceManager(txvDate.getContext());
            String name = preferenceManager.getString(Constants.USER_NAME, "null");
            String lastName = preferenceManager.getString(Constants.USER_LAST_NAME, "null");
            return name + " "  + lastName;
        }
    }
}
