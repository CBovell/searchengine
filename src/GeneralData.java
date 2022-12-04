import java.io.Serializable;

public abstract class GeneralData implements Serializable {

    String title;
    String url;

    public GeneralData(){
        this.title="";
        this.url = "";
    }

    public GeneralData(String title, String url){
        this.title=title;
        this.url=url;

    }
}
