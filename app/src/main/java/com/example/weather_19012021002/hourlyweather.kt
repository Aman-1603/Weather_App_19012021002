package com.example.weather_19012021002

class hourlyweather (var time_day:String, var temp_day:String, var wind_day:String,var icon_day:String,var list_wind:String,var list_direction:String) {

    companion object{

        var hourly_Array: List<hourlyweather> = ArrayList()
    }

}