package com.example.weather_19012021002

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import org.json.JSONObject
import java.net.URL
import java.sql.RowId
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    //extra gps
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_ID = 1010
    //upto this

    // for recycler adapter for early time

//


    //
    //list

    val listitems = hourlyweather.hourly_Array as ArrayList<hourlyweather>
    val adapter = hourly_adapter(this, listitems)
    lateinit var list: ListView
    //


    //for city
    lateinit var CITY: String
    lateinit var icon: String

//    val CITY: String = "pune"
//upto this

    val API: String = "469fdefd886b4db9b0745323210411" // Use API key // uncomment this
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide()

        //expand cardview
        val expandBtn = findViewById<Button>(R.id.expandBtn)
        val expandableLayout = findViewById<LinearLayout>(R.id.expandableLayout)
        val cardView = findViewById<CardView>(R.id.cardView1)
        expandBtn.setOnClickListener {
            if (expandableLayout.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                expandableLayout.visibility = View.VISIBLE
                expandBtn.text = "COLLAPSE"
            } else {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                expandableLayout.visibility = View.GONE
                expandBtn.text = "EXPAND"
            }
        }


      findViewById<EditText>(R.id.main_searchview).visibility = View.GONE
        findViewById<Button>(R.id.main_searchbutton).visibility = View.GONE
        findViewById<LinearLayout>(R.id.listview).visibility = View.GONE

        val nav = findViewById<ChipNavigationBar>(R.id.bottom_navigation1)
        nav.setOnItemSelectedListener {
                       when(it){
                           R.id.nav_search ->{
                              findViewById<EditText>(R.id.main_searchview).visibility = View.VISIBLE
                               findViewById<Button>(R.id.main_searchbutton).visibility = View.VISIBLE
                           }
                           R.id.nav_Home -> {
                               findViewById<EditText>(R.id.main_searchview).visibility = View.GONE
                               findViewById<Button>(R.id.main_searchbutton).visibility = View.GONE
                               findViewById<LinearLayout>(R.id.listview).visibility = View.GONE
                           }
                           R.id.nav_information -> {
//                               findViewById<CoordinatorLayout>(R.id.main_layout).visibility = View.GONE
                               findViewById<LinearLayout>(R.id.listview).visibility = View.VISIBLE
//                               findViewById<FloatingActionButton>(R.id.floating_gps).visibility= View.GONE
                           }
                       }
            }






        findViewById<LinearLayout>(R.id.main_container).visibility = View.GONE

        //list view
        list = findViewById(R.id.weather_recyclarview)


        val getpos = findViewById<FloatingActionButton>(R.id.floating_gps)
        //extra gps
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getpos.setOnClickListener {
            Log.d("Debug:", CheckPermission().toString())
            Log.d("Debug:", isLocationEnabled().toString())
            ActivityResultContracts.RequestPermission()

            /* fusedLocationProviderClient.lastLocation.addOnSuccessListener{location: Location? ->
                 textView.text = location?.latitude.toString() + "," + location?.longitude.toString()
             }*/
            getLastLocation()
        }

        //menu city
//        val menu_city = findViewById<View>(R.id.view)
//        menu_city.setOnClickListener {
//            Toast.makeText(applicationContext, "clicked", Toast.LENGTH_LONG).show()
//            Intent(this, search_city::class.java).apply {
//                startActivity(this)
//            }
//        }




        val input = findViewById<EditText>(R.id.main_searchview)
        val search = findViewById<Button>(R.id.main_searchbutton)


        search.setOnClickListener {

            //for recycler hourly adapterx`x`


            //take input in edit text and excecute weather task
            CITY = input.getText().toString()
            Toast.makeText(applicationContext, input.getText().toString(), Toast.LENGTH_LONG).show()
            weatherTask().execute()
        }

    }

fun temp()
{

    weatherTask().execute()
    Toast.makeText(applicationContext,"nothing", Toast.LENGTH_SHORT).show()
}

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()


            /* Showing the ProgressBar, Making the main design GONE */
