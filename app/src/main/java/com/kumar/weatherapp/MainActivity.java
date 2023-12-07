package com.kumar.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationListenerCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText enterCity;
    private ImageView condition_iv;
    private FloatingActionButton search;
    private TextView cityName, condition_desc, currentTemp, minTemp, maxTemp, wind, pressure, humidity;
    private TextClock clock;

    LocationManager locationManager;
    LocationListener locationListener;
    double lat, lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterCity = findViewById(R.id.enter_city_box);
        search = findViewById(R.id.search_button);
        condition_iv = findViewById(R.id.condition_iv);
        cityName = findViewById(R.id.city_name);
        condition_desc = findViewById(R.id.conditionDesc);
        currentTemp = findViewById(R.id.current_temperature);
        minTemp = findViewById(R.id.min_temperature);
        maxTemp = findViewById(R.id.max_temperature);
        wind = findViewById(R.id.wind);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        clock = findViewById(R.id.clock);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String city=enterCity.getText().toString();

                getWeatherData(city);

                enterCity.setText("");


            }
        });


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Prompt the user to enable GPS
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat = location.getLatitude();
                lon = location.getLongitude();

                Log.e("lat : ", String.valueOf(lat));
                Log.e("lon : ", String.valueOf(lon));

                getWeatherData(lat, lon);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                    , 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 50, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && permissions.length > 0 && ContextCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 50, locationListener);
        }
    }


    //SEARCH BY DEVICE LOCATION FUNCTION
    public void getWeatherData(double lat, double lon) {
        WeatherAPI weatherAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call = weatherAPI.getWeatherWithLocation(lat, lon);

        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {

                cityName.setText(response.body().getName() + " , " + response.body().getSys().getCountry());
                currentTemp.setText(response.body().getMain().getTemp() + " °C");
                condition_desc.setText(response.body().getWeather().get(0).getDescription());
                humidity.setText("" + response.body().getMain().getHumidity() + "%");
                maxTemp.setText("" + response.body().getMain().getTempMax() + " °C");
                minTemp.setText("" + response.body().getMain().getTempMin() + " °C");
                pressure.setText("" + response.body().getMain().getPressure());
                wind.setText("" + response.body().getWind().getSpeed());

                String iconCode = response.body().getWeather().get(0).getIcon();
                Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(condition_iv);

            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {

            }
        });
    }


    //SEARCH BY CITY NAME FUNCTION
    public void getWeatherData(String name) {
        WeatherAPI weatherAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call = weatherAPI.getWeatherWithCityName(name);

        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {

                if (response.isSuccessful()) {
                    cityName.setText(response.body().getName() + " , " + response.body().getSys().getCountry());
                    currentTemp.setText(response.body().getMain().getTemp() + " °C");
                    condition_desc.setText(response.body().getWeather().get(0).getDescription());
                    humidity.setText("" + response.body().getMain().getHumidity() + "%");
                    maxTemp.setText("" + response.body().getMain().getTempMax() + " °C");
                    minTemp.setText("" + response.body().getMain().getTempMin() + " °C");
                    pressure.setText("" + response.body().getMain().getPressure()+"hPa");
                    wind.setText("" + response.body().getWind().getSpeed()+"mph");

                    String iconCode = response.body().getWeather().get(0).getIcon();
                    Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(condition_iv);
                } else {
                    Toast.makeText(MainActivity.this, "City not found,please try again."
                            , Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {

            }
        });
    }


}





