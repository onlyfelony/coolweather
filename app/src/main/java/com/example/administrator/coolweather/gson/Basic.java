package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {

    public String city;
    @SerializedName("id")
    public String weatherid;
    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;

    }


}
