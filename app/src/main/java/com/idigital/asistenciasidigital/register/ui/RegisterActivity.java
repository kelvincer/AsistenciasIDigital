package com.idigital.asistenciasidigital.register.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.idigital.asistenciasidigital.BuildConfig;
import com.idigital.asistenciasidigital.PreferenceManager;
import com.idigital.asistenciasidigital.R;
import com.idigital.asistenciasidigital.ReportActivity;
import com.idigital.asistenciasidigital.TestConnectionAsyncTask;
import com.idigital.asistenciasidigital.adapter.RecyclerEventAdapter;
import com.idigital.asistenciasidigital.register.RegisterPresenter;
import com.idigital.asistenciasidigital.register.RegisterPresenterImpl;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.util.LocationUtil;
import com.idigital.asistenciasidigital.util.SimpleDividerItemDecoration;
import com.idigital.asistenciasidigital.util.Util;
import com.idigital.asistenciasidigital.view.AlertDialogView;
import com.idigital.asistenciasidigital.view.ProgressDialogView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements RegisterView {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    @BindView(R.id.event_ryv)
    RecyclerView eventRyv;
    @BindView(R.id.delete_btn)
    Button deleteBtn;
    private int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private ProgressDialogView progressView;
    private RecyclerEventAdapter eventAdapter;
    private boolean hasPermissionAccessFineLocation;
    private PreferenceManager preferenceManager;
    private RegisterPresenter presenter;
    private int category;
    private String movement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getResources().getString(R.string.registro));
        setUpEventsRecyclerview();
        presenter = new RegisterPresenterImpl(this, this);
        presenter.onCreate();

        preferenceManager = new PreferenceManager(this);

        progressView = new ProgressDialogView(this);
        progressView.setMessage("Conectando...");

        checkPassLogin();

        // This is optional
        if (checkPlayServices())
            Log.i(TAG, "tiene play service");
        else
            Log.i(TAG, "No tiene play service");
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.createGoogleApiClient();
                hasPermissionAccessFineLocation = true;

            } else {
                Log.d(TAG, "permission denied");
                hasPermissionAccessFineLocation = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item1:
                logoutAndClose();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.enter_btn, R.id.enter_launch_btn, R.id.exit_launch_btn, R.id.exit_btn, R.id.delete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.exit_btn:
                registerMovement();
                movement = Constants.SALIDA;
                category = Constants.LABORAL;
                break;
            case R.id.enter_btn:
                registerMovement();
                movement = Constants.INGRESO;
                category = Constants.LABORAL;
                break;
            case R.id.enter_launch_btn:
                registerMovement();
                movement = Constants.INGRESO;
                category = Constants.ALMUERZO;
                break;
            case R.id.exit_launch_btn:
                registerMovement();
                movement = Constants.SALIDA;
                category = Constants.ALMUERZO;
                break;
            case R.id.delete_btn:
                eventAdapter.clearList();
                break;
        }
    }

    private void registerMovement() {

        if (!hasPermissionAccessFineLocation) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        showProgressDialog();
        new TestAndGetLocationAsyncTask().execute();
    }

    @Override
    public void showProgressDialog() {
        progressView.showProgressDialog();
    }

    @Override
    public void hideProgressDialog() {
        progressView.dismissDialog();
    }

    @Override
    public void setProgressMessage(String message) {
        progressView.setMessage(message);
    }

    @Override
    public void showAlert(String message) {
        AlertDialogView.showInternetAlertDialog(this, message);
    }

    @Override
    public void updateList(String message) {
        eventAdapter.addNewEvent(message);
    }

    public void showLocationSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Localización");
        alertDialog.setMessage("Localización no esta habilitado. ¿Deseas ir al menú de configuración?");

        alertDialog.setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void setUpEventsRecyclerview() {

        eventAdapter = new RecyclerEventAdapter();
        eventRyv.setAdapter(eventAdapter);
        eventRyv.setLayoutManager(new LinearLayoutManager(this));
        eventRyv.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private void requestPermissionForLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermissionAccessFineLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasPermissionAccessFineLocation)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            else {
                presenter.createGoogleApiClient();
            }
        } else {
            hasPermissionAccessFineLocation = true;
            presenter.createGoogleApiClient();
        }
    }

    /*private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }*/

    private boolean checkPlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private class TestAndGetLocationAsyncTask extends TestConnectionAsyncTask {

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                eventAdapter.addNewEvent("No hay conexión a internet");
                return;
            }

            eventAdapter.addNewEvent("Hay conexión a internet");
            if (LocationUtil.isLocationServicesAvailable(getApplicationContext())) {

                progressView.setMessage("Obteniendo tu ubicación");
                presenter.sendRegister(movement, category);

            } else {
                progressView.dismissDialog();
                showLocationSettingsAlert();
            }
        }
    }

    private void logoutAndClose() {

        preferenceManager.putBoolean(Constants.LOGGED_IN, false);
        preferenceManager.clearKeyPreference(Constants.USER_EMAIL);
        preferenceManager.clearKeyPreference(Constants.USER_PASSWORD);
        finish();
    }

    private void showUpdateAppVersionDialog(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alerta");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(R.string.alert_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestPermissionForLocation();
            }
        });

        alertDialog.setNegativeButton(R.string.alert_aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                requestPermissionForLocation();
            }
        });

        alertDialog.show();
    }

    private void checkPassLogin() {

        boolean passLogin = getIntent().getBooleanExtra(Constants.PASS_FOR_LOGIN, false);

        if (passLogin) {
            requestPermissionForLocation();
        } else {
            boolean versionUpdated = preferenceManager.getBoolean(Constants.VERSION_UPDATE, true);
            if (!versionUpdated) {
                String message = getIntent().getStringExtra(Constants.FETCH_VERSION_MESSAGE);
                showUpdateAppVersionDialog(message);
            } else {
                requestPermissionForLocation();
            }
        }
    }
}
