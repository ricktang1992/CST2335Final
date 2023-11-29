package algonquin.cst2335.cst2355final.yuxing;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
//test
import java.util.List;

import algonquin.cst2335.cst2355final.yuxing.SearchTerm;

@Dao
public interface SearchTermDAO {
    @Insert
    public long insertSearchTerm(SearchTerm searchTerm);
    @Delete
    public void deleteMessage(SearchTerm searchTerm);
    @Query("SELECT * FROM search_terms")
    public List<SearchTerm> getAllSearchTerms();

}