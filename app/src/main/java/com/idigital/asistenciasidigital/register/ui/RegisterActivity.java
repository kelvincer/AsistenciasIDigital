package com.idigital.asistenciasidigital.register.ui;

import android.Manifest;
import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.idigital.asistenciasidigital.PreferenceManager;
import com.idigital.asistenciasidigital.R;
import com.idigital.asistenciasidigital.ReportActivity;
import com.idigital.asistenciasidigital.TestConnectionAsyncTask;
import com.idigital.asistenciasidigital.adapter.RecyclerEventAdapter;
import com.idigital.asistenciasidigital.database.DatabaseHelper;
import com.idigital.asistenciasidigital.database.UserDao;
import com.idigital.asistenciasidigital.model.User;
import com.idigital.asistenciasidigital.register.RegisterPresenter;
import com.idigital.asistenciasidigital.register.RegisterPresenterImpl;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.util.LocationUtil;
import com.idigital.asistenciasidigital.util.SimpleDividerItemDecoration;
import com.idigital.asistenciasidigital.util.Util;
import com.idigital.asistenciasidigital.view.DialogView;
import com.idigital.asistenciasidigital.view.ProgressDialogView;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements RegisterView {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    @BindView(R.id.event_ryv)
    RecyclerView eventRyv;
    @BindView(R.id.delete_btn)
    Button deleteBtn;
    @BindView(R.id.enter_btn)
    Button enterBtn;
    @BindView(R.id.enter_launch_btn)
    Button enterLaunchBtn;
    @BindView(R.id.exit_launch_btn)
    Button exitLaunchBtn;
    @BindView(R.id.exit_btn)
    Button exitBtn;
    @BindView(R.id.time_enter_txv)
    TextView timeEnterTxv;
    @BindView(R.id.time_enter_launch_txv)
    TextView timeEnterLaunchTxv;
    @BindView(R.id.time_exit_launch_txv)
    TextView timeExitLaunchTxv;
    @BindView(R.id.time_exit_txv)
    TextView timeExitTxv;
    private int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private ProgressDialogView progressView;
    private RecyclerEventAdapter eventAdapter;
    private boolean hasPermissionAccessFineLocation;
    private RegisterPresenter presenter;
    private int category, activeButton;
    private PreferenceManager preferenceManager;
    DatabaseHelper helper;
    UserDao userDao;
    User userLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getResources().getString(R.string.registro_title));
        getSupportActionBar().setElevation(0);
        setUpEventsRecyclerview();
        presenter = new RegisterPresenterImpl(this, this);
        presenter.onCreate();
        preferenceManager = new PreferenceManager(this);
        helper = new DatabaseHelper(this);
        userDao = new UserDao(helper);

        userLoggedIn = userDao.findUserByLoggedIn();
        activeButton = userLoggedIn.getActiveButton();
        updateButton();
        updateTimeTextViews();

        progressView = new ProgressDialogView(this);
        progressView.setMessage("Conectando...");

        checkPassLogin();

        // This is optional
        if (checkPlayServices())
            Log.i(TAG, "tiene play service");
        else
            Log.i(TAG, "No tiene play service");
    }

    private void updateTimeTextViews() {

        timeEnterTxv.setText(userLoggedIn.getTimeOne());
        timeExitLaunchTxv.setText(userLoggedIn.getTimeTwo());
        timeEnterLaunchTxv.setText(userLoggedIn.getTimeThree());
        timeExitTxv.setText(userLoggedIn.getTimeFour());
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

            case R.id.item_1:
                logoutAndClose();
                break;
            case R.id.item_2:
                navigateToReportActivity();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToReportActivity() {
        startActivity(new Intent(this, ReportActivity.class));
    }

    @OnClick({R.id.enter_btn, R.id.enter_launch_btn, R.id.exit_launch_btn, R.id.exit_btn, R.id.delete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.enter_btn:
                clearTextViewAndUserTime();
                registerMovement();
                category = Constants.LABOR_ENTER;
                break;
            case R.id.exit_launch_btn:
                registerMovement();
                category = Constants.LAUNCH_EXIT;
                break;
            case R.id.enter_launch_btn:
                registerMovement();
                category = Constants.LAUNCH_ENTER;
                break;
            case R.id.exit_btn:
                registerMovement();
                category = Constants.LABOR_EXIT;
                break;
            case R.id.delete_btn:
                eventAdapter.clearList();
                break;
        }
    }

    private void clearTextViewAndUserTime() {

        timeEnterTxv.setText("");
        timeExitTxv.setText("");
        timeEnterLaunchTxv.setText("");
        timeExitLaunchTxv.setText("");

        userLoggedIn.setTimeOne(null);
        userLoggedIn.setTimeTwo(null);
        userLoggedIn.setTimeThree(null);
        userLoggedIn.setTimeFour(null);

        saveUserOnDB(userLoggedIn);

        /*preferenceManager.clearKeyPreference(Constants.TIME_1);
        preferenceManager.clearKeyPreference(Constants.TIME_2);
        preferenceManager.clearKeyPreference(Constants.TIME_3);
        preferenceManager.clearKeyPreference(Constants.TIME_4);*/
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
        DialogView.showDialog(this, message, Constants.SUCCESS_DIALOG, null);
    }

    @Override
    public void updateList(String message) {
        eventAdapter.addNewEvent(message);
    }

    @Override
    public void updateTextView(String time) {

        switch (activeButton) {

            case 1:
                timeEnterTxv.setText(time);
                userLoggedIn.setTimeOne(time);
                //preferenceManager.putString(Constants.TIME_1, time);
                break;
            case 2:
                timeExitLaunchTxv.setText(time);
                userLoggedIn.setTimeTwo(time);
                //preferenceManager.putString(Constants.TIME_3, time);
                break;
            case 3:
                timeEnterLaunchTxv.setText(time);
                userLoggedIn.setTimeThree(time);
                //preferenceManager.putString(Constants.TIME_2, time);
                break;
            case 4:
                timeExitTxv.setText(time);
                userLoggedIn.setTimeFour(time);
                //preferenceManager.putString(Constants.TIME_4, time);
                break;
            default:
                throw new RuntimeException("Invalid active button number");
        }

        saveUserOnDB(userLoggedIn);
    }

    @Override
    public void enableButton() {

        if (activeButton < 4) {
            activeButton++;
            updateButton();
        } else {
            activeButton = 1;
            updateButton();
            //preferenceManager.clearKeyPreference(Constants.TOKEN);
            userLoggedIn.setToken(null);
            userDao.insertUser(userLoggedIn);
        }
    }

    public void showLocationSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Localización");
        alertDialog.setMessage(getResources().getString(R.string.alert_location_message));

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
                //String token = preferenceManager.getString(Constants.TOKEN, null);
                String token = userLoggedIn.getToken();
                if (token == null) {
                    token = Util.generateToken();
                    userLoggedIn.setToken(token);
                    userDao.insertUser(userLoggedIn);
                }
                presenter.sendRegister(category, token);
            } else {
                progressView.dismissDialog();
                showLocationSettingsAlert();
            }
        }
    }

    private void logoutAndClose() {

        preferenceManager.putBoolean(Constants.LOGGED_IN, false);
        userLoggedIn.setLoggedIn(false);
        saveUserOnDB(userLoggedIn);
        //preferenceManager.clearKeyPreference(Constants.USER_EMAIL);
        //preferenceManager.clearKeyPreference(Constants.USER_PASSWORD);
        finish();
    }

    public void updateButton() {

        Button[] buttons = {enterBtn, exitLaunchBtn, enterLaunchBtn, exitBtn};

        for (int i = 0; i < buttons.length; i++) {
            if ((activeButton - 1) == i) {
                buttons[i].setEnabled(true);
                buttons[i].setBackgroundColor(ContextCompat.getColor(this, R.color.button_enable_color));
            } else {
                buttons[i].setEnabled(false);
                buttons[i].setBackgroundColor(ContextCompat.getColor(this, R.color.button_disable_color));
            }
        }

        userLoggedIn.setActiveButton(activeButton);
        saveUserOnDB(userLoggedIn);
    }

    private void showUpdateAppVersionDialog(String message) {

        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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

        alertDialog.show();*/

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.update_dialog);
        Button acceptBtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button cancelBtn = (Button) dialog.findViewById(R.id.dialogCancelOK);
        TextView messageTxv = (TextView) dialog.findViewById(R.id.message_txv);
        messageTxv.setText(message);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPermissionForLocation();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                requestPermissionForLocation();
            }
        });

        dialog.show();
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

    private void saveUserOnDB(User user) {
        userDao.insertUser(user);
    }

}
