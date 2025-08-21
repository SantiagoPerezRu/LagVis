package com.example.lagvis_v1.data.mapper;

import com.example.lagvis_v1.data.remote.PublicHolidayDto;
import com.example.lagvis_v1.dominio.PublicHoliday;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HolidaysMappers {
    private HolidaysMappers() {
    }

    public static List<PublicHoliday> toDomain(List<PublicHolidayDto> dtos) {
        List<PublicHoliday> out = new ArrayList<>();
        if (dtos == null) return out;
        for (PublicHolidayDto d : dtos) {
            boolean global = d.global != null ? d.global : false;
            List<String> counties = d.counties != null ? new ArrayList<>(d.counties) : Collections.emptyList();
            out.add(new PublicHoliday(
                    d.date, d.localName, d.name, d.countryCode, global, counties
            ));
        }
        return out;
    }
}
