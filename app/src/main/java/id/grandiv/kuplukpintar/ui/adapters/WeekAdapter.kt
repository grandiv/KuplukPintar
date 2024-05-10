package id.grandiv.kuplukpintar.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.grandiv.kuplukpintar.R
import id.grandiv.kuplukpintar.models.Day
import android.graphics.Color

class WeekAdapter(private val weekDays: List<Day>) : RecyclerView.Adapter<WeekAdapter.WeekViewHolder>() {

    class WeekViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayOfWeek: TextView = view.findViewById(R.id.tv_day_of_week)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val viewCurrentDate: View = view.findViewById(R.id.view_current_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar, parent, false)
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val day = weekDays[position]
        holder.tvDayOfWeek.text = day.dayOfWeek
        holder.tvDate.text = day.date
        if (day.isCurrentDate) {
            holder.viewCurrentDate.setBackgroundResource(R.drawable.blue_circle)
            holder.viewCurrentDate.visibility = View.VISIBLE
            holder.tvDate.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            holder.viewCurrentDate.visibility = View.GONE
            holder.tvDate.setTextColor(Color.parseColor("#1A5E94"))
        }
    }

    override fun getItemCount() = weekDays.size
}