//            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            //          findViewById<LinearLayout>(R.id.mainContainer).visibility = View.GONE
//            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?

            try {
                response =
                    URL("https://api.weatherapi.com/v1/forecast.json?key=469fdefd886b4db9b0745323210411&q=$CITY&days=1&aqi=no&alerts=no").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)

                val current = jsonObj.getJSONObject("current")
                val location = jsonObj.getJSONObject("location")
                val forcast = jsonObj.getJSONObject("forecast")


                //fetching data from subelement
                val forcastday = forcast.getJSONArray("forecastday")

                // it is main of all sub array
                val first = forcastday.getJSONObject(0)

                val day = first.getJSONObject("day")

                // working on astro
                val astro = first.getJSONObject("astro")


                //recycler view
                val hourlyupdate = first.getJSONArray("hour")
                // val hourlyupdate_start = hourlyupdate.getJSONObject(0)
                // val hourlytime = hourlyupdate_start.getString("temp_c")

                //for testing......
                // findViewById<TextView>(R.id.nothing).text = hourlytime

                for (i in 0..12) {
                    val hourlyupdate_start = hourlyupdate.getJSONObject(i)
                    val time_day = "Time : " + hourlyupdate_start.getString("time")
                    val time_temp = "Tempreture : " + hourlyupdate_start.getString("temp_c")+"°C"
                    val time_wind = "Wind : "+hourlyupdate_start.getString("wind_kph")+" KM/H"


                    val icon_day = hourlyupdate_start.getJSONObject("condition")
                    val icon_list = "https:" + icon_day.getString("icon")
                    //tring
                    val list_wind = hourlyupdate_start.getString("wind_kph")
                    val list_direction = hourlyupdate_start.getString("wind_dir")
                    //end
                    listitems.add(hourlyweather(time_day, time_temp, time_wind, icon_list, list_wind, list_direction))
                    list.adapter = adapter

//                    Toast.makeText(this@MainActivity,time_temp, Toast.LENGTH_SHORT).show()

                }


//                for (i in 0..12) {
//
//                    val jsonObj_0 = jsonarr.getJSONObject(i)
//                    val id_jason = "Person ID : " + jsonObj_0.getString("_id")
//                    val name_json = "Name : " + jsonObj_0.getString("name")
//                    val num_json = "Phone No : " + jsonObj_0.getString("phone")
//                    val add_json = "Address : " + jsonObj_0.getString("address")
//                    listitems.add(Contact(id_jason, name_json, num_json, add_json))
//                    list.adapter = adapter
//                }
                //end here

                val condition = current.getJSONObject("condition")


                val maxtemp = day.getString("maxtemp_c") + "°C"
                val mintemp = day.getString("mintemp_c") + "°C"

                val sunrise = astro.getString("sunrise")
                val sunset = astro.getString("sunset")
                val moonrise = astro.getString("moonrise")
                val moonset = astro.getString("moonset")

                val temp = current.getString("temp_c") + "°C"
                val wind = current.getString("wind_kph") + "km/h"
                val humidity = current.getString("humidity") + "%"
                val rounddial_himidity = current.getString("humidity")
                val wind_direction = current.getString("wind_dir")
                val cloud = current.getString("cloud")
                val feelslike = current.getString("feelslike_c") + "°C"
                val pressure = current.getString("pressure_in") + "in"
                val wind_degree = current.getString("wind_degree") + "°"
                val time_update = current.getString("last_updated")
                val precipitation = current.getString("precip_mm") + "mm"
                val visibility = current.getString("vis_km") + "km"
                val uv = current.getString("uv")
                val avgtemp = day.getString("avgtemp_c") + "°C"
                val rain_chance = day.getString("daily_will_it_rain") + "%"
                val snow_chance = day.getString("daily_will_it_snow") + "%"
                val state = location.getString("region")
                val country = location.getString("country")


                val city = location.getString("name")

                val weather_discription = condition.getString("text")

                val url = condition.getString("icon")


                //uncomment below


                findViewById<TextView>(R.id.main_tempreture).text = temp
                findViewById<TextView>(R.id.main_wind).text = wind
                findViewById<TextView>(R.id.main_humidity).text = humidity
                findViewById<TextView>(R.id.circular_humidity).text = humidity
                findViewById<TextView>(R.id.main_airdirection).text = wind_direction
                findViewById<TextView>(R.id.main_cloud1).text = cloud
                findViewById<TextView>(R.id.main_feelslikes).text = feelslike
                findViewById<TextView>(R.id.main_pressures).text = pressure
                findViewById<TextView>(R.id.main_windspeed).text = wind
                findViewById<TextView>(R.id.main_degree).text = wind_degree
                findViewById<TextView>(R.id.main_city).text = city
                findViewById<TextView>(R.id.main_maximum_temp).text = maxtemp
                findViewById<TextView>(R.id.main_minimumtemp).text = mintemp
                findViewById<TextView>(R.id.main_sunrise).text = sunrise
                findViewById<TextView>(R.id.main_sunset).text = sunset
                findViewById<TextView>(R.id.weather_text).text = weather_discription
                findViewById<TextView>(R.id.main_moonrise).text = moonrise
                findViewById<TextView>(R.id.main_moonset).text = moonset
                findViewById<TextView>(R.id.time_update).text = time_update

                findViewById<TextView>(R.id.main_precipitation).text = precipitation
                findViewById<TextView>(R.id.main_visibility).text = visibility
                findViewById<TextView>(R.id.main_uv).text = uv
                findViewById<TextView>(R.id.main_avgtemp).text = avgtemp
                findViewById<TextView>(R.id.main_rainchance).text = rain_chance
                findViewById<TextView>(R.id.main_snowchance).text = snow_chance
                findViewById<TextView>(R.id.main_state).text = state
                findViewById<TextView>(R.id.main_countryname).text = country


