package com.idigital.asistenciasidigital.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by USUARIO on 09/05/2017.
 */

public class DetailReport {

    @SerializedName("id_attendance")
    @Expose
    private String idAttendance;
    @SerializedName("id_user")
    @Expose
    private String idUser;
    @SerializedName("id_headquarter")
    @Expose
    private String idHeadquarter;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("fecha")
    @Expose
    private String fecha;
    @SerializedName("ingreso")
    @Expose
    private String ingreso;
    @SerializedName("salida")
    @Expose
    private String salida;
    @SerializedName("horabruta")
    @Expose
    private String horabruta;

    public String getIdAttendance() {
        return idAttendance;
    }

    public void setIdAttendance(String idAttendance) {
        this.idAttendance = idAttendance;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdHeadquarter() {
        return idHeadquarter;
    }

    public void setIdHeadquarter(String idHeadquarter) {
        this.idHeadquarter = idHeadquarter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIngreso() {
        return ingreso;
    }

    public void setIngreso(String ingreso) {
        this.ingreso = ingreso;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public String getHorabruta() {
        return horabruta;
    }

    public void setHorabruta(String horabruta) {
        this.horabruta = horabruta;
    }
}
