import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;





public class DB {
    private HashMap<String,ValueOBJ> db;
//    private ConcurrentHashMap<String , String> concurrentDb;

    public DB(){
        this.db = new HashMap<String,ValueOBJ>();
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
}
