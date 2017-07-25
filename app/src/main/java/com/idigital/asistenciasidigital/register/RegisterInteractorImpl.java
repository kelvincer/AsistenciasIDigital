package com.idigital.asistenciasidigital.register;

import android.location.Location;

/**
 * Created by USUARIO on 24/05/2017.
 */

class RegisterInteractorImpl implements RegisterInteractor {

    private RegisterRepository registerRepository;

    public RegisterInteractorImpl() {
        this.registerRepository = new RegisterRepositoryImpl();
    }

    @Override
    public void sendRegisteredMovement(String userId, String idQuarter, int flag, int distance, Location location, int category, String token) {
        registerRepository.sendRegister(userId, idQuarter, flag, distance, location,category, token);
    }
}
