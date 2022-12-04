import java.io.Serializable;
import java.util.HashMap;

public class IdfData implements Serializable {

    public HashMap<String, Double> data;


    public IdfData(HashMap<String, Double> data){
        this.data=data;
    }
}
