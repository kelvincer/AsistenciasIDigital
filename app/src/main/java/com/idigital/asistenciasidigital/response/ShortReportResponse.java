package com.idigital.asistenciasidigital.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.idigital.asistenciasidigital.model.Report;
import com.idigital.asistenciasidigital.model.ShortReport;

import java.util.List;

/**
 * Created by USUARIO on 06/04/2017.
 */

public class ShortReportResponse {

    @SerializedName("data")
    @Expose
    private List<ShortReport> data = null;
    @SerializedName("error")
    @Expose
    private boolean error;

    public List<ShortReport> getData() {
        return data;
    }

    public void setData(List<ShortReport> data) {
        this.data = data;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
