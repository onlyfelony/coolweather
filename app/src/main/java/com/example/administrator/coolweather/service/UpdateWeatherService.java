package com.example.administrator.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.administrator.coolweather.gson.Weather;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utilty;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateWeatherService extends Service {
    public UpdateWeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBiying();

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long eightTime = SystemClock.elapsedRealtime() + 1000 * 60 * 60 * 8;//八个小时
        Intent starService = new Intent(this, UpdateWeatherService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, starService, 0);
        manager.cancel(pendingIntent);//先把之前的pendingIntent取消再设定
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, eightTime, pendingIntent);


        return super.onStartCommand(intent, flags, startId);
    }

    void updateWeather() {

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherdata = pref.getString("weather", null);
        if (weatherdata != null) {
            Weather mwea = Utilty.handleWeatherData(weatherdata);
            String weaId = mwea.basic.weatherid;
            String address = "http://guolin.tech/api/weather?cityid=" + weaId + "&key=9abd14513e2c4f48a64762f727d296f8";
            HttpUtil.sendHttpRequest(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String weatherData = response.body().string();
                    Weather puWea = Utilty.handleWeatherData(weatherData);
                    if (puWea != null && puWea.status.equals("ok")) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("weather", weatherData);
                        editor.apply();

                    }

                }
            });


        }


    }

    void updateBiying() {

        String address = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String image = response.body().string();
                if (image != null) {

                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateWeatherService.this).edit();
                    editor.putString("biying", image);
                    editor.apply();
                }

            }
        });


    }

}
