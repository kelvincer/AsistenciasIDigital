package com.idigital.asistenciasidigital;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.idigital.asistenciasidigital.adapter.RecyclerEventAdapter;
import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.database.DatabaseHelper;
import com.idigital.asistenciasidigital.database.PlaceDao;
import com.idigital.asistenciasidigital.model.Place;
import com.idigital.asistenciasidigital.response.RegisterResponse;
import com.idigital.asistenciasidigital.util.ConnectionUtil;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.util.LocationUtil;
import com.idigital.asistenciasidigital.util.SimpleDividerItemDecoration;
import com.idigital.asistenciasidigital.util.Util;
import com.idigital.asistenciasidigital.view.ProgressDialogView;

import java.io.IOException;
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
    @BindView(R.id.event_ryv)
    RecyclerView eventRyv;
    @BindView(R.id.textClock)
    TextClock textClock;
    @BindView(R.id.delete_btn)
    Button deleteBtn;
    private GoogleApiClient googleApiClient;
    private int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location idLocation = new Location("IDigital");
    private Location userLocation;
    private Map<String, Double> sortedDistancedMap;
    ProgressDialogView progressView;
    String movement;
    Handler handler;
    Runnable myRunnable;
    RecyclerEventAdapter eventAdapter;
    String closestPlaceId;
    List<Place> places;
    boolean hasPermissionAccessFineLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        requestPermissionForLocation();

        setUpEventsRecyclerview();

        // This is optional
        if (checkPlayServices())
            Log.i(TAG, "tiene play service");
        else
            Log.i(TAG, "No tiene play service");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

        calculateDistancesAndSorted(location);
        Map.Entry<String, Double> firstMapEntry = getFirstMapEntry();
        Double mininDistance = firstMapEntry.getValue();
        closestPlaceId = firstMapEntry.getKey();

        if (mininDistance.intValue() <= getRadiusFromClosestPlace()) {

            Log.i(TAG, "radius " + getRadiusFromClosestPlace());
            Log.i(TAG, "El usuario esta dentro de rango");
            eventAdapter.addNewEvent("Estas dentro de la sede: " + mininDistance.intValue() + " m. del centro");
            setUpForSendRegister(location);
        } else {
            Log.i(TAG, "El usuario esta fuera de rango");
            eventAdapter.addNewEvent("Estas fuera de la sede: " + mininDistance.intValue() + " m. del centro");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                createGoogleApiClient();
                hasPermissionAccessFineLocation = true;
                //googleApiClient.connect();
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

        switch (item.getItemId()){

            case R.id.item1:
                logoutAndClose();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.register_in_btn, R.id.register_out_btn, R.id.see_btn, R.id.delete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register_in_btn:
                movement = "ingreso";
                registerMovement();
                break;
            case R.id.register_out_btn:
                movement = "salida";
                registerMovement();
                break;
            case R.id.see_btn:
                startActivity(new Intent(getApplicationContext(), ReportActivity.class));
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

        /*showProgressDialog();
        //if (ConnectionUtil.haveNetworkConnection(this)) {
        if (ConnectionUtil.isOnline()) {

            eventAdapter.addNewEvent("Hay conexión a internet");
            if (LocationUtil.isLocationServicesAvailable(this)) {

                progressView.setMessage("Obteniendo tu ubicación");

                if (googleApiClient != null && !googleApiClient.isConnected())
                    googleApiClient.connect();

            } else {
                showLocationSettingsAlert();
            }
        } else {
            progressView.dismissDialog();
            Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_SHORT).show();
            eventAdapter.addNewEvent("No hay conexión a internet");
        }*/

        showProgressDialog();
        new TestAndRegisterAsyncTask().execute();
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
        locationRequest.setInterval(1000 * 6);
        locationRequest.setFastestInterval(1000 * 3);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        waitForUserLocation();
    }

    public void showProgressDialog() {

        progressView = new ProgressDialogView(this);
        progressView.setMessage("Conectando...");
        progressView.showProgressDialog();
    }


    private void waitForUserLocation() {

        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                eventAdapter.addNewEvent("Registro insatisfactorio");
                stopLocationUpdates();
                progressView.dismissDialog();
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

    private void sendMovementToServer() {

        Map.Entry<String, Double> firstId = getFirstMapEntry();
        if (firstId == null)
            return;
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String userId = preferenceManager.getString(Constants.USER_ID, "null");
        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<RegisterResponse> call = service.postRegistry(userId, firstId.getKey(), movement,
                userLocation.getLatitude(), userLocation.getLongitude());
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                progressView.dismissDialog();
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();
                    if (!registerResponse.getError()) {
                        Toast.makeText(getApplicationContext(), "Registro satisfactorio", Toast.LENGTH_SHORT).show();
                        eventAdapter.addNewEvent("Registro satisfactorio: " + movement);
                    } else {
                        Toast.makeText(getApplicationContext(), "Registro insatisfactorio", Toast.LENGTH_SHORT).show();
                        eventAdapter.addNewEvent("Registro insatisfactorio");
                    }
                }
                Log.i(TAG, response.raw().toString());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Registro fallido", Toast.LENGTH_SHORT).show();
                eventAdapter.addNewEvent("Registro fallido");
                progressView.dismissDialog();
            }
        });
    }

    private void calculateDistancesAndSorted(Location myLocation) {

        if (places == null) {
            DatabaseHelper helper = new DatabaseHelper(this);
            PlaceDao placeDao = new PlaceDao(helper);
            places = placeDao.getAllPlaces();
        }

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

        stopLocationUpdates();
        progressView.setMessage("Enviando registro");
        userLocation = location;
        sendMovementToServer();
        handler.removeCallbacks(myRunnable);
    }

    private Map.Entry<String, Double> getFirstMapEntry() {

        if (sortedDistancedMap == null)
            return null;
        Map.Entry<String, Double> firstEntry = sortedDistancedMap.entrySet().iterator().next();
        return firstEntry;
    }

    private void setUpEventsRecyclerview() {

        eventAdapter = new RecyclerEventAdapter();
        eventRyv.setAdapter(eventAdapter);
        eventRyv.setLayoutManager(new LinearLayoutManager(this));
        eventRyv.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private int getRadiusFromClosestPlace() {

        for (Place p : places) {
            if (p.getIdHeadquarter().equals(closestPlaceId)) {
                String stringRadio = p.getRadio();
                Double doubleRadio = Double.parseDouble(stringRadio);
                return doubleRadio.intValue();
            }
        }

        return 0;
    }

    public void stopLocationUpdates() {

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private void requestPermissionForLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermissionAccessFineLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasPermissionAccessFineLocation)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            else
                createGoogleApiClient();
        } else {
            hasPermissionAccessFineLocation = true;
            createGoogleApiClient();
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

    private class TestAndRegisterAsyncTask extends TestConnectionAsyncTask {

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                eventAdapter.addNewEvent("No hay conexión a internet");
                return;
            }

            eventAdapter.addNewEvent("Hay conexión a internet");
            if (LocationUtil.isLocationServicesAvailable(getApplicationContext())) {

                progressView.setMessage("Obteniendo tu ubicación");

                if (googleApiClient != null && !googleApiClient.isConnected())
                    googleApiClient.connect();

            } else {
                progressView.dismissDialog();
                showLocationSettingsAlert();
            }
        }
    }

    private void logoutAndClose() {

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.putBoolean(Constants.LOGGED_IN, false);

        finish();
    }
}
