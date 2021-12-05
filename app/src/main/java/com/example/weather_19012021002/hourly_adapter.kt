package com.example.weather_19012021002

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class hourly_adapter (private var context: Context, var items:ArrayList<hourlyweather>): BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = LayoutInflater.from(context).inflate(R.layout.recyclar_view, parent, false)

        val id: TextView = view.findViewById(R.id.today_time_show)
        val name: TextView =view.findViewById(R.id.toady_temprature)
        val icon:ImageView =view.findViewById(R.id.today_weather_icon)
        val address: TextView = view.findViewById(R.id.today_wind_speed)
        val wind: TextView = view.findViewById(R.id.list_wind)
        val direction : TextView = view.findViewById(R.id.list_direction)


        id.text=items[position].time_day
        name.text=items[position].temp_day

        Glide.with(context).load(items[position].icon_day).into(icon)
//
        address.text=items[position].wind_day
        wind.text=items[position].list_wind
        direction.text=items[position].list_direction

        //contact First Name
        //txt_ltr.text= n[0].toString()


        return view
    }


}