import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Search {

    public ArrayList<String> phrase;
    public ArrayList<Double> queryVector;
    public ArrayList<String> basisVector;
    public ArrayList<ArrayList<String>> queryMap;
    public HashMap<String, Double> idfData;
    public ArrayList<pageFreqData> csList;
    public boolean boost;
    public int maxReturn;



    public Search(String input, boolean boost, int X){
        init(input,boost, X);
    }

    public void init(String input, boolean boost, int X){
        this.phrase=new ArrayList<>();
        String[] phr = input.split(" ");
        for(int i=0;i<phr.length;i++){
            this.phrase.add(phr[i]);
        }
        this.queryVector = new ArrayList<>();
        this.basisVector = new ArrayList<>();
        this.queryMap = new ArrayList<>();
        this.csList = new ArrayList<>();
        this.boost = boost;
        this.maxReturn = X;


        try {
            IdfData data;
            ObjectInputStream in;



            in = new ObjectInputStream(new FileInputStream("files" + File.separator + "pageFreqFiles" + File.separator + "IDFData.txt"));
            data = (IdfData)in.readObject();
            this.idfData = data.data;

            in.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Object's class does not match");
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot open file for writing");
        } catch (IOException e) {
            System.out.println("Error: Cannot read from file");
        }

    }

    public void populateBasisVector(){
        HashMap<String, Integer> map = new HashMap<>();
        int count=0;
        for(int i =0;i<this.phrase.size();i++){
            if(idfData.containsKey(this.phrase.get(i)) && (idfData.get(this.phrase.get(i)) != 0.0)){
                if(map.containsKey(this.phrase.get(i)) == false){
                    map.put(this.phrase.get(i), count);
                    ArrayList toAdd = new ArrayList<>();
                    toAdd.add(this.phrase.get(i));
                    toAdd.add("1");
                    this.queryMap.add(toAdd);
                    this.basisVector.add(this.phrase.get(i));
                    count+=1;

                }
                else{
                    int index = map.get(this.phrase.get(i));
                    ArrayList<String> currList = this.queryMap.get(index);
                    int currCount=Integer.parseInt(currList.get(1));
                    currCount+=1;
                    currList.set(1, Integer.toString(currCount));
                    this.queryMap.set(index, currList);
                }

            }
        }

    }

    public void populateQueryVector(){
        for (int i=0; i<this.queryMap.size();i++) {

            this.queryVector.add((double)Integer.parseInt(this.queryMap.get(i).get(1))/this.phrase.size());
        }
        for(int i =0;i<this.basisVector.size();i++){
            this.queryVector.set(i,((Math.log(1.0 + this.queryVector.get(i))/Math.log(2))*idfData.get(basisVector.get(i))));
        }
    }

    public void compareScore(pageFreqData freqData){
        if(this.csList.size() == 0){
            csList.add(0, freqData);
        }
        else if (this.csList.size()<this.maxReturn) {
            csList = insert_List(freqData);
        }
        else if (freqData.score>this.csList.get(this.csList.size()-1).score) {
            this.csList.remove(this.csList.size()-1);
            csList = insert_List(freqData);
        }
    }


    public ArrayList<pageFreqData> insert_List(pageFreqData csInfo){
        ArrayList<pageFreqData>  list = this.csList;

        for(int i=0;i<list.size();i++){
            if(csInfo.score>list.get(i).score){
                list.add(i, csInfo);
                return list;
            }
        }
        list.add(csInfo);
        return list;

    }

    public double fetchPageRankData(String title){
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

    public double calc_Cs(ArrayList<Double> queryVector, pageFreqData pageVector){
        double numerator = 0.0;
        double qEuclidNorm = 0.0;
        double pEuclidNorm = 0.0;
        for(int i=0; i<queryVector.size();i++) {

            numerator += (queryVector.get(i) * pageVector.scoreArr.get(i));
            qEuclidNorm += Math.pow(queryVector.get(i), 2.0);
            pEuclidNorm += Math.pow(pageVector.scoreArr.get(i), 2.0);
        }

        if(qEuclidNorm ==0.0 || qEuclidNorm ==0.0){
            return 0.0;
        }
        if(this.boost){
            double toReturn = (((numerator/((Math.sqrt(qEuclidNorm)*Math.sqrt(pEuclidNorm)))*fetchPageRankData(pageVector.title))));
            if(Double.isNaN(toReturn)){
                return 0.0;
            }
            return toReturn;
        }
        double toReturn = (numerator/(Math.sqrt(qEuclidNorm)*(Math.sqrt(pEuclidNorm))));
        if(Double.isNaN(toReturn)){
            return 0.0;
        }
        return toReturn;
    }


    public void calculateScore(){
        File path = new File("files" + File.separator + "pageFreqFiles");
        String[] list = path.list();
        for(int i=0; i<list.length;i++){
            if(list[i].equals("IDFData.txt")){
                continue;
            }
            try {
                PageFreqFiles currFile;
                ObjectInputStream in;

                in = new ObjectInputStream(new FileInputStream("files" + File.separator + "pageFreqFiles" + File.separator + list[i]));

                currFile = (PageFreqFiles)in.readObject();


                in.close();

                pageFreqData currData = new pageFreqData(currFile.url, currFile.title);


                for(int x=0; x<basisVector.size();x++){

                    if(currFile.tfidfData.containsKey(basisVector.get(x))){
                        currData.scoreArr.add(currFile.tfidfData.get(basisVector.get(x)));
                    }
                    else{
                        currData.scoreArr.add(0.0);
                    }
                }
                currData.score = calc_Cs(this.queryVector, currData);

                compareScore(currData);


            } catch (ClassNotFoundException e) {
                System.out.println("Error: Object's class does not match");
            } catch (FileNotFoundException e) {
                System.out.println("Error: Cannot open file for writing");
            } catch (IOException e) {
                System.out.println("Error: Cannot read from file");
            }

        }
    }

    public ArrayList<String> finalString(){
        ArrayList<String> toReturn = new ArrayList<>();
        for(int i=0; i<this.csList.size();i++){
            toReturn.add(this.csList.get(i).toString());
        }
        return toReturn;
    }

    public ArrayList<SearchResult> getInterfaceList(){
        ArrayList<SearchResult> toReturn = new ArrayList<>();
        for(int i=0; i<this.csList.size();i++){
            SearchResult currObj = this.csList.get(i);
            toReturn.add(currObj);
        }
        return toReturn;
    }

    public ArrayList<SearchResult> search(){
        this.populateBasisVector();
        this.populateQueryVector();
        this.calculateScore();
        return getInterfaceList();
    }
}



