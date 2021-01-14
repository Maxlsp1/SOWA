package com.example.simpleOpenWeatherApp

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.androdocs.httprequest.HttpRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.city_weather_form.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class city_weather_frag(lang: String, cityName: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val language = lang
    var multiDayDataWeather = arrayListOf<appData.multi_dayData>()
    val cityName = cityName
    var day: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val cityWeatherForm = inflater.inflate(R.layout.city_weather_form, container, false)
        cityWeatherForm.previousDayButton.visibility = View.INVISIBLE
        weatherTask(language, cityName, this, cityWeatherForm).execute()

        if(language == "fr"){
            cityWeatherForm.previousDayButton.text = "Jours -1"
            cityWeatherForm.nextDayButton.text = "Jours +1"
        } else{
            cityWeatherForm.previousDayButton.text = "Prev day"
            cityWeatherForm.nextDayButton.text = "Next day"
        }

        cityWeatherForm.nameCityTextView.text = cityName

        cityWeatherForm.previousDayButton.setOnClickListener(){
            day = day - 1
            displayMultiDayWeatherData(cityWeatherForm, day, this)
        }

        cityWeatherForm.nextDayButton.setOnClickListener(){
            cityWeatherForm.nextDayButton.visibility = View.VISIBLE
            day = day + 1
            displayMultiDayWeatherData(cityWeatherForm, day, this)
        }

        cityWeatherForm.previousDayButton.setBackgroundColor(Color.parseColor("#8800ff"))
        cityWeatherForm.nextDayButton.setBackgroundColor(Color.parseColor("#8800ff"))

        return cityWeatherForm
    }

    /**
     *
     * This method allows you to display the different information that the query has allowed to retrieve,
     * by reading from the table where the data is stored,
     * there are three possibilities : on day 0 the method calls the class that executes the request,
     * on day 1 it displays the weather on d+1,
     * day 2 which displays the weather on d+2.
     *
     * @param cityWeatherForm object of the city_weather_form instance that allows to display the different information.
     *
     * @param day day is incremented by one, or decremented by 1 if the user clicks on the nextDayButton or previousDayButton,
     * it will be used to initialize the "index" variable,
     * that will allow to read the information at the desired index in the array.
     *
     * @param cityFrag is used to check if the language that the user has saved is in French,
     * this allows you to change the units used such as km/h, °c, and mm.
     * */

    fun displayMultiDayWeatherData(cityWeatherForm: View, day: Int, cityFrag: city_weather_frag){
        var tempUnit: String? = null
        var windUnit: String? = null
        var preicipitationUnit: String? = null
        var index: Int = 0
        index = day

        if(cityFrag.language == "fr"){
            tempUnit = " °c"
            windUnit= " Km/h"
            preicipitationUnit = " mm"
        }else{
            tempUnit = " °f"
            windUnit= " mph"
            preicipitationUnit = " in"
        }
        if((day>=0 && day<=2)&&(cityFrag.multiDayDataWeather.size>0)){
            when(day){
                0 ->{
                    cityWeatherForm.previousDayButton.visibility = View.INVISIBLE
                    cityWeatherForm.pressureContainer.visibility = View.VISIBLE
                    weatherTask(language, cityName, this, cityWeatherForm).execute()
                }
                1,2 ->{
                    cityWeatherForm.pressureContainer.visibility = View.INVISIBLE
                    cityWeatherForm.previousDayButton.visibility = View.VISIBLE
                    cityWeatherForm.nextDayButton.visibility = View.VISIBLE
                    if(day == 2){
                        cityWeatherForm.nextDayButton.visibility = View.INVISIBLE
                    }

                    cityWeatherForm.temperatureTextView.text = multiDayDataWeather[index].avgTemp.toString() + tempUnit
                    cityWeatherForm.tempTextView.text = multiDayDataWeather[index].avgTemp.toString() + tempUnit

                    cityWeatherForm.conditionTextView.text = multiDayDataWeather[index].conditionText

                    val uri = "http:"+multiDayDataWeather[0].conditionIcon
                    Picasso.get().load(uri).into(cityWeatherForm.imageCurrentWeatherImageView)


                    cityWeatherForm.windTextView.text = multiDayDataWeather[index].maxWind.toString() + windUnit

                    cityWeatherForm.rainTextView.text = multiDayDataWeather[index].totalPrecip.toString() + preicipitationUnit

                    cityWeatherForm.humidityTextView.text = multiDayDataWeather[index].avgHumidity.toString() + " %"
                    cityWeatherForm?.cloudImageView?.setImageDrawable(this.context?.let {
                        ContextCompat.getDrawable(
                            it, R.drawable.chanceofrain)
                    })
                    cityWeatherForm?.cloudTextView?.text = multiDayDataWeather[index].dailyChanceOfRain + " %"

                    cityWeatherForm.lastUpdateTextView.text = multiDayDataWeather[index].date
                }
                else ->{
                    cityFrag.day = 0
                    cityWeatherForm.previousDayButton.visibility = View.INVISIBLE
                    weatherTask(language, cityName, this, cityWeatherForm).execute()
                }
            }
        } else{
            if(cityFrag.multiDayDataWeather.size <=0){
                println("There's a problem with the board. It's a lousy size.")
                return
            }else{
                if(index<0 || index>3){
                    println("the index is not between 0 (inclusive) and 3 (inclusive)\n" +
                            "its value is : "+index)
                    return
                }
            }
            return
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            city_weather_frag(lang = "fr", cityName = "").apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    /**
     *
     *  The function of this class is to display the current weather data,
     * and to record the weather data at d+1 and d+2 in a table.
     *
     * */

    class weatherTask: AsyncTask<String, Int, String> {
        var lang: String = ""
        var nameCity: String = ""
        var cityWeather: city_weather_frag? = null
        var cityWeatherForm: View? = null
        var task: String? = null

        constructor(lang: String, nameCity: String, cityWeather: city_weather_frag, cityWeatherForm: View) : super() {
            this.lang = lang
            this.nameCity = nameCity
            this.cityWeather = cityWeather
            this.cityWeatherForm = cityWeatherForm
            this.task = task
      }

        /**
         *
         *The purpose of this method is to execute in the background the query, which is sent to the api,
         * it has for parameters the language to be used,
         * the name of the city and the number of days,
         * on which the weather data must be retrieved to be displayed.
         *
         * @param params is not used in this case.
         *
         * @return the response as json for processing in the onPostExecute method.
         * */

        override fun doInBackground(vararg params: String?): String {
            val response = HttpRequest.excuteGet("http://api.weatherapi.com/v1/forecast.json?key=36c11b9a575a4831bdf60903200906&lang="+lang+"&q="+nameCity+"&days=3")
            return response
        }

        /**
         *
         *this method parses the response retrieved by the query in the doInBackground method,
         * it directly displays the current weather data and records in a table,
         * the weather data at d+0, d+1, and, d+2.
         *
         * @param result
         * */

        override fun onPostExecute(result: String?) {
            val jsonResult = result

            try{
                val jsonObj = JSONObject(jsonResult)

                if(lang == "fr"){
                    val currentWeatherData = jsonObj.getJSONObject("current")
                    cityWeatherForm?.temperatureTextView?.text = currentWeatherData.getDouble("temp_c").toString() + " °c"
                    cityWeatherForm?.tempTextView?.text = currentWeatherData.getDouble("temp_c").toString() + " °c"

                    val condition = currentWeatherData.getJSONObject("condition")
                    cityWeatherForm?.conditionTextView?.text = condition.getString("text")

                    val uri = "http:"+condition.getString("icon")
                    Picasso.get().load(uri).into(cityWeatherForm?.imageCurrentWeatherImageView)


                    cityWeatherForm?.windTextView?.text = currentWeatherData.getDouble("wind_kph").toString() + " Km/h"

                    cityWeatherForm?.pressureTextView?.text = currentWeatherData.getDouble("pressure_mb").toString() +" mb"

                    cityWeatherForm?.rainTextView?.text = currentWeatherData.getDouble("precip_mm").toString() + " mm"

                    cityWeatherForm?.humidityTextView?.text = currentWeatherData.getInt("humidity").toString() + " %"
                    cityWeatherForm?.cloudImageView?.setImageDrawable(cityWeather?.context?.let {
                        ContextCompat.getDrawable(
                            it, R.drawable.cloud)
                    })
                    cityWeatherForm?.cloudTextView?.text = currentWeatherData.getInt("cloud").toString() + " %"
                    val date = currentWeatherData.getLong("last_updated_epoch")

                    cityWeatherForm?.lastUpdateTextView?.text = "dernière mise à jour :\n" + SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.FRANCE).format(Date((date + 7800) * 1000))

                    val multiDaysDataObj = jsonObj.getJSONObject("forecast")
                    val multiDaysDataArray = multiDaysDataObj.getJSONArray("forecastday")

                    for(i in 0.. multiDaysDataArray.length()-1){
                        val obj = multiDaysDataArray.getJSONObject(i)
                        val date = obj.getLong("date_epoch")
                        val dateFormated = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.FRANCE).format(Date(date * 1000))

                        val dayObj = obj.getJSONObject("day")

                        val avgTemp = dayObj.getDouble("avgtemp_c")
                        val maxWind = dayObj.getDouble("maxwind_kph")
                        val totalPrecip = dayObj.getDouble("totalprecip_mm")
                        val avgHumidity = dayObj.getDouble("avghumidity")
                        val dailyChanceOfRain = dayObj.getString("daily_chance_of_rain")

                        val condition = dayObj.getJSONObject("condition")
                        val conditionText = condition.getString("text")
                        val conditionIcon = condition.getString("icon")

                        cityWeather?.multiDayDataWeather?.add(
                            appData.multi_dayData(
                                avgTemp,
                                maxWind,
                                totalPrecip,
                                avgHumidity,
                                dailyChanceOfRain,
                                conditionText,
                                conditionIcon,
                                dateFormated
                            )
                        )
                    }


                } else {
                    val currentWeatherData = jsonObj.getJSONObject("current")
                    cityWeatherForm?.temperatureTextView?.text = currentWeatherData.getDouble("temp_f").toString() + " °f"
                    cityWeatherForm?.tempTextView?.text = currentWeatherData.getDouble("temp_f").toString() + " °f"

                    val condition = currentWeatherData.getJSONObject("condition")
                    cityWeatherForm?.conditionTextView?.text = condition.getString("text")
                    val uri = "http:"+condition.getString("icon")
                    Picasso.get().load(uri).into(cityWeatherForm?.imageCurrentWeatherImageView)

                    cityWeatherForm?.windTextView?.text = currentWeatherData.getDouble("wind_mph").toString() +" mph"

                    cityWeatherForm?.pressureTextView?.text = currentWeatherData.getDouble("pressure_in").toString() + " in"

                    cityWeatherForm?.rainTextView?.text = currentWeatherData.getDouble("precip_in").toString() + " in"

                    cityWeatherForm?.humidityTextView?.text = currentWeatherData.getInt("humidity").toString() + " %"
                    cityWeatherForm?.cloudTextView?.text = currentWeatherData.getInt("cloud").toString() + " %"

                    val date = currentWeatherData.getLong("last_updated_epoch")
                    cityWeatherForm?.lastUpdateTextView?.text = "LAST UPDATE :\n" + SimpleDateFormat(
                        "MM/dd/yyyy hh:mm a",
                        Locale.ENGLISH).format(Date((date + 7800) * 1000))

                    val multiDaysDataObj = jsonObj.getJSONObject("forecast")
                    val multiDaysDataArray = multiDaysDataObj.getJSONArray("forecastday")

                    for(i in 0.. multiDaysDataArray.length()-1){
                        val obj = multiDaysDataArray.getJSONObject(i)
                        val date = obj.getLong("date_epoch")
                        val dateFormated = SimpleDateFormat(
                            "MM/dd/yyyy",
                            Locale.ENGLISH).format(Date(date * 1000))

                        val dayObj = obj.getJSONObject("day")

                        val avgTemp = dayObj.getDouble("avgtemp_f")
                        val maxWind = dayObj.getDouble("maxwind_mph")
                        val totalPrecip = dayObj.getDouble("totalprecip_in")
                        val avgHumidity = dayObj.getDouble("avghumidity")
                        val dailyChanceOfRain = dayObj.getString("daily_chance_of_rain")

                        val condition = dayObj.getJSONObject("condition")
                        val conditionText = condition.getString("text")
                        val conditionIcon= condition.getString("icon")

                        cityWeather?.multiDayDataWeather?.add(
                            appData.multi_dayData(
                                avgTemp,
                                maxWind,
                                totalPrecip,
                                avgHumidity,
                                dailyChanceOfRain,
                                conditionText,
                                conditionIcon,
                                dateFormated
                            )
                        )
                    }
                }



            }catch (e: Exception){
                println("error message from weather city frag : " +e)
            }
        }

    }
}
