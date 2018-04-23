package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperature;
    public Cond cond;

    public class Cond {
        @SerializedName("txt")
        public String info;

    }

}
