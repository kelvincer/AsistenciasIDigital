package com.idigital.asistenciasidigital.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.idigital.asistenciasidigital.model.Place;
import com.idigital.asistenciasidigital.model.Report;

import java.util.List;

/**
 * Created by USUARIO on 05/04/2017.
 */

public class PlaceResponse {

    @SerializedName("data")
    @Expose
    private List<Place> data = null;
    @SerializedName("error")
    @Expose
    private boolean error;

    public List<Place> getData() {
        return data;
    }

    public void setData(List<Place> data) {
        this.data = data;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
