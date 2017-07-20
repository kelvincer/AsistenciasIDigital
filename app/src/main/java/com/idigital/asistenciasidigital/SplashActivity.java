package com.idigital.asistenciasidigital;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.database.DatabaseHelper;
import com.idigital.asistenciasidigital.database.PlaceDao;
import com.idigital.asistenciasidigital.database.UserDao;
import com.idigital.asistenciasidigital.model.Place;
import com.idigital.asistenciasidigital.model.User;
import com.idigital.asistenciasidigital.response.LoginResponse;
import com.idigital.asistenciasidigital.response.PlaceResponse;
import com.idigital.asistenciasidigital.response.VersionResponse;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.view.DialogView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = SplashActivity.class.getSimpleName();
    private String fetchVersionServerMessage = "";
    PreferenceManager preferenceManager;
    DatabaseHelper helper;
    UserDao userDao;
    User userLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferenceManager = new PreferenceManager(getApplicationContext());
        helper = new DatabaseHelper(this);
        userDao = new UserDao(helper);
        userLoggedIn = userDao.findUserByLoggedIn();

        getPlacesFromServer();
    }

    private void getPlacesFromServer() {

        new TestConnectionAndFetchPlacesAsyncTask().execute();
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
                        Toast.makeText(getApplicationContext(), placeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void checkLoguedIn() {

        boolean loggedIn = preferenceManager.getBoolean(Constants.LOGGED_IN, false);

        if (loggedIn) {
            automaticLogin();
        } else {
            navigateToLoginActivity();
            finish();
        }
    }

    private void saveDataListOnDatabase(List<Place> data) {

        PlaceDao placeDao = new PlaceDao(helper);
        placeDao.insertPlaceList(data);
    }

    private class TestConnectionAndFetchPlacesAsyncTask extends TestConnectionAsyncTask {

        @Override
        protected void onPostExecute(Boolean result) {
            // Activity 1 GUI stuff
            super.onPostExecute(result);
            if (!result) {
                showInternetDialog();
                return;
            }
            fetchPlaces();
        }
    }

    private void showInternetDialog() {
        DialogView.showDialog(this, getResources().getString(R.string.splash_dialog), Constants.ALERT_DIALOG, this);
    }

    private void automaticLogin() {

        if (userLoggedIn == null) {
            Toast.makeText(this, "Error logged in user", Toast.LENGTH_SHORT).show();
            return;
        }

        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<LoginResponse> call = service.postLogin(userLoggedIn.getEmail(), userLoggedIn.getPassword());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.i(TAG, response.raw().toString());
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();

                    if (!loginResponse.getBlocking()) {

                        if (loginResponse.getCode() == 5) {
                            navigateToRegisterActivity();
                        } else if (loginResponse.getCode() == 6) {
                            deleteUserAndPreferenceManager();
                            navigateToLoginActivity();
                            Toast.makeText(getApplicationContext(), loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i(TAG, loginResponse.getMessage());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(this, LoginActivity.class);
        if (!fetchVersionServerMessage.isEmpty())
            intent.putExtra(Constants.FETCH_VERSION_MESSAGE, fetchVersionServerMessage);
        startActivity(intent);
    }

    private void navigateToRegisterActivity() {

        Intent intent = new Intent(this, com.idigital.asistenciasidigital.register.ui.RegisterActivity.class);
        if (!fetchVersionServerMessage.isEmpty())
            intent.putExtra(Constants.FETCH_VERSION_MESSAGE, fetchVersionServerMessage);
        startActivity(intent);
    }

    private void deleteUserAndPreferenceManager() {
        preferenceManager.putBoolean(Constants.LOGGED_IN, false);

        userDao.deleteUser(userLoggedIn);

        //userLoggedIn.setLoggedIn(false);
        //userDao.insertUser(userLoggedIn);

        //preferenceManager.clearKeyPreference(Constants.USER_PASSWORD);
        //preferenceManager.clearKeyPreference(Constants.USER_EMAIL);
    }

    private void fetchVersion() {

        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<VersionResponse> call = service.postVersion(BuildConfig.VERSION_CODE);
        call.enqueue(new Callback<VersionResponse>() {
            @Override
            public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
                if (response.isSuccessful()) {

                    VersionResponse versionResponse = response.body();
                    if (versionResponse.getCode() == 2) {
                        preferenceManager.putBoolean(Constants.VERSION_UPDATE, true);
                        checkLoguedIn();

                    } else if (versionResponse.getCode() == 3) {
                        preferenceManager.putBoolean(Constants.VERSION_UPDATE, false);
                        fetchVersionServerMessage = versionResponse.getMessage();
                        checkLoguedIn();

                    } else {
                        Log.i(TAG, versionResponse.getMessage());
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
