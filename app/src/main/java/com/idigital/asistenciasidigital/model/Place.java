package com.idigital.asistenciasidigital.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by USUARIO on 07/04/2017.
 */

@DatabaseTable
public class Place {

    @DatabaseField(id = true)
    @SerializedName("id_headquarter")
    @Expose
    private String idHeadquarter;
    @DatabaseField
    @SerializedName("name")
    @Expose
    private String name;
    @DatabaseField
    @SerializedName("address")
    @Expose
    private String address;
    @DatabaseField
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @DatabaseField
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @DatabaseField
    @SerializedName("radio")
    @Expose
    private String radio;
    @DatabaseField
    @SerializedName("active")
    @Expose
    private String active;
    @DatabaseField
    @SerializedName("deleted")
    @Expose
    private String deleted;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getRadio() {
        return radio;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

}
