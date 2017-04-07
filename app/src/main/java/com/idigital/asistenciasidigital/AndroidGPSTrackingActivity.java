package com.idigital.asistenciasidigital;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AndroidGPSTrackingActivity extends AppCompatActivity {

    private final String TAG = AndroidGPSTrackingActivity.class.getSimpleName();
    // GPSTracker class
    GPSTracker gps;
    @BindView(R.id.btnShowLocation)
    Button btnShowLocation;
    @BindView(R.id.distance_txt)
    TextView distanceTxt;
    @BindView(R.id.distance2_txt)
    TextView distance2Txt;
    private Location idLocation = new Location("IDigital");
    private double distance;
    int ACCESS_COARSE_LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_gpstracking);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermissionAccessCoarseLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasPermissionAccessCoarseLocation)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_PERMISSION_REQUEST_CODE);
        }

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        idLocation.setLatitude(-12.0954204);
        idLocation.setLongitude(-77.0261567);

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(AndroidGPSTrackingActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    Location location = new Location("My location");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);

                    Location locationNetwork = gps.getNetworkLocation();
                    if(locationNetwork != null) {
                        distance = idLocation.distanceTo(locationNetwork);
                        distanceTxt.setText("Location network: " + String.valueOf(distance));
                        Log.i(TAG, "Network location: " + locationNetwork.toString());
                    }

                    Location locationGPS = gps.getGpsLocation();
                    if (locationGPS != null) {
                        float distance2 = idLocation.distanceTo(locationGPS);
                        distance2Txt.setText("Location GPS: " + String.valueOf(distance2));
                        Log.i(TAG, "GPS location: " + locationGPS.toString());
                    }

                    // \n is for new line
                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_COARSE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                Log.i(TAG, "permission granted");
            } else {
                Log.d(TAG, "permission denied");
            }
        }
    }
}
