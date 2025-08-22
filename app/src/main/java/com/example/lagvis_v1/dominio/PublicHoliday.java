// app/src/main/java/com/example/lagvis_v1/dominio/PublicHoliday.java
package com.example.lagvis_v1.dominio;

import com.google.gson.annotations.SerializedName;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

public class PublicHoliday {

    @SerializedName("date") private String date;
    @SerializedName("name") private String name;

    // <-- importante: leer "scope" o "type"
    @SerializedName(value = "scope", alternate = { "type" })
    private String scope;

    @SerializedName("province") private String province;
    @SerializedName(value = "autonomy", alternate = { "ccaa", "autonomia", "community" })
    private String autonomy;

    @SerializedName(value = "localName", alternate = { "local_name", "title" })
    private String localName;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getAutonomy() { return autonomy; }
    public void setAutonomy(String autonomy) { this.autonomy = autonomy; }
    public String getLocalName() { return localName != null ? localName : name; }
    public void setLocalName(String localName) { this.localName = localName; }

    public String getScopeNormalized() {
        String s = scope;
        if (s == null) return "otros";
        String t = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT).trim();
        if ("estatal".equals(t)) return "nacional";
        if (t.startsWith("autonom")) return "autonomico";
        if (t.startsWith("municip")) return "municipal";
        if (t.startsWith("local") || t.startsWith("provinc")) return "local";
        if (t.startsWith("info")) return "info";
        if ("nacional".equals(t) || "autonomico".equals(t) || "municipal".equals(t)
                || "local".equals(t) || "info".equals(t)) return t;
        return "otros";
    }

    @Override public String toString() {
        return "PublicHoliday{date='" + date + "', name='" + name + "', scope='" + scope +
                "', province='" + province + "', autonomy='" + autonomy + "', localName='" + localName + "'}";
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicHoliday)) return false;
        PublicHoliday that = (PublicHoliday) o;
        return Objects.equals(date, that.date) && Objects.equals(name, that.name);
    }

    @Override public int hashCode() {
        return Objects.hash(date, name);
    }
}
