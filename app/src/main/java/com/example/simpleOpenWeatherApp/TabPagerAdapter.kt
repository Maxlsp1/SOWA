package com.example.simpleOpenWeatherApp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 *
 * This method makes it possible to create the fragments and to display them in the good positions,
 * it also makes it possible to pass to the fragments the various information,
 * which were recovered in the json usersetings file.
 *
 *
 * @param firstUse if this parameter has the value true, only the app_params fragment will be instantiated,
 * otherwise if its value is false all the fragments will be instantiated.
 *
 * @param tabCount returns the number of tabs that have been created.
 *
 * @param nbCityFragment corresponds to the size of table -1 in which the data of the cities are retrieved,
 * it will be used for the index.
 *
 * @param language matches the language that the user has saved in the settings if this is not his first use,
 * otherwise the default language of the application is English.
 *
 * @param dataCity matches to the table in which the data of the cities are recorded (their names, latitude, longitude).
 *
 * */


class TabPagerAdapter(fm: FragmentManager,private var firstUse: Boolean , private var tabCount: Int,
                      private var nbCityFragment: Int, private var language: String, private var dataCity: ArrayList<appData.data>) :
    FragmentPagerAdapter(fm) {


    /**
     *
     * The purpose of this method is to instantiate the different fragments
     * and assign them to the desired tab position.
     *
     * @param position in order to indicate the position of the fragment
     * thanks to the iterative structure when (which is in fact a switch)
     *
     * @return a fragment
     * */

    override fun getItem(position: Int): Fragment {
        var aFragment: Fragment
        var indexCity = nbCityFragment
        val paramsIndex = indexCity+1

        aFragment = city_weather_frag(language, "")

       when(firstUse) {
           true -> {
               when (position) {
                   0 -> aFragment = app_params(indexCity, language, dataCity)
               }
           }

           false -> {
               if (nbCityFragment <= 0) {
                   when (position) {
                       0 -> aFragment = city_weather_frag(language, dataCity[0].city_Name)
                       1 -> aFragment = app_params(indexCity, language, dataCity)
                   }
               } else {
                   if(position<paramsIndex){
                       indexCity = position
                   }
                   when (position) {
                       indexCity -> aFragment = city_weather_frag(language, dataCity[indexCity].city_Name)
                       paramsIndex -> aFragment = app_params(indexCity, language, dataCity)
                   }
               }
           }
       }
        return aFragment
    }

    /**
     *
     * returns the total number of tabs created
     *
     * @return the total number of tabs created
     * */

    override fun getCount(): Int {
        return tabCount
    }
}