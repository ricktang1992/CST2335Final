package algonquin.cst2335.cst2355final;
import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {Recipe.class}, version=1)
public abstract class RecipeDatabase  extends RoomDatabase {
    public abstract RecipeDAO cmDAO();
}

