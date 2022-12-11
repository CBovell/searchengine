import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Tester implements ProjectTester {

    public void initialize(){
        new File("files").delete();
        new File("files"+File.separator+"pageFiles").mkdirs();
        new File("files"+File.separator+"pageFreqFiles").mkdirs();
        new File("files"+File.separator+"pageRank").mkdirs();
        new File("files"+File.separator+"incomingLinks").mkdirs();
    }

    public void crawl(String seed){
        Crawler crawler = new Crawler(seed);
        crawler.crawl();
        PageRank pageRank = new PageRank(crawler.numDocs);
        pageRank.runPageRank();
    }

    public List<String> getOutgoingLinks(String url) {
        String prefix = url.substring(url.lastIndexOf("/")+1, url.length()-5);
        try {
            PageFiles file;
            ObjectInputStream in;
            in = new ObjectInputStream(new FileInputStream("resources" + File.separator + "pageFiles" + File.separator+ prefix+".txt"));
            file = (PageFiles) in.readObject();
            in.close();
            return file.outgoingLinks;
        } catch (ClassNotFoundException e) {
            return new ArrayList<String>();
        } catch (FileNotFoundException e) {
            return new ArrayList<String>();
        } catch (IOException e) {
            return new ArrayList<String>();
        }
    }

    public double getPageRank(String url){
        String title = url.substring(url.lastIndexOf("/")+1, url.length()-5);
        try {
            DataInputStream in;
            in = new DataInputStream(new FileInputStream("files" + File.separator + "pageRank" + File.separator+ title + ".dat"));
            double data = in.readDouble();
            in.close();
            return data;
        } catch (FileNotFoundException e) {
            return 404.0;
        } catch (IOException e) {
            return 404.0;
        }
    }

    public double getIDF(String word){
        try {
            HashMap<String, Double> idfData;
            IdfData data;
            ObjectInputStream in;


            in = new ObjectInputStream(new FileInputStream("files" + File.separator + "pageFreqFiles" + File.separator + "IDFData.txt"));
            data = (IdfData)in.readObject();
            idfData = data.data;
            in.close();

            if(idfData.containsKey(word)){
                return idfData.get(word);
            }
            return 0.0;
        } catch (ClassNotFoundException e) {
            return 404.0;
        } catch (FileNotFoundException e) {
            return 404.0;
        } catch (IOException e) {
            return 404.0;
        }
    }

    public double getFreq(String url, String word, int x){
        try {
            String prefix = url.substring(url.lastIndexOf("/")+1, url.length()-5);
            PageFreqFiles currFile;
            ObjectInputStream in;

            in = new ObjectInputStream(new FileInputStream("files" + File.separator + "pageFreqFiles" + File.separator + prefix + ".txt"));
            currFile = (PageFreqFiles)in.readObject();
            in.close();

            if(x == 0){
                if(currFile.tfData.containsKey(word)){
                    return currFile.tfData.get(word);
                }
                return 0.0;
            }
            if(currFile.tfidfData.containsKey(word)){
                return currFile.tfidfData.get(word);
            }
            return 0.0;

        } catch (ClassNotFoundException e) {
            return 404.0;
        } catch (FileNotFoundException e) {
            return 404.0;
        } catch (IOException e) {
            return 404.0;
        }

    }

    public double getTF(String url, String word) {
        return getFreq(url, word, 0);
    }

    public double getTFIDF(String url, String word) {
        return getFreq(url, word, 1);
    }


    public List<String> getIncomingLinks(String url) {
        ArrayList<String> toReturn = new ArrayList<>();
        url = url.substring(url.lastIndexOf("/")+1, url.length()-5);
        try {
            File myObj = new File(("files" + File.separator + "incomingLinks" + File.separator + url + ".txt"));
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                toReturn.add(myReader.nextLine());
            }
            myReader.close();
            return toReturn;
        } catch (FileNotFoundException e) {
            toReturn.add(e.toString());
            return toReturn;
        }
    }

    public List<SearchResult> search(String query, boolean boost, int X) {
        Search search = new Search(query, boost, X);
        return search.search();
    }
}
