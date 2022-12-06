import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PageFiles extends GeneralData implements Serializable {

    public HashMap<String, Integer> words;
    public int totalWords;
    public ArrayList<String> outgoingLinks;


    public PageFiles(){
        super();
        this.words = new HashMap<>();
        this.totalWords=0;
        this.outgoingLinks = new ArrayList<String>();

    }

    public PageFiles(String title, String url, HashMap<String,Integer> words, int totalWords, ArrayList<String> outgoingLinks){
        super(title, url);
        this.words = words;
        this.totalWords=totalWords;
        this.outgoingLinks=outgoingLinks;

    }

    public String toString(){
        return this.title +" "+ this.outgoingLinks.toString();
    }

}
