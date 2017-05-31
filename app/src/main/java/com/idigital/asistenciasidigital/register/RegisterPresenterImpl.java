package com.idigital.asistenciasidigital.register;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.idigital.asistenciasidigital.PreferenceManager;
import com.idigital.asistenciasidigital.database.DatabaseHelper;
import com.idigital.asistenciasidigital.database.PlaceDao;
import com.idigital.asistenciasidigital.lib.EventBus;
import com.idigital.asistenciasidigital.lib.GreenRobotEventBus;
import com.idigital.asistenciasidigital.model.Place;
import com.idigital.asistenciasidigital.register.events.RegisterEvent;
import com.idigital.asistenciasidigital.register.location.GeolocationListener;
import com.idigital.asistenciasidigital.register.location.LocationManager;
import com.idigital.asistenciasidigital.register.ui.RegisterView;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.util.Util;
import com.idigital.asistenciasidigital.view.AlertDialogView;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USUARIO on 24/05/2017.
 */

public class RegisterPresenterImpl implements RegisterPresenter, GeolocationListener {

    private static final String TAG = RegisterPresenterImpl.class.getSimpleName();
    private Context context;
    private EventBus eventBus;
    private RegisterView registerView;
    private RegisterInteractor registerInteractor;
    private LocationManager locationManager;
    private List<Place> places;
    private Map<String, Double> sortedDistanceMap;
    private String closestPlaceId;
    private int attempNumber = 0;
    private String movement;
    private PreferenceManager preferenceManager;

    public RegisterPresenterImpl(Context context, RegisterView registerView) {

        this.context = context;
        this.registerView = registerView;
        this.eventBus = GreenRobotEventBus.getInstance();
        this.registerInteractor = new RegisterInteractorImpl();
        locationManager = new LocationManager(context, this);
        preferenceManager = new PreferenceManager(context);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onCreate() {
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        registerView = null;
        eventBus.unregister(this);
    }

    @Override
    public void sendRegister(String movement) {
        if (registerView != null) {
            registerView.showProgressDialog();
        }
        this.movement = movement;
        locationManager.connect();
    }

    @Override
    @Subscribe
    public void onEventMainThread(RegisterEvent event) {

        registerView.hideProgressDialog();

        switch (event.getEventType()) {
            case RegisterEvent.onSendEnterRegisterSuccess:
                onEnterRegisterSuccess(event.getMessage());
                break;
            case RegisterEvent.onSendExitRegisterSuccess:
                onExitRegisterSuccess(event.getMessage());
                break;
            case RegisterEvent.onSendRegisterError:
                onRegisterError(event.getMessage());
                break;
            case RegisterEvent.onSendRegisterFailure:
                onRegisterFailure();
                break;
            case RegisterEvent.onUserBlocking:
                onBlocking();
                break;
            default:
                throw new IllegalArgumentException("Event type Invalid");
        }
    }

    @Override
    public void locationChanged(Location location) {

        Log.i(TAG, "lat changed " + location.getLatitude());
        Log.i(TAG, "lng changed " + location.getLongitude());

        calculateDistancesAndSort(location);
        Map.Entry<String, Double> firstMapEntry = getFirstMapEntry();
        Double mininDistance = firstMapEntry.getValue();
        closestPlaceId = firstMapEntry.getKey();

        Place place = getClosestPlace();
        if (place == null) {
            Toast.makeText(context, "Error on place", Toast.LENGTH_SHORT).show();
            return;
        }

        Double placeRadio = Double.parseDouble(place.getRadio());
        if (mininDistance.intValue() <= placeRadio.intValue()) {

            handleUserInRange(place, mininDistance);
            setUpForSendRegister(location, Constants.NORMAL, mininDistance.intValue());
        } else {

            handleUserOutOfRange(place, mininDistance);
            if (attempNumber == 2) {
                setUpForSendRegister(location, Constants.OBSERVATION, mininDistance.intValue());
            } else {
                attempNumber++;
            }
        }
    }

    @Override
    public void createGoogleApiClient() {
        if (locationManager != null)
            locationManager.createGoogleApiClient();
    }

    private void onBlocking() {
        Log.i(TAG, "user blocking");
    }

    private void onRegisterFailure() {

        registerView.updateList("Registro Fallido");
    }

    private void onRegisterError(String message) {

        registerView.updateList(message);
        registerView.showAlert(message);
    }

    private void onExitRegisterSuccess(String message) {

        registerView.updateList(message);
        registerView.showAlert(message);
        registerView.updateButton(Constants.INGRESO);
    }

    private void onEnterRegisterSuccess(String message) {

        registerView.updateList(message);
        registerView.showAlert(message);
        registerView.updateButton(Constants.SALIDA);
    }

    private void calculateDistancesAndSort(Location myLocation) {

        if (places == null) {
            DatabaseHelper helper = new DatabaseHelper(context);
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
        sortedDistanceMap = new HashMap<>();
        sortedDistanceMap = Util.sortMapByValue(map);
        for (Map.Entry<String, Double> entry : sortedDistanceMap.entrySet()) {
            Log.i(TAG, entry.getKey() + "/" + entry.getValue());
        }
    }

    private Map.Entry<String, Double> getFirstMapEntry() {

        if (sortedDistanceMap == null)
            return null;
        Map.Entry<String, Double> firstEntry = sortedDistanceMap.entrySet().iterator().next();
        return firstEntry;
    }

    private Place getClosestPlace() {

        for (Place p : places) {
            if (p.getIdHeadquarter().equals(closestPlaceId)) {
                return p;
            }
        }
        return null;
    }

    private void handleUserInRange(Place place, Double mininDistance) {
        Log.i(TAG, "El usuario esta dentro de rango");
        Double placeRadio = Double.parseDouble(place.getRadio());
        locationManager.stopLocationUpdates();
        registerView.updateList("Dentro de: " + place.getName() + " Centro: " + mininDistance.intValue() + " Radio: " + placeRadio.intValue());
    }

    private void setUpForSendRegister(Location location, int flag, int distance) {

        registerView.setProgressMessage("Enviando registro");
        attempNumber = 0;
        if (movement.equalsIgnoreCase("ingreso")) {
            registerInteractor.sendEnterRegister(getUserId(), closestPlaceId, flag, distance, location);
        } else {
            registerInteractor.sendExitRegister(getUserId(), closestPlaceId, flag, distance, location);
        }
    }

    private void handleUserOutOfRange(Place place, Double mininDistance) {

        Log.i(TAG, "El usuario esta fuera de rango");
        Double placeRadio = Double.parseDouble(place.getRadio());
        locationManager.stopLocationUpdates();
        registerView.updateList("Fuera de: " + place.getName() + " Centro: " + mininDistance.intValue() + " Radio: " + placeRadio.intValue());
        if (attempNumber < 2) {
            registerView.updateList("Registro insatisfactorio");
            registerView.hideProgressDialog();
            registerView.showAlert("Estas afuera del radio de la sede. Estas a "
                    + (mininDistance.intValue() - placeRadio.intValue()) + "m.");
        }
    }

    private String getUserId() {
        String userId = preferenceManager.getString(Constants.USER_ID, "null");
        return userId;
    }
}
