package com.idigital.asistenciasidigital.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by USUARIO on 06/04/2017.
 */

public class Report {

    @SerializedName("id_attendance")
    @Expose
    private String idAttendance;
    @SerializedName("id_user")
    @Expose
    private String idUser;
    @SerializedName("id_headquarter")
    @Expose
    private String idHeadquarter;
    @SerializedName("date_add")
    @Expose
    private String dateAdd;
    @SerializedName("movement")
    @Expose
    private String movement;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("sede")
    @Expose
    private String sede;

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

    public String getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }
}
