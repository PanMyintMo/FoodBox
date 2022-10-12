package com.pan.foodbox.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.model.Marker
import com.pan.foodbox.R

class InfoWindowAdapter(context: Context) :
    com.google.android.gms.maps.GoogleMap.InfoWindowAdapter {

    private val contextView =
        (context as Activity).layoutInflater.inflate(R.layout.infowindow, null)

    override fun getInfoContents(marker: Marker): View? {
        renderView(marker,contextView)
        return contextView
    }

    override fun getInfoWindow(marker: Marker): View? {
        renderView(marker,contextView)
        return contextView
    }

     private fun renderView(marker: Marker?, contextView: View) {

        val title = marker?.title
        val description = marker?.snippet

        val titleTextView = contextView.findViewById<TextView>(R.id.title)
        titleTextView.text = title


    }
}