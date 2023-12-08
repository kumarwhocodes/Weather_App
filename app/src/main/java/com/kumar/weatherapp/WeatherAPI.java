package com.kumar.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("weather?appid={Your API key}&units=metric")
    Call<OpenWeatherMap> getWeatherWithLocation(@Query("lat")double lat, @Query("lon")double lon);

    @GET("weather?appid={Your API key}&units=metric")
    Call<OpenWeatherMap> getWeatherWithCityName(@Query("q")String name);
}