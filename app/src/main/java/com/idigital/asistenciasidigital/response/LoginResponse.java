package com.idigital.asistenciasidigital.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.idigital.asistenciasidigital.model.Login;
import com.idigital.asistenciasidigital.model.Report;

import java.util.List;

/**
 * Created by USUARIO on 05/04/2017.
 */

public class LoginResponse {

    @SerializedName("data")
    @Expose
    private Login data = null;
    @SerializedName("error")
    @Expose
    private boolean error;

    public Login getData() {
        return data;
    }

    public void setData(Login data) {
        this.data = data;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
