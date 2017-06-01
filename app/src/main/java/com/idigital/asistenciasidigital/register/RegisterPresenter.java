package com.idigital.asistenciasidigital.register;

import com.idigital.asistenciasidigital.register.events.RegisterEvent;

/**
 * Created by USUARIO on 24/05/2017.
 */

public interface RegisterPresenter {

    void onCreate();
    void onDestroy();

    void sendRegister(String movement);
    void onEventMainThread(RegisterEvent event);

    void createGoogleApiClient();
}
