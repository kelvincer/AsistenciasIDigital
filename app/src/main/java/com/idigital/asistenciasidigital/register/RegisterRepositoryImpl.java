package com.idigital.asistenciasidigital.register;

import android.location.Location;

import com.idigital.asistenciasidigital.model.Time;
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
    public void sendRegister(String userId, String idQuarter, int flag, int distance, Location location, int category, String token) {

        IDigitalService service = IDigitalClient.getIDigitalService();
        Call<RegisterResponse> call = service.postMovement(userId, idQuarter, flag, distance,
                location.getLatitude(), location.getLongitude(), category, token);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                Log.i(TAG, response.raw().toString());
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();

                    if (registerResponse.getBlocking()) {
                        postEvent(RegisterEvent.onUserBlocking, registerResponse.getMessage(), null);
                    } else {
                        if (registerResponse.getCode() == 9 || registerResponse.getCode() == 10
                                || registerResponse.getCode() == 16 || registerResponse.getCode() == 17
                                || registerResponse.getCode() == 20 || registerResponse.getCode() == 21
                                || registerResponse.getCode() == 11 || registerResponse.getCode() == 12
                                || registerResponse.getCode() == 18 || registerResponse.getCode() == 19
                                || registerResponse.getCode() == 22 || registerResponse.getCode() == 23) {
                            postEvent(RegisterEvent.onSendEnterRegisterSuccess, registerResponse.getMessage(), registerResponse.getData());
                        } else if (registerResponse.getCode() == 7 || registerResponse.getCode() == 8
                                || registerResponse.getCode() == 13) {
                            postEvent(RegisterEvent.onSendRegisterError, registerResponse.getMessage(), null);
                        } else {
                            throw new RuntimeException("Invalid register response code");
                        }
                    }
                } else {
                    postEvent(RegisterEvent.onSendRegisterError, response.message(), null);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                t.printStackTrace();
                postEvent(RegisterEvent.onSendRegisterFailure);
            }
        });
    }

    private void postEvent(int type) {
        postEvent(type, null, null);
    }

    private void postEvent(int type, String message, Time time) {
        RegisterEvent registerEvent = new RegisterEvent();
        registerEvent.setEventType(type);
        if (message != null)
            registerEvent.setMesage(message);
        if (time != null)
            registerEvent.setTime(time.getScalar());

        EventBus eventBus = GreenRobotEventBus.getInstance();
        eventBus.post(registerEvent);
    }
}
