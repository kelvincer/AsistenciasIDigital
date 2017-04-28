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
import com.idigital.asistenciasidigital.response.PlaceResponse;
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

        /*
        if (!ConnectionUtil.isOnline()) {
            showInternetAlertDialog();
            return;
        }*/

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
                    if (placeResponse.getError()) {
                        Toast.makeText(getApplicationContext(), "Error en el servicio", Toast.LENGTH_SHORT).show();
                    } else {
                        saveDataListOnDatabase(placeResponse.getData());
                        navigateToActivity();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void navigateToActivity() {

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        boolean loggedIn = preferenceManager.getBoolean(Constants.LOGGED_IN, false);

        if (loggedIn) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

    private void saveDataListOnDatabase(List<Place> data) {

        DatabaseHelper helper = new DatabaseHelper(this);
        PlaceDao placeDao = new PlaceDao(helper);
        placeDao.insertPlaceList(data);
    }

    public void showInternetAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Internet");
        alertDialog.setMessage("No tienes conexión a internet");

        alertDialog.setPositiveButton("Cerrar App", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.show();
    }

    private class TestAndFetchAsyncTask extends TestConnectionAsyncTask {

        @Override
        protected void onPostExecute(Boolean result) {
            // Activity 1 GUI stuff
            super.onPostExecute(result);
            if (!result) {
                showInternetAlertDialog();
                return;
            }
            fetchPlaces();
        }
    }
}
