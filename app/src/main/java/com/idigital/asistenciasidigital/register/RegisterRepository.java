package com.idigital.asistenciasidigital.register;

import android.location.Location;

/**
 * Created by USUARIO on 24/05/2017.
 */

public interface RegisterRepository {

    void sendEnterRegister(String userId, String idQuarter, int flag, int distance, Location location);

    void sendExitRegister(String userId, String idQuarter, int flag, int distance, Location location);

}