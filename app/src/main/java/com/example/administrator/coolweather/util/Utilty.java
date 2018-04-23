package com.example.administrator.coolweather.util;

import android.text.TextUtils;

import com.example.administrator.coolweather.db.City;
import com.example.administrator.coolweather.db.Country;
import com.example.administrator.coolweather.db.Province;
import com.example.administrator.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析从服务器端得到的数据并且存入数据库
 */
public class Utilty {

    //解析Province级数据
    public static boolean handleProvinceData(String response) {

        if (!TextUtils.isEmpty(response)) {

            try {
                JSONArray provincesArray = new JSONArray(response);
                for (int i = 0; i < provincesArray.length(); i++) {
                    JSONObject provin = provincesArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provin.getInt("id"));
                    province.setProvinceName(provin.getString("name"));

                    province.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    //解析City级数据
    public static boolean handleCityData(String response, int provonceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray cityArray = new JSONArray(response);
                for (int i = 0; i < cityArray.length(); i++) {
                    JSONObject cit = cityArray.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cit.getInt("id"));
                    city.setCityName(cit.getString("name"));
                    city.setProvinceId(provonceId);
                    city.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    //解析Country级数据
    public static boolean handleCountryData(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray countryArray = new JSONArray(response);
                for (int i = 0; i < countryArray.length(); i++) {
                    JSONObject countr = countryArray.getJSONObject(i);
                    Country country = new Country();

                    country.setCountryName(countr.getString("name"));
                    country.setWeatherId(countr.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();

                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static Weather handleWeatherData(String response){
        if(!TextUtils.isEmpty(response)){

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
                JSONObject object = jsonArray.getJSONObject(0);
                Gson gson = new Gson();
                Weather weather = gson.fromJson(object.toString(),Weather.class);

                return weather;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        
        return null;
    }


}
