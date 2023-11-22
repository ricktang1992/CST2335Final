package algonquin.cst2335.cst2355final.ziyao;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import algonquin.cst2335.cst2355final.ziyao.Recipe;
import algonquin.cst2335.cst2355final.ziyao.RecipeDAO;

//test
@Database(entities = {Recipe.class}, version=1)
public abstract class RecipeDatabase  extends RoomDatabase {
    public abstract RecipeDAO cmDAO();
}

