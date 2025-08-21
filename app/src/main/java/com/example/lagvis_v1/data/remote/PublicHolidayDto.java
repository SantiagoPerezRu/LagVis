// com/example/lagvis_v1/data/remote/PublicHolidayDto.java
package com.example.lagvis_v1.data.remote;

import java.util.List;

public class PublicHolidayDto {
    public String date;        // "yyyy-MM-dd"
    public String localName;
    public String name;
    public String countryCode;
    public Boolean global;     // puede venir null
    public List<String> counties; // puede venir null
}
