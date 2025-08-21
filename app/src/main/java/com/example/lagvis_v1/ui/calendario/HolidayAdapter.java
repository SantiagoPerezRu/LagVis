
package com.example.lagvis_v1.ui.calendario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.dominio.PublicHoliday;

import java.util.List;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {

    private List<PublicHoliday> holidayList;

    public HolidayAdapter(List<PublicHoliday> holidayList) {
        this.holidayList = holidayList;
    }

    // Método para actualizar los datos del adaptador
    public void setHolidayList(List<PublicHoliday> newHolidayList) {
        this.holidayList = newHolidayList;
        notifyDataSetChanged(); // Notifica al RecyclerView que los datos han cambiado
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_holiday, parent, false);
        return new HolidayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        PublicHoliday holiday = holidayList.get(position);
        holder.tvHolidayDate.setText(holiday.getDate());
        holder.tvHolidayLocalName.setText(holiday.getLocalName());
        holder.tvHolidayName.setText(holiday.getName());
    }

    @Override
    public int getItemCount() {
        return holidayList.size();
    }

    public static class HolidayViewHolder extends RecyclerView.ViewHolder {
        TextView tvHolidayDate;
        TextView tvHolidayLocalName;
        TextView tvHolidayName;

        public HolidayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHolidayDate = itemView.findViewById(R.id.tvHolidayDate);
            tvHolidayLocalName = itemView.findViewById(R.id.tvHolidayLocalName);
            tvHolidayName = itemView.findViewById(R.id.tvHolidayName);
        }
    }
}