// app/src/main/java/com/example/lagvis_v1/data/mapper/HolidaysMappers.java
package com.example.lagvis_v1.data.mapper;

import com.example.lagvis_v1.data.remote.HolidaysResponse;
import com.example.lagvis_v1.dominio.PublicHoliday;

import java.util.ArrayList;
import java.util.List;

public final class HolidaysMappers {

    private HolidaysMappers() {}

    public static List<PublicHoliday> map(HolidaysResponse res) {
        List<PublicHoliday> out = new ArrayList<>();
        if (res == null || res.getHolidays() == null) return out;

        String display = res.getDisplay(); // p.ej. "Madrid"

        for (PublicHoliday h : res.getHolidays()) {
            if (h == null) continue;

            // Si el backend no pone province por holiday, usa el display del response
            if ((h.getProvince() == null || h.getProvince().isEmpty()) && display != null) {
                h.setProvince(display);
            }

            // Asegúrate de no perder el scope (viene dentro del holiday)
            // h.setScope(h.getScope()); // no hace falta, ya viene en 'h'

            out.add(h);
        }
        return out;
    }
}
