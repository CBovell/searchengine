import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Search {

    public ArrayList<String> phrase;
    public ArrayList<Double> queryVector;
    public ArrayList<String> basisVector;
    public HashMap<String, Double> queryMap;
    public HashMap<String, Double> idfData;
    public ArrayList<pageFreqData> csList;
    public boolean boost;



    public Search(String input, boolean boost){
        init(input,boost);
    }

    public void init(String input, boolean boost){
        this.phrase=new ArrayList<>();
        String[] phr = input.split(" ");
        for(int i=0;i<phr.length;i++){
            this.phrase.add(phr[i]);
        }
        this.queryVector = new ArrayList<>();
        this.basisVector = new ArrayList<>();
        this.queryMap = new HashMap<>();
        this.csList = new ArrayList<>();
        this.boost = boost;


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
        for(int i =0;i<this.phrase.size();i++){
            if(idfData.containsKey(this.phrase.get(i) + "IDF") && (idfData.get(this.phrase.get(i)) != 0.0)){
                if(this.queryMap.containsKey(this.phrase.get(i)) ==false){
                    this.queryMap.put(this.phrase.get(i), 0.0);
                        this.basisVector.add(this.phrase.get(i));
                }
                this.queryMap.put(this.phrase.get(i), this.queryMap.get(this.phrase.get(i)+1.0));
            }
        }
    }

    public void populateQueryVector(){
        for (Map.Entry<String, Double> entry : this.queryMap.entrySet()) {
            this.queryVector.add(entry.getValue()/this.phrase.size());
        }

        for(int i =0;i<this.basisVector.size();i++){
            this.queryVector.set(i, ((Math.log(this.queryVector.get(i))/Math.log(2))*idfData.get(basisVector.get(i) + "IDF")));
        }
    }

    public void compareScore(pageFreqData freqData){
        if(this.csList.size() == 0){
            csList.add(0, freqData);
        }
        else if (this.csList.size()<10) {
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
            return(numerator/((Math.sqrt(qEuclidNorm)*Math.sqrt(pEuclidNorm))*fetchPageRankData(pageVector.title)));
        }
        return (numerator/(Math.sqrt(qEuclidNorm)*(Math.sqrt(pEuclidNorm))));
    }


    public void calculateScore(){

    }
}



