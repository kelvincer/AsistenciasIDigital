package com.idigital.asistenciasidigital.register;

import android.location.Location;

/**
 * Created by USUARIO on 24/05/2017.
 */

public interface RegisterRepository {

    void sendRegister(String userId, String idQuarter, int flag, int distance, Location location, int category, String token);
}