//                val iv = findViewById<ImageView>(R.id.main_icon)
                val image: ImageView = findViewById(R.id.main_icon)
                Glide.with(this@MainActivity).load("https://" + url).into(image)
//                Glide.with(this@MainActivity).load("https://s3.amazonaws.com/appsdeveloperblog/Micky.jpg").into(image)


                progressBar = findViewById(R.id.progress_bar)
                progressBar.max = 100
                progressBar.progress = rounddial_himidity.toInt()

                findViewById<TextView>(R.id.circular_humidity).text = humidity

                Toast.makeText(this@MainActivity,"nothing", Toast.LENGTH_SHORT).show()
                //recylar view

                findViewById<LinearLayout>(R.id.main_container).visibility = View.VISIBLE
                /* Views populated, Hiding the loader, Showing the main design */
//                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
//                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
//                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
//                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE

            }

        }


    }


    private fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT in 19..20) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
            }
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val winParameters = window.attributes
        if (on) {
            winParameters.flags = winParameters.flags or bits
        } else {
            winParameters.flags = winParameters.flags and bits.inv()
        }
        window.attributes = winParameters
    }

    fun getLastLocation() {
        if (CheckPermission()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Sorry no location found", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("Debug:", "Your Location:" + location.longitude)
                        val textView = findViewById<TextView>(R.id.gps_city)
                        textView.text = getCityName(
                            location.latitude,
                            location.longitude
                        ) + "," + getCountryName(location.latitude, location.longitude)
                    }
                }
            } else {
                RequestPermission()
                Toast.makeText(this, "Please Turn on Your device Location", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            RequestPermission()
        }
    }


//    fun NewLocationData(){
//        var locationRequest = LocationRequest
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.interval = 0
//        locationRequest.fastestInterval = 0
//        locationRequest.numUpdates = 1
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        fusedLocationProviderClient!!.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
//    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:", "your last last location: " + lastLocation.longitude.toString())
            val gps = findViewById<TextView>(R.id.gps_city)
            //  gps.text = "You Last Location is : Long: "+ lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n" + getCityName(lastLocation.latitude,lastLocation.longitude)
        }
    }

    private fun CheckPermission(): Boolean {
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if (
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        return false

    }

    fun RequestPermission() {
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled(): Boolean {
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have the Permission")
            }
        }
    }

    private fun getCityName(lat: Double, long: Double): String {
        var cityName: String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat, long, 3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:", "Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }

    private fun getCountryName(lat: Double, long: Double): String {
        var cityName: String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat, long, 3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:", "Your City: " + cityName + " ; your Country " + countryName)
        return countryName
    }

    private fun getcityextra(lat: Double, long: Double): String {
        var cityName: String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat, long, 3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName

        Log.d(
            "Debug:",
            "Your City: " + cityName + " ; your Country " + countryName + "address" + Adress
        )
        return Adress.toString()
    }

    private fun getgeocoder(lat: Double, long: Double): String {
        var cityName: String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat, long, 3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:", "Your City: " + cityName + " ; your Country " + countryName)
        return geoCoder.toString()
    }

//    companion object{
//        var city1 = ""
//    }

}



