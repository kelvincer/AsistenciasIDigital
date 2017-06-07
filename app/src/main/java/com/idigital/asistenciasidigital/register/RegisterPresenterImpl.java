package com.idigital.asistenciasidigital.register;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.idigital.asistenciasidigital.PreferenceManager;
import com.idigital.asistenciasidigital.R;
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
    private int category;

    public RegisterPresenterImpl(Context context, RegisterView registerView) {

        this.context = context;
        this.registerView = registerView;
        this.eventBus = GreenRobotEventBus.getInstance();
        this.registerInteractor = new RegisterInteractorImpl();
        locationManager = new LocationManager(context, this);
        preferenceManager = new PreferenceManager(context);
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
    public void sendRegister(String movement, int category) {
        if (registerView != null) {
            registerView.showProgressDialog();
        }
        this.movement = movement;
        this.category = category;
        locationManager.connect();
    }

    @Override
    @Subscribe
    public void onEventMainThread(RegisterEvent event) {

        registerView.hideProgressDialog();

        switch (event.getEventType()) {
            case RegisterEvent.onSendEnterRegisterSuccess:
                onRegisterSuccess(event.getMessage(), event.getTime());
                break;
            case RegisterEvent.onSendExitRegisterSuccess:
                onRegisterSuccess(event.getMessage(), event.getTime());
                break;
            case RegisterEvent.onSendRegisterError:
                onRegisterError(event.getMessage());
                break;
            case RegisterEvent.onSendRegisterFailure:
                onRegisterFailure();
                break;
            case RegisterEvent.onUserBlocking:
                onBlocking(event.getMessage());
                break;
            default:
                throw new IllegalArgumentException("Invalid event type");
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
        /*if (place == null) {
            Toast.makeText(context, "Error on place", Toast.LENGTH_SHORT).show();
            return;
        }*/

        Double placeRadio = Double.parseDouble(place.getRadio());
        if (mininDistance.intValue() <= placeRadio.intValue()) {
            handleUserInRange(place, mininDistance, location);
        } else {
            handleUserOutOfRange(place, mininDistance, location);
        }
    }

    @Override
    public void createGoogleApiClient() {
        if (locationManager != null)
            locationManager.createGoogleApiClient();
    }

    private void onBlocking(String message) {
        Log.i(TAG, "user blocking");
        registerView.updateList(message);
    }

    private void onRegisterFailure() {

        registerView.updateList(context.getResources().getString(R.string.register_failed));
    }

    private void onRegisterError(String message) {

        registerView.updateList(message);
        registerView.showAlert(message);
    }

    private void onRegisterSuccess(String message, String time) {

        registerView.updateList(message);
        registerView.showAlert(message);
        registerView.updateTextView(time);
        registerView.enableButton();
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

    private void handleUserInRange(Place place, Double mininDistance, Location location) {
        Log.i(TAG, "El usuario esta dentro de rango");
        Double placeRadio = Double.parseDouble(place.getRadio());
        locationManager.stopLocationUpdates();
        registerView.updateList("Dentro de: " + place.getName() + " Centro: " + mininDistance.intValue() + " Radio: " + placeRadio.intValue());
        setUpForSendRegister(location, Constants.NORMAL, mininDistance.intValue());
    }

    private void setUpForSendRegister(Location location, int flag, int distance) {

        registerView.setProgressMessage("Enviando registro");
        attempNumber = 0;
        if (movement.equalsIgnoreCase(Constants.INGRESO)) {
            registerInteractor.sendEnterRegister(getUserId(), closestPlaceId, flag, distance, location, category);
        } else {
            registerInteractor.sendExitRegister(getUserId(), closestPlaceId, flag, distance, location, category);
        }
    }

    private void handleUserOutOfRange(Place place, Double mininDistance, Location location) {

        Log.i(TAG, "El usuario esta fuera de rango");
        Double placeRadio = Double.parseDouble(place.getRadio());
        locationManager.stopLocationUpdates();
        registerView.updateList("Fuera de: " + place.getName() + " Centro: " + mininDistance.intValue() + " Radio: " + placeRadio.intValue());
        if (attempNumber < 2) {
            registerView.updateList(context.getResources().getString(R.string.register_unsuccessful));
            registerView.hideProgressDialog();
            registerView.showAlert(String.format(context.getResources().getString(R.string.out_of_range), mininDistance.intValue() - placeRadio.intValue()));
            attempNumber++;
        } else if (attempNumber == 2) {
            setUpForSendRegister(location, Constants.OBSERVATION, mininDistance.intValue());
        } else {
            throw new IllegalArgumentException("Illegal attempNumber value");
        }
    }

    private String getUserId() {
        String userId = preferenceManager.getString(Constants.USER_ID, "null");
        return userId;
    }
}
