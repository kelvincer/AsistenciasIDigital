package com.idigital.asistenciasidigital;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.database.DatabaseHelper;
import com.idigital.asistenciasidigital.database.PlaceDao;
import com.idigital.asistenciasidigital.model.Place;
import com.idigital.asistenciasidigital.register.ui.*;
import com.idigital.asistenciasidigital.response.LoginResponse;
import com.idigital.asistenciasidigital.response.PlaceResponse;
import com.idigital.asistenciasidigital.response.VersionResponse;
import com.idigital.asistenciasidigital.util.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getPlacesFromServer();
    }

    private void getPlacesFromServer() {

        new TestAndFetchAsyncTask().execute();
    }

    private void fetchPlaces() {

        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<PlaceResponse> call = service.getPlaces();
        call.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {

                Log.i(TAG, response.raw().toString());
                if (response.isSuccessful()) {

                    PlaceResponse placeResponse = response.body();
                    if (placeResponse.getCode() == 0) {

                        saveDataListOnDatabase(placeResponse.getData());
                        fetchVersion();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error en el servicio places", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void checkLoguedIn() {

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        boolean loggedIn = preferenceManager.getBoolean(Constants.LOGGED_IN, false);

        if (loggedIn) {
            automaticLogin();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void saveDataListOnDatabase(List<Place> data) {

        DatabaseHelper helper = new DatabaseHelper(this);
        PlaceDao placeDao = new PlaceDao(helper);
        placeDao.insertPlaceList(data);
    }

    private class TestAndFetchAsyncTask extends TestConnectionAsyncTask {

        @Override
        protected void onPostExecute(Boolean result) {
            // Activity 1 GUI stuff
            super.onPostExecute(result);
            if (!result) {
                showInternetAlertDialog(getResources().getString(R.string.splash_dialog));
                return;
            }
            fetchPlaces();
        }
    }

    private void automaticLogin() {

        final PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        String password = preferenceManager.getString(Constants.USER_PASSWORD, "");
        String email = preferenceManager.getString(Constants.USER_EMAIL, "");
        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<LoginResponse> call = service.postLogin(email, password, BuildConfig.VERSION_CODE);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.i(TAG, response.raw().toString());
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.getError() == 0) {
                        navigateToRegisterActivity();
                    } else {
                        updatePreferenceManager();
                        navigateToLoginActivity();
                        Toast.makeText(getApplicationContext(), "Autenticación interna incorrecta", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void navigateToLoginActivity() {

        startActivity(new Intent(this, LoginActivity.class));
    }

    private void navigateToRegisterActivity() {

        startActivity(new Intent(this, com.idigital.asistenciasidigital.register.ui.RegisterActivity.class));
    }

    private void updatePreferenceManager(){
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.putBoolean(Constants.LOGGED_IN, false);
        preferenceManager.clearKeyPreference(Constants.USER_PASSWORD);
        preferenceManager.clearKeyPreference(Constants.USER_EMAIL);
    }

    public void showInternetAlertDialog(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alerta");
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void fetchVersion(){

        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<VersionResponse> call = service.getVersion();
        call.enqueue(new Callback<VersionResponse>() {
            @Override
            public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
                if(response.isSuccessful()){

                    VersionResponse versionResponse = response.body();
                    if(versionResponse.getCode() == 0){

                        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                        preferenceManager.putString(Constants.ACTUAL_VERSION, versionResponse.getData().getValue());
                        checkLoguedIn();

                    }else{
                        Toast.makeText(SplashActivity.this, "Error en fetch version", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<VersionResponse> call, Throwable t) {

                t.printStackTrace();
            }
        });
    }
}
