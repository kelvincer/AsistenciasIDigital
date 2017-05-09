package com.idigital.asistenciasidigital.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by USUARIO on 08/05/2017.
 */

public class InactiveResponse {

    @SerializedName("result")
    @Expose
    boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}

