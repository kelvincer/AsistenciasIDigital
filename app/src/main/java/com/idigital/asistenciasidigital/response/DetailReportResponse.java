package com.idigital.asistenciasidigital.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.idigital.asistenciasidigital.model.DetailReport;

import java.util.List;

/**
 * Created by USUARIO on 09/05/2017.
 */

public class DetailReportResponse {

    @SerializedName("data")
    @Expose
    private List<DetailReport> data = null;
    @SerializedName("error")
    @Expose
    private Integer error;

    public List<DetailReport> getData() {
        return data;
    }

    public void setData(List<DetailReport> data) {
        this.data = data;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }
}
