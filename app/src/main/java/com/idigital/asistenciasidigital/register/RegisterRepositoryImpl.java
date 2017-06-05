package com.idigital.asistenciasidigital.register;

import android.location.Location;

import com.idigital.asistenciasidigital.register.events.RegisterEvent;

import android.util.Log;

import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.lib.EventBus;
import com.idigital.asistenciasidigital.lib.GreenRobotEventBus;
import com.idigital.asistenciasidigital.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by USUARIO on 24/05/2017.
 */

class RegisterRepositoryImpl implements RegisterRepository {

    private static final String TAG = RegisterRepositoryImpl.class.getSimpleName();

    @Override
    public void sendEnterRegister(String userId, String idQuarter, int flag, int distance, Location location, int category) {

        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<RegisterResponse> call = service.postMovement(userId, idQuarter, flag, distance,
                location.getLatitude(), location.getLongitude(), category);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();

                    if (registerResponse.getBlocking()) {
                        postEvent(RegisterEvent.onUserBlocking, registerResponse.getMessage());
                    } else {
                        if (registerResponse.getCode() == 9 || registerResponse.getCode() == 10) {
                            postEvent(RegisterEvent.onSendEnterRegisterSuccess, registerResponse.getMessage());
                        } else if (registerResponse.getCode() == 7 || registerResponse.getCode() == 8) {
                            postEvent(RegisterEvent.onSendRegisterError, registerResponse.getMessage());
                        }
                    }
                } else {
                    postEvent(RegisterEvent.onSendRegisterError, response.message());
                }
                Log.i(TAG, response.raw().toString());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                t.printStackTrace();
                postEvent(RegisterEvent.onSendRegisterFailure);
            }
        });
    }

    @Override
    public void sendExitRegister(String userId, String idQuarter, int flag, int distance, Location location, int category) {

        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<RegisterResponse> call = service.postUpdateMovement(userId, idQuarter, flag, distance,
                location.getLatitude(), location.getLongitude(), category);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();

                    if (registerResponse.getBlocking()) {
                        postEvent(RegisterEvent.onUserBlocking, registerResponse.getMessage());
                    } else {
                        if (registerResponse.getCode() == 11 || registerResponse.getCode() == 12) {
                            postEvent(RegisterEvent.onSendExitRegisterSuccess, registerResponse.getMessage());
                        } else if (registerResponse.getCode() == 13 || registerResponse.getCode() == 8) {
                            postEvent(RegisterEvent.onSendRegisterError, registerResponse.getMessage());
                        }
                    }
                } else {
                    postEvent(RegisterEvent.onSendRegisterError, response.message());
                }
                Log.i(TAG, response.raw().toString());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                t.printStackTrace();
                postEvent(RegisterEvent.onSendRegisterFailure);
            }
        });
    }

    private void postEvent(int type) {
        postEvent(type, null);
    }

    private void postEvent(int type, String message) {
        RegisterEvent registerEvent = new RegisterEvent();
        registerEvent.setEventType(type);
        if (message != null) {
            registerEvent.setMesage(message);
        }

        EventBus eventBus = GreenRobotEventBus.getInstance();
        eventBus.post(registerEvent);
    }
}
