import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class WebRequester {
    public static String readURL(String url) throws MalformedURLException, IOException {
        URL page = new URL(url);
        StringBuilder response = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(page.openStream()));
        String currentLine = reader.readLine();
        while(currentLine != null){
            response.append(currentLine + "\n");
            currentLine = reader.readLine();
        }
        return response.toString();
    }

    /*
    Below is an example of how you can read a URL with this class.
    If exceptions are thrown during your crawl, you should handle them gracefully.
    For example, add the URL to the queue again and retry later.
     */
    public static void main(String[] args){
        try {
            System.out.println(WebRequester.readURL("http://people.scs.carleton.ca/~davidmckenney/fruits/N-0.html"));
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}