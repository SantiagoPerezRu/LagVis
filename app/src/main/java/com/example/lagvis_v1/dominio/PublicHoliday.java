package com.example.lagvis_v1.dominio;

import java.util.Collections;
import java.util.List;

public class PublicHoliday {
    private final String date;
    private final String localName;
    private final String name;
    private final String countryCode;
    private final boolean global;
    private final List<String> counties;

    public PublicHoliday(String date, String localName, String name, String countryCode,
                         boolean global, List<String> counties) {
        this.date = date != null ? date : "";
        this.localName = localName != null ? localName : "";
        this.name = name != null ? name : "";
        this.countryCode = countryCode != null ? countryCode : "";
        this.global = global;
        this.counties = counties != null ? counties : Collections.emptyList();
    }

    public String getDate() { return date; }
    public String getLocalName() { return localName; }
    public String getName() { return name; }
    public String getCountryCode() { return countryCode; }
    public boolean isGlobal() { return global; }
    public List<String> getCounties() { return counties; }
}
