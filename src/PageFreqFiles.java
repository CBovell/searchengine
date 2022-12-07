import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PageFreqFiles extends GeneralData implements Serializable {
    public HashMap<String, Double> tfData;
    public HashMap<String, Double> tfidfData;

    public PageFreqFiles(String title, String url, HashMap<String, Double> tfData, HashMap<String, Double> tfidfData){
        super(title, url);
        this.tfData = tfData;
        this.tfidfData=tfidfData;
    }
}
