package com.idigital.asistenciasidigital.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by USUARIO on 08/05/2017.
 */

public class ShortReport implements Serializable {

    @SerializedName("id_user")
    @Expose
    private String idUser;
    @SerializedName("fecha")
    @Expose
    private String fecha;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("total_horas")
    @Expose
    private String totalHoras;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTotalHoras() {
        return totalHoras;
    }

    public void setTotalHoras(String totalHoras) {
        this.totalHoras = totalHoras;
    }
}
