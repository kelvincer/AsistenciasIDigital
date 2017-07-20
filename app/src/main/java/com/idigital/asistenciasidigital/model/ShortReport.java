package com.idigital.asistenciasidigital.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by USUARIO on 08/05/2017.
 */

public class ShortReport implements Serializable {

    @SerializedName("id_attendance")
    @Expose
    private String idAttendance;
    @SerializedName("id_user")
    @Expose
    private String idUser;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("date_show")
    @Expose
    private String dateShow;
    @SerializedName("id_headquarter")
    @Expose
    private String idHeadquarter;
    @SerializedName("sede")
    @Expose
    private String sede;
    @SerializedName("fecha")
    @Expose
    private String fecha;
    @SerializedName("hora")
    @Expose
    private String hora;
    @SerializedName("movement")
    @Expose
    private String movement;
    @SerializedName("id_attendance_category")
    @Expose
    private String idAttendanceCategory;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("state")
    @Expose
    private String state;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDateShow() {
        return dateShow;
    }

    public void setDateShow(String dateShow) {
        this.dateShow = dateShow;
    }

    public String getIdHeadquarter() {
        return idHeadquarter;
    }

    public void setIdHeadquarter(String idHeadquarter) {
        this.idHeadquarter = idHeadquarter;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }

    public String getIdAttendanceCategory() {
        return idAttendanceCategory;
    }

    public void setIdAttendanceCategory(String idAttendanceCategory) {
        this.idAttendanceCategory = idAttendanceCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
