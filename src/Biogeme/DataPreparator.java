/**
 * 
 */
package Biogeme;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.logging.Level;
import java.util.logging.Logger;
//import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

import Utils.Dictionnary;
import Utils.RandomNumberGen;
import Utils.Reader;
import Utils.Writer;

/**
 * @author Antoine
 *Prepare the data by adding a column with the choice identifier.
 */
public class DataPreparator {

	Reader myInputDataReader = new Reader();
	Writer myOutputFileWritter = new Writer();
	HashMap<String, ArrayList<Object>> myData = new HashMap<String, ArrayList<Object>>();
	protected RandomNumberGen randGen = new RandomNumberGen();
	
	public DataPreparator(String path){
		myInputDataReader.OpenFile(path);
		String[] temp;
		temp=path.split(".csv");
		myOutputFileWritter.OpenFile(temp[0] + "_prepared.csv");	
	}
	
	public void processData(int choiceSetSize) throws IOException{
		storeData();
		
		System.out.println("-- min and max distance for a trip processed");
		processAlternatives(choiceSetSize);// maybe some work should be done to avoid to get alternatives irrelevant (like not taking PT, or for a different kind of occupation)
		System.out.println("--alternative processed");
		
		
		selectAndPrint(choiceSetSize);
	}
	
	
	
	private double hourStrToDouble(String time) {
		// TODO Auto-generated method stub
		//in OD survey of Montr�al, TIME is formated 2800
		time.trim();
		double hour = 0;
		if(time.length()==4){
			hour = 60 * Double.parseDouble(time.substring(0, 1)) + Double.parseDouble(time.substring(2,3));
		}
		else if(time.length() == 3){
			hour = 60 * Double.parseDouble(Character.toString(time.charAt(0))) + Double.parseDouble(time.substring(1,2));
		}
		return hour; //here, hour is in minutes
	}
	

	
	public void processAlternatives(int n){
		for(int i = 0; i < n; i++){
			myData.put(Dictionnary.firstDep+Integer.toString(i), new ArrayList<Object>());
			myData.put(Dictionnary.lastDep+Integer.toString(i), new ArrayList<Object>());
			myData.put(Dictionnary.fidelPtRange+Integer.toString(i), new ArrayList<Object>());
			myData.put(Dictionnary.nAct+Integer.toString(i), new ArrayList<Object>());
		}
		
		for(int i = 0; i < myData.get(Dictionnary.id).size();i++){
			if(myData.get(Dictionnary.pDebut).get(i).equals("T") ){
				String hhId = (String)myData.get(Dictionnary.mNumero).get(i);
				String persId = (String)myData.get(Dictionnary.pRang).get(i);
				//String pStatut = (String)myData.get("P_STATUT").get(i);
				Set<Integer> idClosests = findClosestAlternatives(
						n, 
						Integer.parseInt((String)myData.get("M_DOMXCOOR").get(i)),
						Integer.parseInt((String)myData.get("M_DOMYCOOR").get(i)), 
						hhId,
						persId);
				addAlternatives(n, idClosests);	
			}
			else{
				addAlternatives(n);
			}
			if(i%1000==0){
				System.out.println(i);
			}
		}
	}

	private void addAlternatives(int n) {
		// TODO Auto-generated method stub
		for(int i = 0; i < n; i++){
			myData.get(Dictionnary.firstDep+Integer.toString(i)).add("0");
			myData.get(Dictionnary.lastDep+Integer.toString(i)).add("0");
			myData.get(Dictionnary.fidelPtRange+Integer.toString(i)).add("0");
			myData.get(Dictionnary.nAct+Integer.toString(i)).add("0");
		}
	}

	private void addAlternatives(int n, Set<Integer> idClosests) {
		// TODO Auto-generated method stub
		Iterator<Integer> it = idClosests.iterator();
		for(int i = 0; i < n; i++){
			int currIpere = it.next();
			int j = 0;
			while(currIpere != Integer.parseInt((String)myData.get(Dictionnary.id).get(j))){//IPERE is a unique identifier for each record in the travel survey of Montreal
				j++;
			}
			String altFirstDep = (String)myData.get(Dictionnary.firstDep).get(j);
			String altLastDep = (String)myData.get(Dictionnary.lastDep).get(j);
			String fidelPT = (String)myData.get(Dictionnary.fidelPtRange).get(j);
			Integer nActivities = (Integer)myData.get(Dictionnary.nAct).get(j);
			
			
			myData.get(Dictionnary.firstDep+Integer.toString(i)).add(altFirstDep);
			myData.get(Dictionnary.lastDep+Integer.toString(i)).add(altLastDep);
			myData.get(Dictionnary.fidelPtRange+Integer.toString(i)).add(fidelPT);
			myData.get(Dictionnary.nAct+Integer.toString(i)).add(nActivities);
		}
	}

