import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class PageRank {

    public HashMap<String, Integer> urlToIndex;
    public HashMap<Integer,String> indexToURL;
    public double[][] matrix;
    public double alpha;
    public int length;


    public PageRank(int numDocs){
        resetData(numDocs);
    }

    public void resetData(int numDocs){
        this.urlToIndex=new HashMap<>();
        this.indexToURL = new HashMap<>();
        this.matrix= new double[numDocs][numDocs];
        this.alpha=.1;
        this.length=numDocs;
    }

    public double[][] mult_scalar(double[][] matrix, double scale){
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
            for(int j=0; j<a.length;j++){
                double currSum=0.0;
                for(int k=0;k<a[0].length;k++){

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
                this.matrix[urlToIndex.get(data.url)][urlToIndex.get(outGoingLinks.get(x))] = 1.0;
                this.matrix[urlToIndex.get(outGoingLinks.get(x))][urlToIndex.get(data.url)] = 1.0;
            }
        }

    }


    public void randomProbability(){
        for(int x=0;x<this.matrix.length;x++){
            int numX=0;
            ArrayList<Integer> numIndex= new ArrayList<>();
            for(int y=0;y<this.matrix[x].length;y++){
                if (this.matrix[x][y] ==1.0){
                    numX+=1;
                    numIndex.add(y);
                }
            }
            if (numX == 0){
                for(int w = 0;w<this.matrix[x].length;w++){
                    this.matrix[x][w] = (double)1/this.length;
                }
            }
            else{
                for(int z =0; z<numIndex.size();z++){
                    this.matrix[x][numIndex.get(z)]=(double)1/numX;
                }
            }
        }
    }

    public void modAlpha(){
        this.matrix = mult_scalar(this.matrix, 1-this.alpha);

        for(int i=0;i< this.matrix.length;i++){
            for(int x=0; x<this.matrix[i].length;x++){
                this.matrix[i][x] += (double)this.alpha/this.length;
            }
        }
    }

    public double[][] addCurrVector(){
        double[][]toReturn = new double[1][this.length];
        toReturn[0][0] = 1.0;
        return toReturn;
    }

    public double[][] piMultiplication(){
        double[][] prevVector = new double[1][this.length];

        for(int i=0; i<prevVector[0].length;i++){
            prevVector[0][i] = 100;
        }

        double[][] currVector = addCurrVector();

        double distance =euclidean_dist(prevVector,currVector);
        int count =0;

        while (distance > .0001){
            prevVector =currVector;
            currVector=mult_matrix(currVector, this.matrix);
            count+=1;
            distance=euclidean_dist(prevVector,currVector);
        }

        return currVector;

    }

    public void printArray(double[][] arr){
        for(int i = 0; i<arr.length;i++){
            for(int x=0;x<arr[0].length;x++){
                System.out.println(arr[i][x]);
            }
            System.out.println("Break");
        }
    }

    public void saveData(double[][] values){

        for(int i =0; i<values[0].length;i++){

            try {
                String url = this.indexToURL.get(i);
                String prefix = url.substring(url.lastIndexOf("/")+1, url.length()-5);
                DataOutputStream out;
                out = new DataOutputStream(new FileOutputStream("files" + File.separator + "pageRank" + File.separator + prefix + ".dat"));
                System.out.println(values[0][i]);
                out.writeDouble(values[0][i]);
                out.close();
            } catch (FileNotFoundException e) {
                System.out.println("Error: Cannot open file for writing");
            } catch (IOException e) {
                System.out.println("Error: Cannot write to file");
            }

        }


    }

    public void runPageRank(){
        this.resetData(this.length);
        this.createMap();
        this.createMatrix();
        this.randomProbability();
        this.modAlpha();
        this.printArray(this.matrix);
        this.saveData(this.piMultiplication());
    }

}
