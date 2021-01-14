package com.example.simpleOpenWeatherApp

import kotlin.properties.Delegates

/**
 * This class contains all the classes necessary for the proper functioning of the application,
 * they will be used either to manipulate the objects that will be used to save in the file,
 * or to retrieve information from it, or to extract data from the query.
 * */

class appData {

    var dataCity: ArrayList<data>;
    var  appJSONsettings: settings? = null;

    constructor(appJSONsettings: settings?, dataCity: ArrayList<data>) {
        this.appJSONsettings = appJSONsettings
        this.dataCity = dataCity
    }

    /**
     * This class will be used to manipulate information related to the language used by the user.
     * */

    class languageData {
        var languageName: String = ""
        var languageValue: String = ""

        constructor(languageName: String, languageValue: String) {
            this.languageName = languageName
            this.languageValue = languageValue
        }

        override fun toString(): String {
            return languageName
        }
    }


    /**
     * This class will be used to manipulate information related to the parameters,
     * that the user prefers to use as the language.
     * */
    class settings{
        var  nbCity: Int = 0
        var  language : String = "fr"

        constructor( nbCity: Int, language: String) {
            this.nbCity = nbCity
            this.language = language
        }
    }

    /**
     * This class will be used to manipulate the information related to the cities data that the user has saved,
     * such as their lattitude, longitude and name,
     * which will be used later to verify that the user has not already done so,
     * saved the city he wants to add.
     * */

    class data {
        var id: Int = 0
        var  city_Name: String;
        var  lat by Delegates.notNull<Double>();
        var  longi by Delegates.notNull<Double>();

        constructor(id: Int, city_Name: String, lat: Double, longi: Double) {
            this.id = id
            this.city_Name = city_Name
            this.lat = lat
            this.longi = longi
        }
    }

    /**
     * This class will be used to manipulate the information related to the response of the query send weatherApi.
     * */

    class multi_dayData{
       var avgTemp: Double = 0.00
        var maxWind: Double = 0.00
        var totalPrecip: Double = 0.00
        var avgHumidity: Double = 0.00
        var dailyChanceOfRain: String? = null
        var date: String? = null

        constructor(
            avgTemp: Double,
            maxWind: Double,
            totalPrecip: Double,
            avgHumidity: Double,
            dailyChanceOfRain: String?,
            conditionText: String?,
            conditionIcon: String?,
            date: String?
        ) {
            this.avgTemp = avgTemp
            this.maxWind = maxWind
            this.totalPrecip = totalPrecip
            this.avgHumidity = avgHumidity
            this.dailyChanceOfRain = dailyChanceOfRain
            this.conditionText = conditionText
            this.conditionIcon = conditionIcon
            this.date = date
        }

        var conditionText: String? = null
        var conditionIcon: String? = null



    }
}