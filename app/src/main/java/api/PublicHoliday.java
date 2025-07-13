package api;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Objects;

public class PublicHoliday {
    @SerializedName("date")
    private String date;
    @SerializedName("localName")
    private String localName;
    @SerializedName("name")
    private String name;
    @SerializedName("countryCode")
    private String countryCode;
    @SerializedName("fixed")
    private boolean fixed;
    @SerializedName("global")
    private boolean global;
    @SerializedName("counties")
    private List<String> counties;
    @SerializedName("launchYear")
    private Integer launchYear;

    public PublicHoliday() {}


    public String getDate() { return date; }
    public String getLocalName() { return localName; }
    public String getName() { return name; }
    //  public String getCountryCode() { return countryCode; }
    //  public boolean isFixed() { return fixed; }
    public boolean isGlobal() { return global; }
    public List<String> getCounties() { return counties; }
    // public Integer getLaunchYear() { return launchYear; }


    public void setDate(String date) { this.date = date; }
    // public void setLocalName(String localName) { this.localName = localName; }
    public void setName(String name) { this.name = name; }
    // public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    //  public void setFixed(boolean fixed) { this.fixed = fixed; }
    //  public void setGlobal(boolean global) { this.global = global; }
    // public void setCounties(List<String> counties) { this.counties = counties; }
   // public void setLaunchYear(Integer launchYear) { this.launchYear = launchYear; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicHoliday that = (PublicHoliday) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(localName, that.localName) &&
                Objects.equals(name, that.name) &&
                Objects.equals(countryCode, that.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, localName, name, countryCode);
    }

}
