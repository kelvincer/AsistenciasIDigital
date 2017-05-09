package com.idigital.asistenciasidigital.api;

import com.idigital.asistenciasidigital.response.DetailReportResponse;
import com.idigital.asistenciasidigital.response.InactiveResponse;
import com.idigital.asistenciasidigital.response.LoginResponse;
import com.idigital.asistenciasidigital.response.PlaceResponse;
import com.idigital.asistenciasidigital.response.RegisterResponse;
import com.idigital.asistenciasidigital.response.ShortReportResponse;

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
    @POST("inactive_user")
    Call<InactiveResponse> postInactiveUser(@Field("id_user") String idUser);

    @FormUrlEncoded
    @POST("attendance_add")
    Call<RegisterResponse> postRegistry(@Field("id_user") String idUser, @Field("id_headquarter") String idQuarter,
                                        @Field("movement") String movement, @Field("latitude") double latitude,
                                        @Field("longitude") double longitude);

    @GET("attendance")
    Call<ShortReportResponse> getReport();

    @FormUrlEncoded
    @POST("attendance_place")
    Call<ShortReportResponse> postUserReport(@Field("id_user") String idUser, @Field("id_headquarter") String idQuarter);

    @FormUrlEncoded
    @POST("attendance_allplaces")
    Call<ShortReportResponse> postAllUserReport(@Field("id_user") String idUser);

    @FormUrlEncoded
    @POST("attendance_allheader_user")
    Call<ShortReportResponse> postAttendanceUser(@Field("id_user") String idUser);

    @FormUrlEncoded
    @POST("attendance_detailheader_user")
    Call<DetailReportResponse> postAttendanceDetail(@Field("id_user") String idUser,
                                                    @Field("date_show_in") String date);
}
