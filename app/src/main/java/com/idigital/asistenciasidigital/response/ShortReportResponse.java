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
    private Integer error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("blocking")
    @Expose
    private Boolean blocking;

    public List<ShortReport> getData() {
        return data;
    }

    public void setData(List<ShortReport> data) {
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
