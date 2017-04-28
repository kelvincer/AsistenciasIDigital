package com.idigital.asistenciasidigital;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.idigital.asistenciasidigital.adapter.RecyclerReportAdapter;
import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.model.Report;
import com.idigital.asistenciasidigital.response.ReportResponse;
import com.idigital.asistenciasidigital.util.ConnectionUtil;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.util.SimpleDividerItemDecoration;
import com.idigital.asistenciasidigital.view.ProgressDialogView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private static final String TAG = ReportActivity.class.getSimpleName();

    @BindView(R.id.ryv_report)
    RecyclerView ryvReport;
    ProgressDialogView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        fetchReport();
    }

    private void showProgressDialog() {

        progressView = new ProgressDialogView(this);
        progressView.setMessage("Conectando...");
        progressView.showProgressDialog();
    }

    private void fetchReport() {

        /*if(!ConnectionUtil.isOnline()){
            Toast.makeText(getApplicationContext(), "No estás conectado a internet", Toast.LENGTH_SHORT).show();
            return;
        }*/

        showProgressDialog();
        new TestAndFetchReportAsyncTask().execute();
    }

    private void requestReport() {

        PreferenceManager preferenceManager = new PreferenceManager(this);
        String idUser = preferenceManager.getString(Constants.USER_ID, "null");
        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<ReportResponse> call = service.postAllUserReport(idUser);
        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {

                progressView.dismissDialog();
                if (response.isSuccessful()) {
                    ReportResponse responseList = response.body();
                    if (!responseList.getError())
                        fillRecyclerView(responseList.getData());
                    else {
                        Toast.makeText(getApplicationContext(), "Error cargando reporte", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.i(TAG, response.raw().toString());
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                t.printStackTrace();
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), "Fallas en servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillRecyclerView(List<Report> data) {

        if (data.size() == 0) {
            Toast.makeText(getApplicationContext(), "No hay reporte que mostrar", Toast.LENGTH_LONG).show();
            return;
        }

        ryvReport.setLayoutManager(new LinearLayoutManager(this));
        ryvReport.setAdapter(new RecyclerReportAdapter(data));
        ryvReport.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private class TestAndFetchReportAsyncTask extends TestConnectionAsyncTask {

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), "No estás conectado a internet", Toast.LENGTH_SHORT).show();
                return;
            }
            requestReport();
        }
    }
}
