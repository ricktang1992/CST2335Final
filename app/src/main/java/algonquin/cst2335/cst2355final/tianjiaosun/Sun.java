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


    public Sun(){}

    // constructor
    public Sun(String sunLatitude, String sunLongitude, String sunrise, String sunset, String solar_noon, String golder_hour, String timezone, String cityName) {
        this.sunLatitude = sunLatitude;
        this.sunLongitude = sunLongitude;
        this.sunrise = sunrise;
        this.sunset = sunset;
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

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

}
