import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class DB {
    private HashMap<String,ValueOBJ> db;
//    private ConcurrentHashMap<String , String> concurrentDb;

    public DB(){
        this.db = new HashMap<String,ValueOBJ>();
    }

    public void set(String key , String value){
        set(key,value,Long.MAX_VALUE);
    }

    public void set(String key , String value , Long expiryTime){

        db.put(key,new ValueOBJ(value,System.currentTimeMillis()+expiryTime));
    }

    public String get(String key){
        if(db.get(key) != null){
            ValueOBJ obj = db.get(key);
            Long expiryTime = obj.getExpiryTime();
            if(System.currentTimeMillis() > expiryTime){
                db.remove(key);
                return "null";
            }else{
                return obj.getValue();
            }
        }else{
            return "null";
        }
    }
    public String[] getKeys(){
        String[] keys = this.db.keySet().toArray(new String[0]);

        return keys;
    }
}
