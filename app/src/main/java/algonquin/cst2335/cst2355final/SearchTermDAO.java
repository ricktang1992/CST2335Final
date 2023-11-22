package algonquin.cst2335.cst2355final;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
//test
import java.util.List;
@Dao
public interface SearchTermDAO {
    @Insert
    public long insertSearchTerm(SearchTerm searchTerm);
    @Delete
    public int deleteMessage(SearchTerm searchTerm);
    @Query("SELECT * FROM search_terms")
    public List<SearchTerm> getAllSearchTerms();
}