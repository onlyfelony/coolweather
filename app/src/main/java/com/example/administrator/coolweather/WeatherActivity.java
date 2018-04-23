package com.example.administrator.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.coolweather.gson.ForeCast;
import com.example.administrator.coolweather.gson.Weather;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utilty;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView biying;
    private ScrollView weatherlayout;
    private TextView titleCity;
    private TextView titleUpdateTime;

    private TextView degreeText;
    private TextView weatherInfoText;

    private LinearLayout forecastlayout;

    private TextView aqiText;
    private TextView pm25Text;

    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让背景图跟状态栏融合
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//改变系统UI的显示
           // decorView.setFitsSystemWindows(true);
            getWindow().setStatusBarColor(Color.TRANSPARENT);//将状态栏设成透明色

        }


        setContentView(R.layout.activity_weather);
        //初始化控件
        biying = findViewById(R.id.biying_image);
        weatherlayout = findViewById(R.id.mscroll_view);
        titleCity = findViewById(R.id.city_name);
        titleUpdateTime = findViewById(R.id.update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastlayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pref.getString("weather", null);
        String bi_ying = pref.getString("biying",null);
        if (weatherString != null) {
            Weather weather = Utilty.handleWeatherData(weatherString);
            showWeather(weather);
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherlayout.setVisibility(View.INVISIBLE);//将整个ScrollView设置为不可见
            requestWeather(weatherId);//从网络上获取天气


        }

        if(bi_ying!=null){
            Glide.with(this).load(bi_ying).into(biying);
        }else {
            requestBiying();
        }


    }

    //将天气信息显示在界面上
    void showWeather(Weather weather) {
        String cityName = weather.basic.city;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.cond.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        for (ForeCast foreCast : weather.daily_forecast) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastlayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(foreCast.date);
            infoText.setText(foreCast.cond.info);
            maxText.setText(foreCast.tmp.max);
            minText.setText(foreCast.tmp.min);

            forecastlayout.addView(view);
        }

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfo = "舒适度：" + weather.suggestion.comf.info;
        String washCar = "洗车指数：" + weather.suggestion.cw.info;
        String spor = "运动建议：" + weather.suggestion.sport.info;

        comfortText.setText(comfo);
        carWashText.setText(washCar);
        sportText.setText(spor);
        weatherlayout.setVisibility(View.VISIBLE);

    }

    //从网络上获取天气
    void requestWeather(String weatherId) {
        String address = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=9abd14513e2c4f48a64762f727d296f8";

        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responsedata = response.body().string();
                final Weather weather = Utilty.handleWeatherData(responsedata);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responsedata);
                            editor.apply();
                            showWeather(weather);

                        } else {
                            Toast.makeText(WeatherActivity.this, "加载失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });


    }

    //从网络上获取图片
    void requestBiying(){
        String address =  "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String respnseImage = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(respnseImage!=null){

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("biying",respnseImage);
                            editor.apply();
                            Glide.with(WeatherActivity.this).load(respnseImage).into(biying);

                        }
                    }
                });




            }
        });


    }

}
