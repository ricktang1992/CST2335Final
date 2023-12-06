package algonquin.cst2335.cst2355final.rita;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
/**
 * DeezerSong class represents a song retrieved from the Deezer API.
 * Each instance of DeezerSong contains information such as title, album name, duration, and album cover.
 * This class is used in conjunction with Room database to store and retrieve Deezer songs.
 */
@Entity
public class DeezerSong {
    /**
     * The unique identifier for the DeezerSong in the database.
     */
    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    public long id;
    /**
     * The title of the Deezer song.
     */
    @ColumnInfo(name="Title")
    protected String title;
    /**
     * The name of the album to which the Deezer song belongs.
     */
    @ColumnInfo(name="AlbumName")
    protected String name;
    /**
     * The duration of the Deezer song in seconds.
     */
    @ColumnInfo(name="Duration")
    protected int duration;
    /**
     * The URL of the album cover for the Deezer song.
     */
    @ColumnInfo(name="AlbumCover")
    protected String cover;
    /**
     * Default constructor with no arguments.
     * Used by Room database when creating instances from database records.
     */
    @Ignore
    public DeezerSong(){};
    /**
     * Constructor for DeezerSong.
     * @param title The title of the Deezer song.
     * @param name The name of the album to which the Deezer song belongs.
     * @param duration The duration of the Deezer song in seconds.
     * @param cover The URL of the album cover for the Deezer song.
     */
    public DeezerSong(String title,String name,int duration, String cover)
    {
        this.title = title;
        this.name = name;
        this.duration = duration;
        this.cover = cover;
    }
    /**
     * Getter method for retrieving the title of the Deezer song.
     * @return The title of the Deezer song.
     */
    public String getTitle() {
        return title;
    }
    /**
     * Getter method for retrieving the name of the album to which the Deezer song belongs.
     * @return The name of the album.
     */
    public String getName(){
        return name;
    }
    /**
     * Getter method for retrieving the duration of the Deezer song in seconds.
     * @return The duration of the Deezer song in seconds.
     */
    public int getDuration(){
        return duration;
    }
    /**
     * Getter method for retrieving the URL of the album cover for the Deezer song.
     * @return The URL of the album cover.
     */
    public String getCover(){
        return cover;
    }
}