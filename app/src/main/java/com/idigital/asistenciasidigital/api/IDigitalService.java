package com.idigital.asistenciasidigital.api;

import com.idigital.asistenciasidigital.response.ActiveButtonResponse;
import com.idigital.asistenciasidigital.response.DetailReportResponse;
import com.idigital.asistenciasidigital.response.LoginResponse;
import com.idigital.asistenciasidigital.response.PlaceResponse;
import com.idigital.asistenciasidigital.response.RegisterResponse;
import com.idigital.asistenciasidigital.response.ShortReportResponse;
import com.idigital.asistenciasidigital.response.VersionResponse;

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
    @POST("attendance-add")
    Call<RegisterResponse> postMovement(@Field("id_user") String idUser, @Field("id_headquarter") String idQuarter,
                                        @Field("flag_obs") int flag, @Field("distance") int distance,
                                        @Field("latitude") double latitude, @Field("longitude") double longitude,
                                        @Field("id_attendance_category") int category, @Field("token") String token);

    @FormUrlEncoded
    @POST("attendance-allheader-user")
    Call<ShortReportResponse> postAttendanceUser(@Field("id_user") String idUser);

    @FormUrlEncoded
    @POST("attendance-detailheader-user")
    Call<DetailReportResponse> postAttendanceDetail(@Field("id_user") String idUser,
                                                    @Field("date_show_in") String date);

    @FormUrlEncoded
    @POST("android-version")
    Call<VersionResponse> postVersion(@Field("android_version") int version);

    @FormUrlEncoded
    @POST("attendance-button")
    Call<ActiveButtonResponse> getActiveButton(@Field("id_user") String idUser, @Field("token") String token);
}