	private Set<Integer> findClosestAlternatives(int n, int x, int y, String hhId, String persId) {
		// TODO Auto-generated method stub
		HashMap<Integer,Integer> closeAlternatives = new HashMap<Integer, Integer>(); // structure : the key is formed by IPERE (from OD survey), the value is the distance
		
		for(int j = 0; j < myData.get(Dictionnary.id).size(); j++){
			if(
					myData.get(Dictionnary.pDebut).get(j).equals("T") && 
					!(((String)myData.get(Dictionnary.mNumero).get(j)).equals(hhId) && 
					((String)myData.get(Dictionnary.pRang).get(j)).equals(persId))
			){
				int curX = Integer.parseInt((String)myData.get("M_DOMXCOOR").get(j));
				int curY = Integer.parseInt((String)myData.get("M_DOMYCOOR").get(j));
				int curIpere = Integer.parseInt((String)myData.get(Dictionnary.id).get(j));
				int curDist = (int)(Math.pow((x-curX), 2) + Math.pow((y-curY), 2)); 
				
				if(closeAlternatives.size() < n){
					closeAlternatives.put(curIpere, curDist);
				}
				else{
					int[] removeId = getFarthestAltenative(closeAlternatives);
					if(curDist< removeId[1]){
						closeAlternatives.remove(removeId[0]);
						closeAlternatives.put(curIpere, curDist);
					}
				}				
			}
			else{	
			}	
		}
		return closeAlternatives.keySet();
	}
	
	private int[] getFarthestAltenative(HashMap<Integer, Integer> closeAlternatives) {
		// TODO Auto-generated method stub
		int curDist = 0;
		int curId = 0;
		for(java.util.Map.Entry<Integer, Integer> e:closeAlternatives.entrySet()){
			if(e.getValue() > curDist){
				curId = e.getKey();
				curDist = e.getValue();
			}
		}
		int answer[] = new int[2];
		answer[0] = curId;
		answer[1] = curDist;
		return answer;
	}

	
	public void storeData() throws IOException
    {
    	 ArrayList<ArrayList> data = new ArrayList<ArrayList>();
    	 data = getData();
    	 
    	 ArrayList<String> headers = data.get(0);
    	 for(int i =0; i < headers.size(); i++){
    		 myData.put(headers.get(i), new ArrayList());
    	 }

    	 for (int i=1; i<data.size()-1; i++)
    	 {
    			for (int j=0; j<data.get(i).size();j++)
    			{
    				myData.get(headers.get(j)).add(data.get(i).get(j));
    			}
    	 }
    	 System.out.println("--travel survey data was successfully stored");
    }
	
	public ArrayList<ArrayList> getData() throws IOException
    {
    	String line=null;
    	Scanner scanner = null;
    	ArrayList<ArrayList> data = new ArrayList<ArrayList>();

    		int i=0;
    		while((line=myInputDataReader.myFileReader.readLine())!= null)
    		{
    			data.add(new ArrayList());
    			scanner = new Scanner(line);
    			scanner.useDelimiter(",");

    				while (scanner.hasNext())
    				{
    					String dat = scanner.next();
    					data.get(i).add(dat);
    				}
    				i++;
    		}
    	return data;
    }
	
	public void printData() throws IOException {

		String headers= new String();
		for (String key : myData.keySet()) {
			headers+= key + Dictionnary.COLUMN_DELIMETER;
		}
		headers = headers.substring(0, headers.length()-1);
		myOutputFileWritter.WriteToFile(headers);
		
		for(int i=0; i < myData.get(Dictionnary.id).size(); i++){
			String line = new String();
			for(String key: myData.keySet()){
				line += myData.get(key).get(i) + Dictionnary.COLUMN_DELIMETER;
			}
			line = line.substring(0, line.length()-1);
			myOutputFileWritter.WriteToFile(line);
		}
		myOutputFileWritter.CloseFile();
	}
	
