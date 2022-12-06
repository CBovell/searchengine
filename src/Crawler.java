import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.*;


public class Crawler {

    public HashMap<String, Integer> wordDoc;
    public int numDocs;
    public String seed;


    public Crawler(String seed){
        this.wordDoc = new HashMap<String, Integer>();
        this.numDocs = 0;
        this.seed = seed;

    }

    public static void testOut(){
        try{
            PrintWriter out  = new PrintWriter(new FileWriter("test" + File.separator + "beta.txt"));
            File file = new File("test");
            String[] files = file.list();
            out.println(56);
            out.println("hello");

            for(int i = 0;i<files.length;i++){
                System.out.println(files[i]);
            }
            out.println(56);
            out.println("hello");
            out.close();

        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static  void testIn(){

    }

    public HashMap<String, Integer> getWords(String content){
        HashMap<String, Integer> toReturn = new HashMap<>();
        String words = (content.substring(content.indexOf("<p>")+4,content.indexOf("</p>")-1));
        String[] wordsList = words.split("\n");
        ArrayList<String> wordsArray = new ArrayList<String>();

        for(int i=0; i < wordsList.length;i++){
            wordsArray.add(wordsList[i]);
            if(toReturn.containsKey(wordsList[i]) == false){
                if(this.wordDoc.containsKey(wordsList[i])){
                    toReturn.put(wordsList[i], 1);
                    this.wordDoc.put(wordsList[i], this.wordDoc.get(wordsList[i])+1);
                }
                else{
                    toReturn.put(wordsList[i], 1);
                    this.wordDoc.put(wordsList[i], 1);
                }
            }
            else{
                toReturn.put(wordsList[i], toReturn.get(wordsList[i])+1);
            }
        }
        this.numDocs+=1;

        return toReturn;
    }

    public String getTitle(String content){
        return (content.substring(content.indexOf("<title>")+7,content.indexOf("</title>")));
    }

    public ArrayList<String> getOutgoingLinks(String content, String URL){
        String prefix = URL.substring(0, URL.lastIndexOf("/")+1);
        String words = (content.substring(content.indexOf("</p>")+5,content.indexOf("</body>")-1));
        ArrayList<String> toReturn = new ArrayList<String>();
        String[] links = words.split("\n");
        for(int i=0; i<links.length;i++){
            String link = links[i];
            link = link.substring(9, link.indexOf("</a>")-5);


            if(link.charAt(0) == '.'){
                link=link.substring(2, link.length());
                toReturn.add(prefix + link);
            }
            else{
                toReturn.add(link);
            }
        }
        return toReturn;
    }


    public void proccessData(String URL){

    }

    public void crawl(){
        Queue<String> q = new LinkedList<>();
        q.add(this.seed);

        while(q.isEmpty() == false){
            try {
                String content = WebRequester.readURL(q.peek());
                ArrayList<String> links = (this.getOutgoingLinks(content, q.peek()));
                for(int i=0;i< links.size();i++){
                    q.add(links.get(i));
                }


            }catch(MalformedURLException e){
                e.printStackTrace();
                String toReAdd = q.remove();
                q.add(toReAdd);
            }catch(IOException e){
                e.printStackTrace();
                String toReAdd = q.remove();
                q.add(toReAdd);
            }


        }


    }

}
