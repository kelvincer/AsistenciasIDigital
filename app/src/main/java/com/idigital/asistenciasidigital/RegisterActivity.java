package com.idigital.asistenciasidigital;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.database.DatabaseHelper;
import com.idigital.asistenciasidigital.database.PlaceDao;
import com.idigital.asistenciasidigital.model.Place;
import com.idigital.asistenciasidigital.response.RegisterResponse;
import com.idigital.asistenciasidigital.util.ConnectionUtil;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.util.DateUtil;
import com.idigital.asistenciasidigital.util.LocationUtil;
import com.idigital.asistenciasidigital.util.Util;
import com.idigital.asistenciasidigital.view.ProgressDialogView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    @BindView(R.id.register_in_btn)
    Button checkInBtn;
    @BindView(R.id.register_out_btn)
    Button checkOutBtn;
    private GoogleApiClient googleApiClient;
    private int ACCESS_COARSE_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private Location idLocation = new Location("IDigital");
    private Location userLocation;
    private Map<String, Double> sortedDistancedMap;
    ProgressDialogView progressView;
    String movement;
    Handler handler;
    Runnable myRunnable;
    Place currentQuarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermissionAccessCoarseLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasPermissionAccessCoarseLocation)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_PERMISSION_REQUEST_CODE);
            else
                createGoogleApiClient();
        } else {

            createGoogleApiClient();
        }

        /*idLocation.setLatitude(-12.0959996);
        idLocation.setLongitude(-77.024008);*/

        /*idLocation.setLatitude(-12.0954204);
        idLocation.setLongitude(-77.0261567);*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*if (googleApiClient != null && !googleApiClient.isConnected())
            googleApiClient.connect();*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (googleApiClient.isConnected())
            googleApiClient.disconnect();*/
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i(TAG, "onConnected " + bundle);
        Location l = null;
        try {
            l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (l != null) {
            Log.i(TAG, "lat " + l.getLatitude());
            Log.i(TAG, "lng " + l.getLongitude());
        }

        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(TAG, "lat changed " + location.getLatitude());
        Log.i(TAG, "lng changed " + location.getLongitude());

        Location location1 = new Location("MyPosition");
        location1.setLatitude(location.getLatitude());
        location1.setLongitude(location.getLongitude());

        Double mininDistance = 0.0;
        if (sortedDistancedMap == null) {
            calculateDistancesAndSorted(location);
            Map.Entry<String, Double> firstMapEntry = getFirstMapEntry();
            mininDistance = firstMapEntry.getValue();
        }

        /*float[] results = new float[1];
        Location.distanceBetween(
                idLocation.getLatitude(), idLocation.getLongitude(),
                location.getLatitude(), location.getLongitude(), results);

        Log.i(TAG, "Distance " + mininDistance);
        Log.i(TAG, "Second distance " + results[0]);
        Log.i(TAG, "Third distance " + meterDistanceBetweenPoints(idLocation.getLatitude(),
                idLocation.getLongitude(), location.getLatitude(), location.getLongitude()));

        LatLng latLng1 = new LatLng(location.getLatitude(), location1.getLongitude());
        LatLng latLng2 = new LatLng(idLocation.getLatitude(), idLocation.getLongitude());

        Log.i(TAG, "Fourth distance " + SphericalUtil.computeDistanceBetween(latLng1, latLng2));*/
        //Toast.makeText(this, "location changed", Toast.LENGTH_SHORT).show();
        if (mininDistance.intValue() <= 20) {

            Log.i(TAG, "El usuario esta dentro de rango");
            setUpForSendRegister(location);
        } else {
            Log.i(TAG, "El usuario esta fuera de rango");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_COARSE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                createGoogleApiClient();
                //googleApiClient.connect();
            } else {
                Log.d(TAG, "permission denied");
            }
        }
    }

    @OnClick({R.id.register_in_btn, R.id.register_out_btn, R.id.see_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register_in_btn:
                movement = "entrada";
                sendRegisterToServer();
                break;
            case R.id.register_out_btn:
                movement = "salida";
                sendRegisterToServer();
                break;
            case R.id.see_btn:
                startActivity(new Intent(getApplicationContext(), ReportActivity.class));
                break;
        }
    }

    private void sendRegisterToServer() {

        if (ConnectionUtil.checkWifiOnAndConnected(this)) {

            if (LocationUtil.isLocationServicesAvailable(this)) {

                showProgressDialog();

                if (googleApiClient != null && !googleApiClient.isConnected())
                    googleApiClient.connect();

                waitForUserLocation();
            } else {
                showLocationSettingsAlert();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexión Wifi", Toast.LENGTH_SHORT).show();
        }
    }

    private void createGoogleApiClient() {
        Log.d(TAG, "createGoogleApiClient()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void startLocationUpdate() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(0);
        //locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void showProgressDialog() {

        progressView = new ProgressDialogView(this);
        progressView.setMessage("Obteniendo tu ubicación");
        progressView.showProgressDialog();
    }


    private void waitForUserLocation() {

        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                googleApiClient.disconnect();
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), "Estás fuera de rango, no puedes registrarte", Toast.LENGTH_SHORT).show();
            }
        };

        handler.postDelayed(myRunnable, 20000);
    }

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f / Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public void showLocationSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Localización");
        alertDialog.setMessage("GPS no esta habilitado. Deseas ir al menú de configuración?");

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

    private void registerEvent(String movement) {

        Map.Entry<String, Double> firstId = getFirstMapEntry();
        if (firstId == null)
            return;
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String userId = preferenceManager.getString(Constants.USER_ID, "null");
        IDigitalService service = IDigitalClient.getClubService();
        Call<RegisterResponse> call = service.postRegistry( userId, firstId.getKey(), DateUtil.getDateTime(),
                movement, userLocation.getLatitude(), userLocation.getLongitude());
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                progressView.dismissDialog();
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();
                    if (!registerResponse.getError())
                        Toast.makeText(getApplicationContext(), "Registro satisfactorio", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Registro inválido", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, response.raw().toString());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                t.printStackTrace();
                progressView.dismissDialog();
            }
        });
    }

    private void calculateDistancesAndSorted(Location myLocation) {

        DatabaseHelper helper = new DatabaseHelper(this);
        PlaceDao placeDao = new PlaceDao(helper);
        List<Place> places = placeDao.getAllPlaces();

        Map<String, Double> map = new HashMap<>();

        for (Place place : places) {

            Location placeLocation = new Location("Place");
            placeLocation.setLatitude(Double.parseDouble(place.getLatitude()));
            placeLocation.setLongitude(Double.parseDouble(place.getLongitude()));
            double distance = myLocation.distanceTo(placeLocation);
            map.put(place.getIdHeadquarter(), distance);
        }
        sortedDistancedMap = Util.sortMapByValue(map);
    }

    private void setUpForSendRegister(Location location) {

        progressView.setMessage("Enviando registro");
        userLocation = location;
        registerEvent(movement);
        handler.removeCallbacks(myRunnable);
        googleApiClient.disconnect();
    }

    private Map.Entry<String, Double> getFirstMapEntry() {

        if (sortedDistancedMap == null)
            return null;
        Map.Entry<String, Double> firstEntry = sortedDistancedMap.entrySet().iterator().next();
        return firstEntry;
    }
}
