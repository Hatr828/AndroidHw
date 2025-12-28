package com.example.androidhw

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class CarAdapter(context: Context, cars: List<CarModel>) :
    ArrayAdapter<CarModel>(context, R.layout.item_car, cars) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_car, parent, false)

        val holder = (view.tag as? ViewHolder) ?: ViewHolder(view).also { view.tag = it }
        val car = getItem(position)
        if (car != null) {
            holder.image.setImageResource(car.photoResId)
            holder.title.text = "${car.brand} ${car.model}"
            holder.yearCost.text = context.getString(R.string.year_cost_format, car.year, car.cost)
            holder.description.text = car.description
        }
        return view
    }

    private class ViewHolder(view: View) {
        val image: ImageView = view.findViewById(R.id.image_car)
        val title: TextView = view.findViewById(R.id.text_title)
        val yearCost: TextView = view.findViewById(R.id.text_year_cost)
        val description: TextView = view.findViewById(R.id.text_description)
    }
}
