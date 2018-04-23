package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class ForeCast {
   public String date;
   public Conde cond;
   public Tmp tmp;

   public class Conde{
       @SerializedName("txt_d")
      public String info;

   }

   public class Tmp{
       public String max;
       public String min;

   }



}
