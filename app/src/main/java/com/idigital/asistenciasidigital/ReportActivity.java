package com.idigital.asistenciasidigital;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.idigital.asistenciasidigital.adapter.RecyclerReportAdapter;
import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.database.DatabaseHelper;
import com.idigital.asistenciasidigital.database.UserDao;
import com.idigital.asistenciasidigital.listener.OnItemClickListener;
import com.idigital.asistenciasidigital.model.ShortReport;
import com.idigital.asistenciasidigital.model.User;
import com.idigital.asistenciasidigital.response.ShortReportResponse;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.util.SimpleDividerItemDecoration;
import com.idigital.asistenciasidigital.view.AlertDialogView;
import com.idigital.asistenciasidigital.view.ProgressDialogView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity implements OnItemClickListener {

    private static final String TAG = ReportActivity.class.getSimpleName();

    @BindView(R.id.ryv_report)
    RecyclerView ryvReport;
    ProgressDialogView progressView;
    DatabaseHelper helper;
    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        helper = new DatabaseHelper(this);
        userDao = new UserDao(helper);
        getSupportActionBar().setTitle(getResources().getString(R.string.report_title));
        fetchReport();
    }

    @Override
    public void onItemClick(ShortReport shortReport) {
        Intent intent = new Intent(this, DetailReportActivity.class);
        intent.putExtra(Constants.shortReport, shortReport);
        startActivity(intent);
    }

    private void showProgressDialog() {

        progressView = new ProgressDialogView(this);
        progressView.setMessage("Conectando...");
        progressView.showProgressDialog();
    }

    private void fetchReport() {

        showProgressDialog();
        new TestAndFetchReportAsyncTask().execute();
    }

    private void requestReport() {

        //PreferenceManager preferenceManager = new PreferenceManager(this);
        //String idUser = preferenceManager.getString(Constants.USER_ID, "null");

        User user = userDao.findUserByLoggedIn();

        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<ShortReportResponse> call = service.postAttendanceUser(user.getUserId());
        call.enqueue(new Callback<ShortReportResponse>() {
            @Override
            public void onResponse(Call<ShortReportResponse> call, Response<ShortReportResponse> response) {

                progressView.dismissDialog();
                if (response.isSuccessful()) {
                    ShortReportResponse responseList = response.body();
                    if (responseList.getCode() == 0)
                        fillRecyclerView(responseList.getData(), responseList.getMessage());
                    else if (responseList.getCode() == 1) {
                        showAlertDialog(responseList.getMessage());
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.report_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i(TAG, response.message());
                }
                Log.i(TAG, response.raw().toString());
            }

            @Override
            public void onFailure(Call<ShortReportResponse> call, Throwable t) {
                t.printStackTrace();
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.service_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fillRecyclerView(List<ShortReport> data, String message) {

        if (data.size() == 0) {
            AlertDialogView.showInternetAlertDialog(this, message);
            return;
        }

        ryvReport.setLayoutManager(new LinearLayoutManager(this));
        ryvReport.setAdapter(new RecyclerReportAdapter(data, this));
        ryvReport.addItemDecoration(new SimpleDividerItemDecoration(this));
    }


    private class TestAndFetchReportAsyncTask extends TestConnectionAsyncTask {

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                return;
            }
            requestReport();
        }
    }

    private void showAlertDialog(String message) {
        AlertDialogView.showInternetAlertDialog(this, message);
    }
}
