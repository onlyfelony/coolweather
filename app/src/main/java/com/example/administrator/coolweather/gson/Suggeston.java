package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggeston {

    public Comfort comf;
    public CarWash cw;
    public Sport sport;

    public class Comfort {
        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        @SerializedName("txt")
        public String info;
    }

    public class Sport {

        @SerializedName("txt")
        public String info;
    }

}
