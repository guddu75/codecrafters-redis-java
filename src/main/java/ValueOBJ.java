public class ValueOBJ {
    private String value;
    private Long expiryTime;

    public ValueOBJ(String v , Long e)
    {
        this.value = v;
        this.expiryTime = e;
    }

    public Long getExpiryTime(){
        return this.expiryTime;
    }

    public String getValue(){
        return this.value;
    }
}
