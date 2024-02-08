import java.util.HashMap;

public class DB {
    private HashMap<String,String> db;

    public DB(){
        this.db = new HashMap<String,String>();
    }

    public void set(String key , String value){
        db.put(key,value);
    }

    public String get(String key){
        return db.get(key);
    }
}
