package com.idigital.asistenciasidigital.register.events;

/**
 * Created by USUARIO on 24/05/2017.
 */

public class RegisterEvent {

    public final static int onSendEnterRegisterSuccess = 0;
    public final static int onSendExitRegisterSuccess = 1;
    public final static int onSendRegisterError = 2;
    public final static int onSendRegisterFailure = 3;
    public final static int onUserBlocking = 4;

    private int eventType;
    private String errorMesage;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return errorMesage;
    }

    public void setMesage(String errorMesage) {
        this.errorMesage = errorMesage;
    }
}
