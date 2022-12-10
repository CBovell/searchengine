import java.util.ArrayList;

public class pageFreqData {
    public String url;
    public String title;
    public ArrayList<Double> scoreArr;
    public double score;

    public pageFreqData(String url, String title){
        this.url = url;
        this.title= title;
        this.scoreArr = new ArrayList<>();
        this.score = 0.0;
    }

    public String toString(){
        return "[URL: " + this.url + ", Title: " + this.title + ", Score: "+this.score+"]";
    }

    public void printArr(){

    }

}
