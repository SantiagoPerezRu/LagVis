// app/src/main/java/com/example/lagvis_v1/dominio/HolidaysResponse.java
package com.example.lagvis_v1.data.remote;

import com.example.lagvis_v1.dominio.PublicHoliday;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HolidaysResponse {
    @SerializedName("year")    private Integer year;
    @SerializedName("slug")    private String slug;
    @SerializedName("display") private String display;
    // OJO: scope top-level es meta, no lo uses para pintar
    @SerializedName("scope")   private String scope;
    @SerializedName("holidays") private List<PublicHoliday> holidays;

    public Integer getYear() { return year; }
    public String getSlug() { return slug; }
    public String getDisplay() { return display; }
    public String getScope() { return scope; }
    public List<PublicHoliday> getHolidays() { return holidays; }
}
