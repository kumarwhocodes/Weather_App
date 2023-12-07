package com.kumar.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("weather?appid=23c6ed0e8904274f86a38241a58dd017&units=metric")
    Call<OpenWeatherMap> getWeatherWithLocation(@Query("lat")double lat, @Query("lon")double lon);

    @GET("weather?appid=23c6ed0e8904274f86a38241a58dd017&units=metric")
    Call<OpenWeatherMap> getWeatherWithCityName(@Query("q")String name);
}