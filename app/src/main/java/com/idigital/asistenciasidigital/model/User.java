package com.idigital.asistenciasidigital.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Kelvin on 19/07/2017.
 */

@DatabaseTable
public class User {

    @DatabaseField(id = true)
    String email;
    @DatabaseField
    String password;
    @DatabaseField
    int activeButton;
    @DatabaseField
    String timeOne;
    @DatabaseField
    String timeTwo;
    @DatabaseField
    String timeThree;
    @DatabaseField
    String timeFour;
    @DatabaseField
    Boolean loggedIn;
    @DatabaseField
    String userId;
    @DatabaseField
    String token;
    @DatabaseField
    String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getActiveButton() {
        return activeButton;
    }

    public void setActiveButton(int activeButton) {
        this.activeButton = activeButton;
    }

    public String getTimeOne() {
        return timeOne;
    }

    public void setTimeOne(String timeOne) {
        this.timeOne = timeOne;
    }

    public String getTimeTwo() {
        return timeTwo;
    }

    public void setTimeTwo(String timeTwo) {
        this.timeTwo = timeTwo;
    }

    public String getTimeThree() {
        return timeThree;
    }

    public void setTimeThree(String timeThree) {
        this.timeThree = timeThree;
    }

    public String getTimeFour() {
        return timeFour;
    }

    public void setTimeFour(String timeFour) {
        this.timeFour = timeFour;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
