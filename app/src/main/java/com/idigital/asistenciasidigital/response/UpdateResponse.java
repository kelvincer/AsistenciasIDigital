package com.idigital.asistenciasidigital.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.idigital.asistenciasidigital.model.Update;

import java.util.List;

/**
 * Created by USUARIO on 10/05/2017.
 */

public class UpdateResponse {

    @SerializedName("data")
    @Expose
    private Update data;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;

    public Update getData() {
        return data;
    }

    public void setData(Update data) {
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
