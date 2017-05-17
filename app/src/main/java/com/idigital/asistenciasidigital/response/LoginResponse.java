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
    private Login data;
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("blocking")
    @Expose
    private Boolean blocking;

    public Login getData() {
        return data;
    }

    public void setData(Login data) {
        this.data = data;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getBlocking() {
        return blocking;
    }

    public void setBlocking(Boolean blocking) {
        this.blocking = blocking;
    }
}
