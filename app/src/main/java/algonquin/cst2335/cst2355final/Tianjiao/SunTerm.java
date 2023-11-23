package algonquin.cst2335.cst2355final.Tianjiao;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "sun_terms")
public class SunTerm {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    long id;
    @ColumnInfo(name="term")
    protected String term;

    @ColumnInfo(name="timeSent")
    protected String timeSent;

    public SunTerm(){}

    public SunTerm(String term, String timeSent) {
        this.term = term;
        this.timeSent = timeSent;
    }


    public String getTerm() {
        return term;
    }
    public String getTimeSent() {
        return timeSent;
    }
}

