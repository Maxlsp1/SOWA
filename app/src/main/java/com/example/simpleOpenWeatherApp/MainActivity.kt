package com.example.simpleOpenWeatherApp

import android.content.pm.FeatureInfo
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer

class MainActivity : AppCompatActivity() {
    var nbCity: Int = 0
    var firstUse: Boolean = true
    var cityDataTab = arrayListOf<appData.data>()
    var aSetting: appData.settings? = null
    var language: String = "en"
    var dataApp: appData? = null
    var tabParameterName = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        tab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        appbar.setExpanded(false)
        configureTabLayout()
    }

    /**
     *this method allows to create the different tabs used in the swipe view,
     * thanks to the TabPagerAdapter method,
     * which will create the fragments that will display the different pages of the application .
     * */

    private fun configureTabLayout() {
        dataApp = getJSONAppData()
        val nbCityIndex = cityDataTab.size -1

        if(language == "fr"){
            tabParameterName = "Paramètres"
        } else {
            tabParameterName = "Parameters"
        }

        if(firstUse === true || nbCityIndex<0){
            tab.addTab(tab.newTab().setText(tabParameterName))
        } else {
            if(nbCityIndex === 0) {
                tab.addTab(tab.newTab().setText(cityDataTab[0].city_Name))
                tab.addTab(tab.newTab().setText(tabParameterName))
            } else {
                for(i in 0..nbCityIndex) {
                    tab.addTab(tab.newTab().setText(cityDataTab[i].city_Name))
                }
                tab.addTab(tab.newTab().setText(tabParameterName))

            }
        }


        val adapter = TabPagerAdapter(supportFragmentManager,
            firstUse, tab.tabCount, nbCityIndex, language, cityDataTab)
        pager.adapter = adapter


        pager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(tab))
        tab.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })
    }

    /**
     * this method parses the json usersettings file
     * which contains the various information set by the user.
     * If the user has never launched the application then the method will create the json usersettings file with different information such as the language used,
     * the dataAdded object will be set to false ... .
     * The firstUse object will be set to false only when the user has entered at least one city in the json file.
     *
     * @return returns an appData object that will be used throughout the application,
     * to configure the different views*/

    fun getJSONAppData(): appData? {


        var json : String? = null
        var data: appData? = null


        try {
            val file = this.openFileInput("usersettings.json")

            if (file != null) {
                json = file.bufferedReader().use { it.readText() }
            }

            var jsonObject = JSONObject(json)
            firstUse = jsonObject.getBoolean("firstUse")

            var settings = jsonObject.getJSONObject("settings")

            nbCity = settings.getInt("nbCity")

            language = settings.getString("lang")

            aSetting?.nbCity = nbCity
            aSetting?.language = language

            val cityData = jsonObject.getJSONObject("cityData")

            if(cityData.getBoolean("dataAdded") === true ) {
                var jsonarr = cityData.getJSONArray("data")
                if(jsonarr.length() === 1){
                    var jsonobjCityData = jsonarr.getJSONObject(0)

                    val id = jsonobjCityData.getInt("id")
                    val name = jsonobjCityData.getString("name")
                    val lat = jsonobjCityData.getDouble("lat")
                    val longi = jsonobjCityData.getDouble("long")
                    cityDataTab.add(appData.data(id ,name, lat, longi))
                } else {

                    for (i in 0..jsonarr.length() - 1) {

                        var jsonobjCityData = jsonarr.getJSONObject(i)

                        val id = jsonobjCityData.getInt("id")
                        val name = jsonobjCityData.getString("name")
                        val lat = jsonobjCityData.getDouble("lat")
                        val longi = jsonobjCityData.getDouble("long")
                        cityDataTab.add(appData.data(id ,name, lat, longi))
                    }
                }
            } else {
                data = appData(aSetting, cityDataTab)
            }
        }
        catch (e : Exception){
            println("file does not exist === " + e)

            val obj = JSONObject()
            val childSettingsJsonObj = JSONObject()
            val childCityDataJsonObj = JSONObject()

            childSettingsJsonObj.put("nbCity", 1)
            childSettingsJsonObj.put("lang", "fr")
            childCityDataJsonObj.put("dataAdded", false)

            obj.put("firstUse", true)
            obj.put("settings", childSettingsJsonObj)
            obj.put("cityData",childCityDataJsonObj)

            var output: Writer? = null
            val file = File(this.filesDir, "usersettings.json")
            output = BufferedWriter(FileWriter(file))
            output.write(obj.toString(3))
            output.close()
        }
        return data
    }

}
