package com.example.simpleOpenWeatherApp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androdocs.httprequest.HttpRequest
import com.example.simpleOpenWeatherApp.dummy.DummyContent
import kotlinx.android.synthetic.main.add_city_form.view.*
import kotlinx.android.synthetic.main.app_params_form.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [app_params.newInstance] factory method to
 * create an instance of this fragment.
 */
class app_params(nbCity: Int, lang: String, dataCity: ArrayList<appData.data>?) : Fragment(), AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var nbCity: Int = nbCity+1
    var listCityTab = arrayListOf<DummyContent.CityListItem>()
    var Language = lang
    var languageValue: appData.languageData? = null
    var dataCityTab = dataCity
    var save: Boolean = false
    var nameCity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val appParamsForm = inflater.inflate(R.layout.app_params_form, container, false)
        val addCityForm = inflater.inflate(R.layout.add_city_form, container, false)
        val listCityForm = inflater.inflate(R.layout.city_list_item_list, container, false)

        val dataTab = arrayListOf<appData.languageData>()

        if(Language == "fr"){
            val frenchLanguage = appData.languageData("Français", "fr")
            dataTab.add(frenchLanguage)

            val englishLanguage = appData.languageData("Anglais", "en")
            dataTab.add(englishLanguage)

            appParamsForm.languageSettingsTextView.setText("Langue utilisée")
            addCityForm.cityNameEditText.hint = "Nom de la ville"
            addCityForm.addCityButon.setText("Ajouter")

        } else {

            val englishLanguage = appData.languageData("English", "en")
            dataTab.add(englishLanguage)

            val frenchLanguage = appData.languageData("French", "fr")
            dataTab.add(frenchLanguage)

            appParamsForm.languageSettingsTextView.setText("Language used")
            addCityForm.cityNameEditText.hint = "City name"
            addCityForm.addCityButon.setText("Add")
        }

        val adapter = context?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, dataTab ) }

        // Set the drop down view resource
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        appParamsForm.languageSpinner.adapter = adapter
        appParamsForm.languageSpinner.onItemSelectedListener = this

        if(nbCity < 3){
            appParamsForm.addCityLayout.addView(addCityForm)
            addCityForm.addCityButon.setBackgroundColor(Color.parseColor("#8800ff"))
        }

        val nbCityIndex = nbCity-1

        if(dataCityTab?.size!! >0){
            if(nbCityIndex >= 1){
                for(i in 0..nbCityIndex) {
                    listCityTab?.add(DummyContent.CityListItem(i.toString(),dataCityTab!![i].city_Name, Language, "none" ))
                }
            } else {
                listCityTab?.add(DummyContent.CityListItem(0.toString(),dataCityTab!![0].city_Name, Language, "none" ))
            }

            val textList = listCityForm.findViewById<View>(R.id.list) as? RecyclerView
            val listCityAdapter = CityListRecyclerViewAdapter(listCityTab)

            listCityAdapter.appParams = this
            textList?.adapter = listCityAdapter
            textList?.layoutManager = LinearLayoutManager(listCityForm.context)

            appParamsForm.listCityLayout.addView(listCityForm)
        }

        appParamsForm.languageSpinner.background.apply {
            Drawable.createFromPath("@android:drawable/btn_dropdown")
        }


        addCityForm.addCityButon.setOnClickListener(){
            nameCity = addCityForm.cityNameEditText.text.toString()
            writeSettingsInJsonFile("addCity", nameCity)
        }

        return appParamsForm
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            languageValue = parent.selectedItem as appData.languageData
            writeSettingsInJsonFile("setLanguage", languageValue!!.languageValue)
        }
    }

    /**
     * this method aims to write in the json file,
     * the different parameters that the user will have chosen as the language,
     * as well as the different cities that he will have chosen to add.
     *
     * @param task is the task that the method must perform, either saving the language, or deleting or adding a city.
     * @param valueTowrite is the value that must be written, or used to manipulate the file.
     * */

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun writeSettingsInJsonFile(task: String, valueTowrite: Any){
        var output: Writer? = null
        val file = File(context?.filesDir, "usersettings.json")
        when(task){
            "setLanguage" -> {
                if(save === true){
                    var jsonFile: String? = null

                    if (file != null) {
                        jsonFile = file.bufferedReader().use { it.readText() }
                    }

                    val obj = JSONObject(jsonFile)
                    var settings = obj.getJSONObject("settings")
                    settings.put("lang", valueTowrite)

                    try {
                        output = BufferedWriter(FileWriter(file))
                        output.write(obj.toString(3))
                        output.close()
                    }
                    catch (e : Exception){
                        print(e)
                    }
                    val intent = Intent(context, MainActivity::class.java)
                    requireContext().startActivity(intent)
                } else {
                    //return
                    save = true
                }
            }

            "addCity" -> {
                val nbCityIndex = nbCity-1
                weatherTask(nbCityIndex ,Language, valueTowrite.toString(), this).execute()
            }

            "deleteCity" ->{

                var json : String? = null

                try {
                    val file = File(this.context?.filesDir, "usersettings.json")

                    if (file != null) {
                        json = file.bufferedReader().use { it.readText() }
                    }
                    val jsonObject = JSONObject(json)
                    val cityData = jsonObject.getJSONObject("cityData")
                    var jsonarr = cityData.getJSONArray("data")


                    for (i in 0..jsonarr.length() - 1) {

                        if(i === valueTowrite){
                            jsonarr.remove(i)
                        }
                    }

                    cityData.put("data", jsonarr)

                    var output: Writer? = null
                    output = BufferedWriter(FileWriter(file))
                    output.write(jsonObject.toString(3))
                    output.close()

                    val intent = Intent(context, MainActivity::class.java)
                    requireContext().startActivity(intent)

                } catch (e: Exception){
                    println("error in Json Delete === " + e)
                }
            }
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
            app_params(nbCity =  0, lang = "french", dataCity = null).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        println("onNothingSelected(parent: AdapterView<*>?) fun is not implemented")
    }

    /**
     * A simple [AsyncTask] subclass.
     *
     * it is mainly used to run the query in the background,
     * check if a city is already saved, or, save a city.
     * */

    class weatherTask: AsyncTask<String, Int, String> {
        var lang: String = ""
        var nameCity: String = ""
        var appParams: app_params? = null
        var dataName: String = ""
        var dataLat: Double = 0.00
        var dataLong: Double = 0.00
        var index: Int = 0


        val failedFrenchShowToast = Runnable {   println("it work !!!! ")
            Toast.makeText(appParams?.context, "la ville de "+ nameCity +" n'a pas pu être ajoutée", Toast.LENGTH_LONG).show() }

        val succesFrenchShowToast = Runnable {   println("it work !!!! ")
            Toast.makeText(appParams?.context, "la ville de "+ nameCity +" à été ajoutée", Toast.LENGTH_LONG).show() }

        val failedEnglishShowToast = Runnable {   println("it work !!!! ")
            Toast.makeText(appParams?.context, "the city of  "+ nameCity +" could not be added", Toast.LENGTH_LONG).show() }

        val succesEnglishShowToast = Runnable {   println("it work !!!! ")
            Toast.makeText(appParams?.context, "the city of  "+ nameCity +" has been added", Toast.LENGTH_LONG).show() }

        val alreadyExistFrenchShowToast = Runnable {   println("it work !!!! ")
            Toast.makeText(appParams?.context, "la ville de "+ nameCity +" est déja sauvegardée", Toast.LENGTH_LONG).show() }

        val alreadyExistEnglishShowToast = Runnable {   println("it work !!!! ")
            Toast.makeText(appParams?.context, "the city of  "+ nameCity +" already saved", Toast.LENGTH_LONG).show() }

        constructor(index: Int ,lang: String, nameCity: String, appParams: app_params) : super() {
            this.index = index
            this.lang = lang
            this.nameCity = nameCity
            this.appParams = appParams
        }

        /**
         * allows you to execute the request in the background.
         * Without this method the request could not be executed.
         *
         * @param params In this case the parameter is not used.
         *
         * @return returns a string which, in this case, is the response to the query
         * */

        override fun doInBackground(vararg params: String?): String {
            val response = HttpRequest.excuteGet("http://api.weatherapi.com/v1/current.json?key=36c11b9a575a4831bdf60903200906&lang="+lang+"&q="+nameCity)
            return response
        }

        /**
         *This method first checks if the city has already been saved in the JSON file.
         * If the city exists, the method stops there,
         * otherwise if the city does not exist,
         * the answer passed in the parameter is parsed,
         * (the answer of the request is returned as json),
         * all useful information is then saved.
         *
         * @param result response of the query that is executed in the doInBackground method.
         * */

        override fun onPostExecute(result: String?) {
           val jsonResult = result
            var alreadyExist = false
            if(jsonResult != null){
                try {
                    val jsonObj = JSONObject(jsonResult)
                  val location = jsonObj.getJSONObject("location")
                    dataName = location.getString("name")
                    dataLat = location.getDouble("lat")
                    dataLong = location.getDouble("lon")

                    if(index>=0){
                        for(i in 0..index){
                            if(appParams?.dataCityTab?.get(i)?.city_Name == dataName){


                                if(lang == "fr"){
                                    alreadyExistFrenchShowToast.run()
                                } else{
                                    alreadyExistEnglishShowToast.run()
                                }
                                alreadyExist = true
                            }
                        }
                    }

                    if(alreadyExist === false){
                        try {
                            val intent = Intent(appParams?.context, MainActivity::class.java)
                            val file = File(appParams?.context?.filesDir, "usersettings.json")
                            var jsonFile: String? = null

                            if (file != null) {
                                jsonFile = file.bufferedReader().use { it.readText() }
                            }

                            val obj = JSONObject(jsonFile)
                            var cityData = obj.getJSONObject("cityData")
                            if(cityData.getBoolean("dataAdded") === true ){
                                var data = cityData.getJSONArray("data")
                                var lastIndex =  data.length()
                                val dataChildObject = JSONObject()

                                dataChildObject.put("id",lastIndex)
                                dataChildObject.put("name", dataName)
                                dataChildObject.put("lat", dataLat)
                                dataChildObject.put("long", dataLong)
                                data.put(lastIndex,dataChildObject)
                                cityData.put("data", data)
                            } else {
                                obj.put("firstUse", false)
                                var data = JSONArray()

                                val dataChildObject = JSONObject()

                                dataChildObject.put("id",0)
                                dataChildObject.put("name", dataName)
                                dataChildObject.put("lat", dataLat)
                                dataChildObject.put("long", dataLong)
                                data.put(0,dataChildObject)
                                cityData.put("dataAdded", true)
                                cityData.put("data", data)

                            }

                            var output: Writer? = null
                            output = BufferedWriter(FileWriter(file))
                            output.write(obj.toString(3))
                            output.close()

                            if(appParams?.Language == "fr" ){
                                succesFrenchShowToast.run()
                            } else {
                                succesEnglishShowToast.run()
                            }

                            appParams?.requireContext()?.startActivity(intent)

                        } catch (e: JSONException){
                            println("save JSON === " + e)
                            if(appParams?.Language == "fr" ){
                                failedFrenchShowToast.run()
                            } else {
                                failedEnglishShowToast.run()
                            }
                        }
                    }

                } catch (e: JSONException) {
                    println("save JSON === " + e)
                    if(appParams?.Language == "fr" ){
                       failedFrenchShowToast.run()
                    } else {
                        failedEnglishShowToast.run()
                    }
                }

            } else {
                if(appParams?.Language == "fr" ){
                    failedFrenchShowToast.run()
                } else {
                    failedEnglishShowToast.run()
                }
            }
        }

    }
}
