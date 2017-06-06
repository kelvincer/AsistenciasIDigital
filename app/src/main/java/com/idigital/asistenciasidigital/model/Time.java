package com.idigital.asistenciasidigital.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by USUARIO on 06/06/2017.
 */

public class Time {

    @SerializedName("scalar")
    @Expose
    String scalar;

    public String getScalar() {
        return scalar;
    }

    public void setScalar(String scalar) {
        this.scalar = scalar;
    }
}
