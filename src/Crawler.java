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

    public void clearAndCreate(){
        new File("files"+File.separator+"pageFiles").delete();
        new File("files"+File.separator+"pageFreqFiles").delete();
        new File("files"+File.separator+"pageFiles").mkdirs();
        new File("files"+File.separator+"pageFreqFiles").mkdirs();
    }

    public GetWords getWords(String content){
        HashMap<String, Integer> wordsMap = new HashMap<>();
        String words = (content.substring(content.indexOf("<p>")+4,content.indexOf("</p>")-1));
        String[] wordsList = words.split("\n");
        ArrayList<String> wordsArray = new ArrayList<String>();

        for(int i=0; i < wordsList.length;i++){
            wordsArray.add(wordsList[i]);
            if(wordsMap.containsKey(wordsList[i]) == false){
                if(this.wordDoc.containsKey(wordsList[i])){
                    wordsMap.put(wordsList[i], 1);
                    this.wordDoc.put(wordsList[i], this.wordDoc.get(wordsList[i])+1);
                }
                else{
                    wordsMap.put(wordsList[i], 1);
                    this.wordDoc.put(wordsList[i], 1);
                }
            }
            else{
                wordsMap.put(wordsList[i], wordsMap.get(wordsList[i])+1);
            }
        }
        this.numDocs+=1;

        return new GetWords(wordsList.length, wordsMap);
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


    public void savePageData(String content, String URL){
        String title_ = getTitle(content);
        ArrayList<String> links = getOutgoingLinks(content, URL);
        GetWords wordsData = getWords(content);
        PageFiles data = new PageFiles(title_, URL, wordsData.wordsMap, wordsData.wordCount, links);
        try {
            ObjectOutputStream out;
            out = new ObjectOutputStream(new FileOutputStream("files" + File.separator + "pageFiles" + File.separator + title_ + ".txt"));
            out.writeObject(data);
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot open file for writing");
        } catch (IOException e) {
            System.out.println("Error: Cannot write to file");
        }

    }

    public void crawl(){
        this.clearAndCreate();
        HashSet<String> visited = new HashSet<>();
        Queue<String> q = new LinkedList<>();
        q.add(this.seed);
        visited.add(this.seed);
        int failCount=0;

        while(q.isEmpty() == false){
            try {
                String content = WebRequester.readURL(q.peek());
                savePageData(content, q.peek());
                ArrayList<String> links = (this.getOutgoingLinks(content, q.peek()));
                for(int i=0;i< links.size();i++){
                    if(visited.contains(links.get(i)) == false){
                        q.add(links.get(i));
                        visited.add(links.get(i));
                    }
                }
                q.remove();


            }catch(MalformedURLException e){
                if(failCount>10){
                    break;
                }
                failCount++;
                e.printStackTrace();
                String toReAdd = q.remove();
                q.add(toReAdd);
            }catch(IOException e){
                if(failCount>10){
                    break;
                }
                failCount++;
                e.printStackTrace();
                String toReAdd = q.remove();
                q.add(toReAdd);
            }


        }

    }

    public static void main(String[] args){
        Crawler crawl_ = new Crawler("https://people.scs.carleton.ca/~davidmckenney/tinyfruits/N-0.html");
        crawl_.crawl();
    }

}
