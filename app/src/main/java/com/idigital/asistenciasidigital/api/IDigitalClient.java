package com.idigital.asistenciasidigital.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class IDigitalClient {

    private static Retrofit retrofit;
    //private final static String BASE_URL = "http://54.67.124.238/asistencias/public/";
    private final static String BASE_URL = "http://54.67.124.238/rest.devel/public/";

    public static IDigitalService getIDigitalService() {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(IDigitalService.class);
    }
}
