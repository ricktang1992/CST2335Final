package algonquin.cst2335.cst2355final.yuxing;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "search_terms")
public class SearchTerm {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    long id;
    @ColumnInfo(name="term")
    protected String term;

    @ColumnInfo(name="definition")
    protected String definition;

    @ColumnInfo(name="timeSent")
    protected String timeSent;


    public  SearchTerm(){}

    public SearchTerm(String term, String timeSent, String definition) {
        this.term = term;
        this.timeSent = timeSent;
        this.definition = definition;

    }


    public String getTerm() {
        return term;
    }
    public String getDefinition() {
        return definition;
    }
    public String getTimeSent() {
        return timeSent;
    }

}