	public void printColumns(ArrayList<String> toPrint) throws IOException {

		String headers= new String();
		Iterator<String> it = toPrint.iterator();
		while(it.hasNext()){
			String head = it.next();
			headers+= head + Dictionnary.COLUMN_DELIMETER;
		}
		headers = headers.substring(0, headers.length()-1);
		myOutputFileWritter.WriteToFile(headers);
		
		for(int i=0; i < myData.get(Dictionnary.id).size()-1; i++){
			if(myData.get(Dictionnary.pDebut).get(i).equals("1")){
				String line = new String();
				Iterator<String> it2 = toPrint.iterator();
				while(it2.hasNext()){
					String head = it2.next();
					try{
						line += myData.get(head).get(i) + Dictionnary.COLUMN_DELIMETER;
					}
					catch(NullPointerException ex){
						System.out.println("null pointer exception : " + head);
					}
				}
				line = line.substring(0, line.length()-1);
				myOutputFileWritter.WriteToFile(line);
			}
			else{
			}
		}
		myOutputFileWritter.CloseFile();
	}
	
	public void selectAndPrint(int choiceSetSize){
		ArrayList<String> headers = new ArrayList<String>();
		headers.add(Dictionnary.id);
		headers.add(Dictionnary.pDebut);
		headers.add(Dictionnary.sex);
		headers.add(Dictionnary.ageGroup);
		headers.add(Dictionnary.firstDep + "Short");//FIRST departure
		headers.add(Dictionnary.lastDep + "Short");
		headers.add(Dictionnary.firstDep);//FIRST departure
		headers.add(Dictionnary.lastDep);
		headers.add(Dictionnary.fidelPtRange);
		headers.add(Dictionnary.nAct);
		for(int i = 0; i < choiceSetSize; i++){
			headers.add(Dictionnary.firstDep + "Short" +Integer.toString(i));
			headers.add(Dictionnary.lastDep + "Short" +Integer.toString(i));
			headers.add(Dictionnary.fidelPtRange+Integer.toString(i));
			headers.add(Dictionnary.nAct+Integer.toString(i));
		}
		/*headers.add(Dictionnary.sim + Dictionnary.nAct);
		headers.add(Dictionnary.sim + Dictionnary.fidelPtRange);
		headers.add(Dictionnary.sim + Dictionnary.firstDep);
		headers.add(Dictionnary.sim + Dictionnary.lastDep);*/
		
		headers.add(Dictionnary.choice);
		
		try {
			printColumns(headers);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Object> createSubSamples(int numberOfCores){

	    //creates sub group of zone that can be processed using multithreading to accelerate computation
	    int subSampleSize = myData.get(Dictionnary.id).size()/(numberOfCores);
	    ArrayList<Object> subSamples = new ArrayList<Object>();
	    int idxCore = 0;
	    
	    for (int j = 0; j<numberOfCores; j++){
	    	HashMap<String, Object> currSubSample = new HashMap<String, Object>();
	    	for (String key: myData.keySet()){
	    		currSubSample.put(key, new ArrayList<Object>());
	    	}
	    	subSamples.add(currSubSample);
	    }
	    
	    System.out.println(myData.get(Dictionnary.id).size());
	    for(int k = 0; k < myData.get(Dictionnary.id).size()-1; k++){
	    	if((k>=idxCore*subSampleSize && k < (idxCore+1)*subSampleSize)|| (k>=idxCore*subSampleSize && idxCore == numberOfCores-1)){// || (k>=i*subSampleSize && i == numberOfCores-1)
	    		for (String key: myData.keySet()){
	    			//System.out.println(key + "  "+ idxCore + "  " + k);
	    			try{

		    			((HashMap<String, ArrayList<Object>>) subSamples.get(idxCore)).get(key).add(myData.get(key).get(k));
	    			}
	    			catch(IndexOutOfBoundsException e){
	    				System.out.println(key + "  "+ idxCore + "  " + k);
	    				System.out.println(e);
	    			}
		    	}
	    	}
	    	else{
	    		idxCore++;
	    	}
	    }
	    for (int j = 0; j<numberOfCores; j++){
	    	int size = ((HashMap<String, ArrayList<Object>>) subSamples.get(j)).get(Dictionnary.id).size();
	    	System.out.println("sample " + j + ": " + size);	
	    }
	    return subSamples;
	}
	
	
	public void processDataMultiThreads(int numberOfCores, int nAlternatives, String pathControleFile, String pathOutput, String pathHypothesis) throws IOException
    {
		String workingDir = System.getProperty("user.dir");
		ControlFileGenerator biogemeGenerator = new ControlFileGenerator();
    	biogemeGenerator.generateBiogemeControlFile(pathControleFile, pathOutput, pathHypothesis);
    	HashMap<String,Integer> dictionnary = biogemeGenerator.getCombinationTable();
    	
    	
		storeData();

		//Here should be all the function that you will have developped to prepare your dataset (for instance here : 
		// process departure hours in three categories: before peak, during peak and after peak
		departureHour3categories();
		lastDepartureHour3categories();
		System.out.println("--categories for departure hours were processed");
		
		processChoice(dictionnary, biogemeGenerator.order);
		
	    ArrayList<Object> subSamples = createSubSamples(numberOfCores);
	    System.out.println("--data batched and ready for multithreading");
	    //create and run multiple threads
	    ExecutorService cores = Executors.newFixedThreadPool(numberOfCores);
    	Set<Future<HashMap<String,ArrayList<Object>>>> set = new HashSet<Future<HashMap<String,ArrayList<Object>>>>();
    	
    	
    	for(int i = 0; i< numberOfCores; i++){
    		Callable<HashMap<String,ArrayList<Object>>> callable = new processChoiceSet(subSamples.get(i), nAlternatives);
        	Future<HashMap<String,ArrayList<Object>>> future = cores.submit(callable);
            set.add(future);
    	}
    	
    	myData.clear();
    	boolean firstAnswer = true;
    	try {
            for (Future<HashMap<String,ArrayList<Object>>> future : set) {
            	HashMap<String,ArrayList<Object>> answer = future.get();
            	if(firstAnswer){
            		myData = answer;
            		firstAnswer = false;
            	}
            	else{
            		for (String key: myData.keySet()){
        	    		myData.get(key).addAll(answer.get(key));
        	    	}
            	}
            }
    	} 
    	catch (InterruptedException | ExecutionException ex) {
    		ex.printStackTrace(); 
    	}
    	
    	System.out.println("--alternatives processed");
		
		/*processDummies();
		System.out.println("-- dummies processed");
		
		nActivitiesSimulation();
		System.out.println("--number of activities were simulated");
		ptFidelitySimulation();
		System.out.println("--pt fidelity simulated");
		firstDepartureSimulation();
		System.out.println("--first departure simulated");
		lastDepartureSimulation();
		System.out.println("--last departure simulated");*/
		
		
		selectAndPrint(nAlternatives);
		cores.shutdown();
    }
	
	private static class processChoiceSet
    implements Callable, Runnable {
    	HashMap<String, ArrayList<Object>> currData;
    	int n;
    	public processChoiceSet(Object subSample, int numberOfAlternatives){
    		currData = (HashMap)subSample;
    		n = numberOfAlternatives;
    	}
    	
        public HashMap<String, ArrayList<Object>> call()  throws Exception {
   		    processAlternatives();
        	return currData;
        }
        
        @Override
		public void run() {
			// TODO Auto-generated method stub
			//return statistics;
		}
	
	
		public void processAlternatives( ){
			for(int i = 0; i < n; i++){
				currData.put(Dictionnary.firstDep + "Short" +Integer.toString(i), new ArrayList<Object>());
				currData.put(Dictionnary.lastDep + "Short"+ Integer.toString(i), new ArrayList<Object>());
				currData.put(Dictionnary.fidelPtRange+Integer.toString(i), new ArrayList<Object>());
				currData.put(Dictionnary.nAct+Integer.toString(i), new ArrayList<Object>());
			}
			
			for(int i = 0; i < currData.get(Dictionnary.id).size();i++){
				if(currData.get(Dictionnary.pDebut).get(i).equals("T") ){
					String hhId = (String)currData.get(Dictionnary.mNumero).get(i);
					String persId = (String)currData.get(Dictionnary.pRang).get(i);
					//String pStatut = (String)currData.get(Dictionnary.pStatut).get(i);
					Set<Integer> idClosests = findClosestAlternatives(
							n, 
							Double.parseDouble((String)currData.get(Dictionnary.mtmX).get(i)),
							Double.parseDouble((String)currData.get(Dictionnary.mtmY).get(i)), 
							hhId,
							persId);
					
					addAlternatives(idClosests);	
				}
				else{
					addAlternatives();
				}
				if(i%1000==0){
					System.out.println(i);
				}
			}
		}
	
		private void addAlternatives() {
			// TODO Auto-generated method stub
			for(int i = 0; i < n; i++){
				currData.get(Dictionnary.firstDep + "Short"+ Integer.toString(i)).add("0");
				currData.get(Dictionnary.lastDep + "Short" +Integer.toString(i)).add("0");
				currData.get(Dictionnary.fidelPtRange+Integer.toString(i)).add("0");
				currData.get(Dictionnary.nAct+Integer.toString(i)).add("0");
			}
		}
	
		private void addAlternatives(Set<Integer> idClosests) {
			// TODO Auto-generated method stub
			Iterator<Integer> it = idClosests.iterator();
			for(int i = 0; i < n; i++){
				int currIpere = it.next();
				int j = 0;
				while(currIpere != Integer.parseInt((String)currData.get(Dictionnary.id).get(j))){//IPERE is a unique identifier for each record in the travel survey of Montreal
					j++;
				}
				String altFirstDep = (String)currData.get(Dictionnary.firstDep + "Short").get(j);
				String altLastDep = (String)currData.get(Dictionnary.lastDep + "Short").get(j);
				String fidelPTRange = (String)currData.get(Dictionnary.fidelPtRange).get(j);
				Integer nActivities = (Integer)currData.get(Dictionnary.nAct).get(j);
				
				
				currData.get(Dictionnary.firstDep + "Short" +Integer.toString(i)).add(altFirstDep);
				currData.get(Dictionnary.lastDep + "Short" +Integer.toString(i)).add(altLastDep);
				currData.get(Dictionnary.fidelPtRange+Integer.toString(i)).add(fidelPTRange);
				currData.get(Dictionnary.nAct+Integer.toString(i)).add(nActivities);
			}
		}
	
		
		private Set<Integer> findClosestAlternatives(int n, double x, double y, String hhId, String persId) {
			// TODO Auto-generated method stub
			HashMap<Integer,Integer> closeAlternatives = new HashMap<Integer, Integer>(); // structure : the key is formed by IPERE (from OD survey), the value is the distance
			
			for(int j = 0; j < currData.get(Dictionnary.id).size(); j++){
				if(
						currData.get(Dictionnary.pDebut).get(j).equals("T") && 
						!(((String)currData.get(Dictionnary.mNumero).get(j)).equals(hhId) && 
						((String)currData.get(Dictionnary.pRang).get(j)).equals(persId))
				){
					double curX = Double.parseDouble((String)currData.get(Dictionnary.mtmX).get(j));
					double curY = Double.parseDouble((String)currData.get(Dictionnary.mtmY).get(j));
					int curIpere = Integer.parseInt((String)currData.get(Dictionnary.id).get(j));
					int curDist = (int)(Math.pow((x-curX), 2) + Math.pow((y-curY), 2)); 
					
					if(closeAlternatives.size() < n){
						closeAlternatives.put(curIpere, curDist);
					}
					else{
						int[] removeId = getFarthestAltenative(closeAlternatives);
						if(curDist< removeId[1]){
							closeAlternatives.remove(removeId[0]);
							closeAlternatives.put(curIpere, curDist);
						}
					}				
				}
				else{	
				}	
			}
			return closeAlternatives.keySet();
		}
		
		private int[] getFarthestAltenative(HashMap<Integer, Integer> closeAlternatives) {
			// TODO Auto-generated method stub
			int curDist = 0;
			int curId = 0;
			for(java.util.Map.Entry<Integer, Integer> e:closeAlternatives.entrySet()){
				if(e.getValue() > curDist){
					curId = e.getKey();
					curDist = e.getValue();
				}
			}
			int answer[] = new int[2];
			answer[0] = curId;
			answer[1] = curDist;
			return answer;
		}
	}
	
	
	public void departureHour3categories(){
		myData.put(Dictionnary.firstDep + "Short", new ArrayList());
		for(int i = 0; i < myData.get(Dictionnary.id).size(); i++){
			String temp = (String) myData.get(Dictionnary.firstDep).get(i);
			if(temp.equals("1")){myData.get(Dictionnary.firstDep+ "Short").add("0");} //living earlier then peak hour
			else if (temp.equals("2")){myData.get(Dictionnary.firstDep+ "Short").add("1");} // leaving during peak hour (6-9am)
			else if (temp.equals("10")){myData.get(Dictionnary.firstDep+ "Short").add("10");}
			else {myData.get(Dictionnary.firstDep+ "Short").add("2");} 
		}
	}
	
	public void lastDepartureHour3categories(){
		myData.put(Dictionnary.lastDep + "Short", new ArrayList());
		
		for(int i = 0; i < myData.get(Dictionnary.id).size(); i++){
			int hour = Integer.parseInt((String)myData.get(Dictionnary.lastDep).get(i));
			if(hour<1530){myData.get(Dictionnary.lastDep+ "Short").add("0");}
			else if(hour < 1830){myData.get(Dictionnary.lastDep+ "Short").add("1");}
			else if (hour >=1830){myData.get(Dictionnary.lastDep+ "Short").add("2");}
			else{myData.get(Dictionnary.lastDep + "Short").add("10");}
		}
	}

	
	
	
	public int antitheticDrawMNL(ArrayList<Double> utilities){
		ArrayList<Double> exponentials = new ArrayList<Double>();
		double denominator = 0;
		for(Double u : utilities){
			double expU = Math.exp(u);
			exponentials.add(expU);
			denominator += expU;
		}
		ArrayList<Double> cumulativeProbabilities = new ArrayList<Double>();
		double cumul = 0;
		for(Double eU : exponentials){
			cumul += eU;
			cumulativeProbabilities.add(cumul/denominator);
		}
		
		double randVal = randGen.NextDoubleInRange(0, 1);
		int index =0;
		for(int i =0; i<cumulativeProbabilities.size(); i++){
			if(randVal>cumulativeProbabilities.get(i)){
			}
			else{
				index = i;
				i+=1000;
			}
		}
		//System.out.println(utilities);
		//System.out.println(cumulativeProbabilities);
		//System.out.println("choice : " + index + " rand value  " + randVal);
		return index;
	}

	public void processDummies() {
		// TODO Auto-generated method stub
		myData.put(Dictionnary.dummyInactiveWomen, new ArrayList<Object>());
		myData.put(Dictionnary.dummyInactiveMen, new ArrayList<Object>());
		
		for(int i = 0; i < myData.get(Dictionnary.id).size(); i++){
			//inactivewoman
			if(myData.get(Dictionnary.sex).get(i).equals("2") &&
					(myData.get(Dictionnary.pStatut).get(i).equals("4")||
					myData.get(Dictionnary.pStatut).get(i).equals("7"))){
				myData.get(Dictionnary.dummyInactiveWomen).add(1);
			}
			else{
				myData.get(Dictionnary.dummyInactiveWomen).add(0);
			}
			//inactive men
			if(myData.get(Dictionnary.sex).get(i).equals("1") &&
					(myData.get(Dictionnary.pStatut).get(i).equals("4")||
					myData.get(Dictionnary.pStatut).get(i).equals("7"))){
				myData.get(Dictionnary.dummyInactiveMen).add(1);
			}
			else{
				myData.get(Dictionnary.dummyInactiveMen).add(0);
			}
			
		}
	}
	
	public void processChoice(HashMap<String, Integer> dictionnary, ArrayList<String> order){
		myData.put(Dictionnary.choice, new ArrayList<Object>());
		
		for(int i = 0; i < myData.get(Dictionnary.id).size()-1; i++){
			String ref = new String();
			
			if((int)myData.get(Dictionnary.nAct).get(i) == 0){
				ref = Dictionnary.stayedHome;
			}
			else if(myData.get(Dictionnary.fidelPtRange).get(i).equals("0")||myData.get(Dictionnary.fidelPtRange).get(i).equals("10")){
				ref = Dictionnary.noPT;
			}
			else{
				Iterator<String> it = order.iterator();
				while(it.hasNext()){
					String curr = it.next();
					ref+=myData.get(curr).get(i);
				}
			}
			String choice = Integer.toString(dictionnary.get(ref));
			myData.get(Dictionnary.choice).add(choice);
			
		}
	}
}
