package com.minimal.launcher

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(
    private val context: android.content.Context,
    private val apps: List<MainActivity.AppInfo>,
    private val iconSizePx: Int,
    var iconColorsEnabled: Boolean = false,
    private val onClick: ((MainActivity.AppInfo) -> Unit)? = null,
    private val onLongClick: ((View, MainActivity.AppInfo) -> Boolean)? = null
) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.layoutParams.width = iconSizePx
        holder.icon.layoutParams.height = iconSizePx
        holder.icon.setImageDrawable(app.icon)
        if (iconColorsEnabled) {
            holder.icon.colorFilter = null
        } else {
            holder.icon.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        holder.label.text = app.label
        holder.itemView.setOnClickListener { onClick?.invoke(app) }
        holder.itemView.setOnLongClickListener { onLongClick?.invoke(holder.itemView, app) ?: false }
    }

    override fun getItemCount(): Int = apps.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.app_icon)
        val label: TextView = view.findViewById(R.id.app_label)
    }
}
