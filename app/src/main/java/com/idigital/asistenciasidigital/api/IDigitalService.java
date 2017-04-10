package com.idigital.asistenciasidigital.api;

import com.idigital.asistenciasidigital.response.LoginResponse;
import com.idigital.asistenciasidigital.response.PlaceResponse;
import com.idigital.asistenciasidigital.response.RegisterResponse;
import com.idigital.asistenciasidigital.response.ReportResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IDigitalService {

    @GET("place")
    Call<PlaceResponse> getPlaces();

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> postLogin(@Field("email") String email, @Field("passwd") String password);

    @FormUrlEncoded
    @POST("attendance_add")
    Call<RegisterResponse> postRegistry(@Field("id_user") String idUser, @Field("id_headquarter") String idQuarter,
                                        @Field("date_add") String date, @Field("movement") String movement,
                                        @Field("latitude") double latitude, @Field("longitude") double longitude);

    @GET("attendance")
    Call<ReportResponse> getReport();

    @FormUrlEncoded
    @POST("attendance_place")
    Call<ReportResponse> getUserReport(@Field("id_user") String idUser, @Field("id_headquarter") String idQuarter);

    @FormUrlEncoded
    @POST("attendance_allplaces")
    Call<ReportResponse> getAllUserReport(@Field("id_user") String idUser);
}
