package algonquin.cst2335.cst2355final.tianjiaosun;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Sun {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="sunId")
    public long sunId;

    @ColumnInfo(name="sunLatitude")
    protected String sunLatitude;

    @ColumnInfo(name="sunLongitude")
    protected String sunLongitude;

    @ColumnInfo(name="sunrise")
    protected String sunrise;

    @ColumnInfo(name="sunset")
    protected String sunset;

    @ColumnInfo(name="solar_noon")
    protected String solar_noon;

    @ColumnInfo(name="golder_hour")
    protected String golder_hour;

    @ColumnInfo(name="timezone")
    protected String timezone;

    @ColumnInfo(name="cityName")
    protected String cityName;

    public Sun(){}

    // constructor
    public Sun(String sunLatitude, String sunLongitude, String sunrise, String sunset, String solar_noon, String golder_hour, String timezone, String cityName) {
        this.sunLatitude = sunLatitude;
        this.sunLongitude = sunLongitude;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.solar_noon = solar_noon;
        this.golder_hour = golder_hour;
        this.timezone = timezone;
        this.cityName = cityName;
    }

    public String getSunLatitude() {
        return sunLatitude;
    }

    public String getSunLongitude() {
        return sunLongitude;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getSolar_noon() {
        return solar_noon;
    }

    public String getGolder_hour() {
        return golder_hour;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getCityName() {
        return cityName;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public void setSolar_noon(String solar_noon) {
        this.solar_noon = solar_noon;
    }

    public void setGolder_hour(String golder_hour) {
        this.golder_hour = golder_hour;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
