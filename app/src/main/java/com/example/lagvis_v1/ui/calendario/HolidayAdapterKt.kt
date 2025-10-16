// ui/calendario/HolidayAdapterKt.kt
package com.example.lagvis_v1.ui.calendario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lagvis_v1.R
import com.example.lagvis_v1.dominio.model.holidays.PublicHolidayKt

class HolidayAdapterKt(
    private var holidayList: MutableList<PublicHolidayKt> = mutableListOf()
) : RecyclerView.Adapter<HolidayAdapterKt.HolidayViewHolder>() {

    fun setHolidayList(newHolidayList: List<PublicHolidayKt>) {
        holidayList.clear()
        holidayList.addAll(newHolidayList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_holiday, parent, false)
        return HolidayViewHolder(view)
    }

    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        val holiday = holidayList[position]
        holder.tvHolidayDate.text = holiday.date
        holder.tvHolidayLocalName.text = holiday.name ?: holiday.localName ?: "Festivo"
        holder.tvHolidayName.text = holiday.name ?: "Festivo"
    }

    override fun getItemCount(): Int = holidayList.size

    class HolidayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHolidayDate: TextView = itemView.findViewById(R.id.tvHolidayDate)
        val tvHolidayLocalName: TextView = itemView.findViewById(R.id.tvHolidayLocalName)
        val tvHolidayName: TextView = itemView.findViewById(R.id.tvHolidayName)
    }
}
