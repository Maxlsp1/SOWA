package com.example.simpleOpenWeatherApp

import android.graphics.Color
import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.simpleOpenWeatherApp.dummy.DummyContent
import kotlinx.android.synthetic.main.city_list_item_content.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class CityListRecyclerViewAdapter(
    private val values: List<DummyContent.CityListItem>
) : RecyclerView.Adapter<CityListRecyclerViewAdapter.ViewHolder>() {

    var appParams: app_params? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val cityListView = inflater.inflate(R.layout.city_list_item_content, parent, false)

        return ViewHolder(cityListView)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.contentView.text = item.content
        holder.itemView.deleteCityButton.text = if(item.language == "fr") "Supprimer" else "Delete"
        holder.itemView.deleteCityButton.setBackgroundColor(Color.parseColor("#a600ff"))

        holder.itemView.deleteCityButton.setOnClickListener(){
            appParams?.writeSettingsInJsonFile("deleteCity", item.id.toInt())
        }

    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentView: TextView = view.findViewById(R.id.content)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}