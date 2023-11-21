package algonquin.cst2335.cst2355final;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
//test
import java.util.List;
@Dao
public interface RecipeDAO {
    @Insert
    long insertRecipe(Recipe m);
    @Query("Select * from Recipe")
    List<Recipe> getAllRecipe();
    @Delete
    void deleteRecipe(Recipe m);
}