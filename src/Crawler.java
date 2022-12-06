import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;


public class Crawler {

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

    public static String[] getWords(String content){
        String words = (content.substring(content.indexOf("<p>")+4,content.indexOf("</p>")-1));
        return words.split("\n");
    }

    public static String getTitle(String content){
        return (content.substring(content.indexOf("<title>")+7,content.indexOf("</title>")));
    }

    public static ArrayList<String> getOutgoingLinks(String content, String URL){
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

    //public static String[] getOutgoingLinks(String content){
        //String words = (content.substring(content.indexOf("<p>")+4,content.indexOf("</p>")-1));
        //return new String["joe"];
    //}



    public static void main(String[] args){
        try {
            String content = WebRequester.readURL("http://people.scs.carleton.ca/~davidmckenney/tinyfruits/N-0.html");

            System.out.println(content);
            ArrayList<String> links = (Crawler.getOutgoingLinks(content, "http://people.scs.carleton.ca/~davidmckenney/tinyfruits/N-0.html"));
            for(int i=0;i< links.size();i++){
                System.out.println(links.get(i));
            }

        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }



    }

}
