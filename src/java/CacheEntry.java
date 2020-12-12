


public class CacheEntry<T> {

    private String eTag;
    private T data;

    public CacheEntry(String eTag, T data){
        this.data= data;
        this.eTag=eTag;
    }
    public CacheEntry(){};

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public String geteTag() {
        return eTag;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
