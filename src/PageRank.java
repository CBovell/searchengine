import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class PageRank {

    public HashMap<String, Integer> urlToIndex;
    public HashMap<Integer,String> indexToURL;
    public double[][] matrix;
    public float alpha;
    public int length;


    public PageRank(int numDocs){
        resetData(numDocs);
    }

    public void resetData(int numDocs){
        this.urlToIndex=new HashMap<>();
        this.indexToURL = new HashMap<>();
        this.matrix= new double[numDocs][numDocs];
        this.alpha=.1f;
        this.length=numDocs;
    }

    public double[][] mult_scalar(double[][] matrix, int scale){
        double[][] resMatrix = matrix;

        for(int i=0; i<resMatrix.length; i++){
            for(int x=0; x<resMatrix[i].length;x++){
                resMatrix[i][x] *=scale;
            }
        }

        return resMatrix;
    }

    public double[][] mult_matrix(double[][] a, double[][] b){
        double[][] resMatrix = new double[a.length][b[0].length];

        for(int i=0; i<b[0].length;i++){
            for(int j=0; j<a.length;i++){
                double currSum=0.0;
                for(int k=0;k<a[0].length;i++){
                    currSum+=(double)a[j][k]*b[k][i];
                }
                resMatrix[j][i] = currSum;
            }
        }
        return resMatrix;
    }


    public double euclidean_dist(double[][]a, double[][]b){
        double sum = 0.0;
        for(int i=0; i<a[0].length;i++){
            sum+=Math.pow(a[0][i]-b[0][i],2);
            return Math.sqrt(sum);
        }
        return Math.sqrt(sum);
    }

    public PageFiles fetchData(String title){
        try {
            PageFiles pageFile;
            ObjectInputStream in;
            in = new ObjectInputStream(new FileInputStream("files" + File.separator + "pageFiles"+ File.separator+ title));
            pageFile = (PageFiles) in.readObject();
            in.close();
            return pageFile;

        } catch (ClassNotFoundException e) {
            System.out.println("Error: Object's class does not match");
            return null;
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot open file for writing");
            return null;
        } catch (IOException e) {
            System.out.println("Error: Cannot read from file");
            return null;
        }
    }

    public void createMap(){
        File dir = new File("files" + File.separator + "pageFiles");
        String[] files = dir.list();

        for(int i =0;i< files.length;i++){
            String currURL = fetchData(files[i]).url;
            this.urlToIndex.put(currURL, i);
            this.indexToURL.put(i, currURL);
        }
    }

    public void createMatrix(){
        File dir = new File("files" + File.separator + "pageFiles");
        String[] files = dir.list();
        for(int i=0; i< files.length;i++){
            PageFiles data = fetchData(files[i]);
            ArrayList<String> outGoingLinks = data.outgoingLinks;
            for(int x=0; x< outGoingLinks.size();x++){
                this.matrix[urlToIndex.get(data.title)][urlToIndex.get(outGoingLinks.get(x))] = 1;
                this.matrix[urlToIndex.get(outGoingLinks.get(x))][urlToIndex.get(data.title)] = 1;
            }
        }

    }

}
