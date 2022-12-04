import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.Array;

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

    public static void main(String[] args){
        testOut();
    }

}
