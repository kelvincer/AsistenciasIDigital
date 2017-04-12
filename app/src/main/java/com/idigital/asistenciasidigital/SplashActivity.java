package com.idigital.asistenciasidigital;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
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
import com.idigital.asistenciasidigital.util.ConnectionUtil;

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

        if (!ConnectionUtil.haveNetworkConnection(this)) {
            Toast.makeText(getApplicationContext(), "No estás conectado a internet", Toast.LENGTH_SHORT).show();
            return;
        }

        IDigitalService service = IDigitalClient.getClubService();
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
                        gotoLoginActivity();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void gotoLoginActivity() {

        Log.i(TAG, "wifi name " + getWifiName(this));
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void saveDataListOnDatabase(List<Place> data) {

        DatabaseHelper helper = new DatabaseHelper(this);
        PlaceDao placeDao = new PlaceDao(helper);
        placeDao.insertPlaceList(data);
    }

    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
        return null;
    }
}
