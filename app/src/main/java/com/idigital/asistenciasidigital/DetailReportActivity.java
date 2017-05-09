package com.idigital.asistenciasidigital;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.idigital.asistenciasidigital.adapter.DetailReportAdapter;
import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.model.DetailReport;
import com.idigital.asistenciasidigital.model.ShortReport;
import com.idigital.asistenciasidigital.response.DetailReportResponse;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.util.SimpleDividerItemDecoration;
import com.idigital.asistenciasidigital.view.ProgressDialogView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailReportActivity extends AppCompatActivity {

    private static final String TAG = DetailReportActivity.class.getSimpleName();
    @BindView(R.id.detail_ryv)
    RecyclerView detailRyv;
    @BindView(R.id.name_txv)
    TextView nameTxv;
    private ProgressDialogView progressView;
    ShortReport shortReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report);
        ButterKnife.bind(this);
        shortReport = (ShortReport) getIntent().getSerializableExtra(Constants.shortReport);

        getSupportActionBar().setTitle(getResources().getString(R.string.detalle));
        fetchDetailReport();
    }

    private void fetchDetailReport() {

        showProgressDialog();
        new TestAndFetchReportAsyncTask().execute();
    }

    private void postDetailFromCloud() {

        progressView.setMessage("Obteniendo reporte...");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String idUser = preferenceManager.getString(Constants.USER_ID, "null");
        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<DetailReportResponse> call = service.postAttendanceDetail(idUser, "07/05/2017");
        call.enqueue(new Callback<DetailReportResponse>() {
            @Override
            public void onResponse(Call<DetailReportResponse> call, Response<DetailReportResponse> response) {

                progressView.dismissDialog();
                if (response.isSuccessful()) {
                    DetailReportResponse responseList = response.body();
                    if (!responseList.getError())
                        fillRecyclerView(responseList.getData());
                    else {
                        Toast.makeText(getApplicationContext(), "Error cargando reporte", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.i(TAG, response.raw().toString());
            }

            @Override
            public void onFailure(Call<DetailReportResponse> call, Throwable t) {
                t.printStackTrace();
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), "Fallas en servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillRecyclerView(List<DetailReport> data) {

        nameTxv.setText(shortReport.getNombre());
        detailRyv.setAdapter(new DetailReportAdapter(data));
        detailRyv.setLayoutManager(new LinearLayoutManager(this));
        detailRyv.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private void showProgressDialog() {

        progressView = new ProgressDialogView(this);
        progressView.setMessage("Conectando...");
        progressView.showProgressDialog();
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
            postDetailFromCloud();
        }
    }
}