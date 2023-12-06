package algonquin.cst2335.cst2355final.tianjiaosun;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a sun object, including information about its latitude, longitude, sunrise, and sunset times.
 *
 * @author Tianjiao Feng
 */
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

    /**
     * Default constructor for the Sun class.
     */
    public Sun(){}

    /**
     * Constructs a Sun object with specified latitude, longitude, sunrise, and sunset times.
     *
     * @param sunLatitude  Latitude of the earth.
     * @param sunLongitude Longitude of the earth.
     * @param sunrise      Time of sunrise.
     * @param sunset       Time of sunset.
     */
    public Sun(String sunLatitude, String sunLongitude, String sunrise, String sunset) {
        this.sunLatitude = sunLatitude;
        this.sunLongitude = sunLongitude;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    /**
     * Gets the latitude of the sun.
     *
     * @return Latitude of the sun.
     */
    public String getSunLatitude() {
        return sunLatitude;
    }

    /**
     * Gets the longitude of the sun.
     *
     * @return Longitude of the sun.
     */
    public String getSunLongitude() {
        return sunLongitude;
    }

    /**
     * Gets the sunrise time.
     *
     * @return Sunrise time.
     */
    public String getSunrise() {
        return sunrise;
    }

    /**
     * Gets the sunset time.
     *
     * @return Sunset time.
     */
    public String getSunset() {
        return sunset;
    }

    /**
     * Sets the sunrise time.
     *
     * @param sunrise New sunrise time.
     */
    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    /**
     * Sets the sunset time.
     *
     * @param sunset New sunset time.
     */
    public void setSunset(String sunset) {
        this.sunset = sunset;
    }
}
