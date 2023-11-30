package algonquin.cst2335.cst2355final.rita;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class DeezerSong {
    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    public long id;
    @ColumnInfo(name="Title")
    protected String title;
    @ColumnInfo(name="AlbumName")
    protected String name;
    @ColumnInfo(name="Duration")
    protected int duration;
    @ColumnInfo(name="AlbumCover")
    protected String cover;

    @Ignore
    public DeezerSong(){};

    public DeezerSong(String title,String name,int duration, String cover)
    {
        this.title = title;
        this.name = name;
        this.duration = duration;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }
    public String getName(){
        return name;
    }
    public int getDuration(){
        return duration;
    }
    public String getCover(){
        return cover;
    }
}