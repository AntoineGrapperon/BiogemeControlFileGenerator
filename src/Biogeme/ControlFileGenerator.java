/**
 * 
 */
package Biogeme;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import Utils.Dictionnary;
import Utils.Reader;
import Utils.Writer;

/**
 * @author Antoine
 *
 */





public class ControlFileGenerator {
	/**
	 * 
	 */
	Reader descReader = new Reader();
	Reader hypothesisReader = new Reader();
    Writer myDataWriter =  new Writer();
    HashMap<String, Integer> choiceDimensions = new HashMap<String,Integer>();
    ArrayList<String> order = new ArrayList<String>();
    ArrayList<HashMap<String,Integer>> combinations = new ArrayList<HashMap<String,Integer>>();
    ArrayList<Hypothesis> hypothesis = new ArrayList<Hypothesis>();
    
    public ControlFileGenerator(){
    	
    }
    
    public void initialize(String pathControleFile, String pathOutput, String pathToHypothesis) throws IOException{
    	descReader.OpenFile(pathControleFile);
    	hypothesisReader.OpenFile(pathToHypothesis);
    	myDataWriter.OpenFile(pathOutput);
    	choiceDimensions = getChoiceDimensions();
    	hypothesis = getHypothesis();
    }
    
    /* Hypothesis File:
     * Constant Name, Name of affecting variables = categories affecting, Name of affected variables = categories affected
     * NOT_PT_RIDER, NCARS = 1-2-3, PT_USE = 0
     * RETIRE_OUT_OF_PEAK_HOUR, OCCUPATION = 3, DEPARTURE_HOUR=3
     */
    
    private ArrayList<Hypothesis> getHypothesis() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
	  ArrayList<Hypothesis> answer = new ArrayList<Hypothesis> ();
	  String strTok;
	  while ((strTok = hypothesisReader.myFileReader.readLine()) != null){
		  String[] tok = strTok.split(Dictionnary.COLUMN_DELIMETER);
		  Hypothesis currHypothesis = new Hypothesis();
		  
		  String coefName = tok[0];
		  currHypothesis.setCoefName(coefName);
		  
		  String[] affectingTok = tok[1].split("=");
		  String dimName = affectingTok[0].trim();
		  String[]affectingCategoriesTok =affectingTok[1].split("-");
		  ArrayList<Integer> categories = new ArrayList<Integer>();
		  for(String e: affectingCategoriesTok){
			  categories.add(Integer.parseInt(e.trim()));
		  }
		  currHypothesis.setAffectingDimension(dimName, categories);
		  
		  String[] affectedTok = tok[2].split("=");
		  dimName = affectedTok[0].trim();
		  String[]affectedCategoriesTok =affectingTok[1].split("-");
		  categories = new ArrayList<Integer>();
		  for(String e: affectedCategoriesTok){
			  categories.add(Integer.parseInt(e.trim()));
		  }
		  currHypothesis.setAffectedDimension(dimName, categories);
		  
		  answer.add(currHypothesis);
	  }
		return answer;
	}

	//this function expect the input data to be in format:
    /*
     * name of category, number of alternatives  ex: 
     * departure hour, 3
     * last dep hour, 3
     * nActivities, 4
     */
    public HashMap<String, Integer> getChoiceDimensions() throws IOException{
    	HashMap<String, Integer> answer = new HashMap<String, Integer>();
    	String strTok;
    	while ((strTok = descReader.myFileReader.readLine()) != null){
    		String[] tok = strTok.split(Dictionnary.COLUMN_DELIMETER);
    		answer.put(tok[0], Integer.parseInt(tok[1].trim()));
        }
    	return answer;
    }
    
    
    public void generateBiogemeControlFile(String pathControleFile, String pathOutput, String pathHypothesis) throws IOException{
    	initialize(pathControleFile, pathOutput, pathHypothesis);
    	writeUpperPart();
    	generateCombinations();
    	writeBetas();
    	writeCoefficients();
    	writeCombinations();
    	writeLowerPart();
    	descReader.CloseFile();
    	hypothesisReader.CloseFile();
    	myDataWriter.CloseFile();
    }
    
    public void generateCombinations() throws IOException{
    	for(String key: choiceDimensions.keySet()){
    		updateCombinations(key);
    	}
    }
    
    public void writeBetas() throws IOException{
    	myDataWriter.WriteToFile("[Beta]");
    	Iterator<HashMap<String,Integer>> it = combinations.iterator();
    	String home = "0";
    	String ptUser = "0";
    	boolean first = false;
    	
		while(it.hasNext()){
			
			HashMap<String,Integer> currCombination = it.next();
			String output = new String();
			String categoryNameReminder = new String();
			
			if(!first){
				for(String key: currCombination.keySet()){
					categoryNameReminder += " - " + key;
					order.add(key);
				}
				myDataWriter.WriteToFile("// " + categoryNameReminder);
				first = true;
			}
			
			if(shouldWrite(currCombination, home, ptUser)){
				for(String key: currCombination.keySet()){
					output+= "_"+currCombination.get(key);
				}
				myDataWriter.WriteToFile("C" + output + " 	    0.0          -10.0     10.0         0");	
			}
		}
    }
    
    public void writeCoefficients() throws IOException{
    	for(Hypothesis h: hypothesis){
    		myDataWriter.WriteToFile(h.coefName + " 	    0.0          -10.0     10.0         0");
    	}
    }
    
    public String processCase(HashMap<String,Integer> combination, HashMap<String, Integer> dictionnary){
	
		String ref = new String();
		
		/*if((int)combination.get(Dictionnary.nAct) == 0){
			ref = Dictionnary.stayedHome;
		}
		else if(combination.get(Dictionnary.fidelPtRange)==0){
			ref = Dictionnary.noPT;
		}
		else{
			Iterator<String> it = order.iterator();
			while(it.hasNext()){
				String curr = it.next();
				ref+=combination.get(curr)+Dictionnary.CATEGORY_DELIMITER;
			}
			ref.substring(0,ref.length()-1);
		}*/
		
		
		
		Iterator<String> it = order.iterator();
		while(it.hasNext()){
			String curr = it.next();
			ref+=combination.get(curr)+Dictionnary.CATEGORY_DELIMITER;
		}
		ref = ref.substring(0,ref.length()-1);
		String combinationCase = Integer.toString(dictionnary.get(ref));
		return combinationCase;
	}
    
    private boolean shouldWrite(HashMap<String, Integer> currCombination, String home, String ptUser){
    	boolean answer = false;
    	if(currCombination.get(Dictionnary.nAct) == 0 && home.equals("0")){
			home = "1";
			answer = true;
		}
		else if(currCombination.get(Dictionnary.nAct)!= 0 && currCombination.get(Dictionnary.fidelPtRange)==0 && ptUser.equals("0")){
			ptUser = "1";
			answer = true;
		}
		else if(currCombination.get(Dictionnary.nAct)!=0 && currCombination.get(Dictionnary.fidelPtRange)!=0){
			answer = true;
		}
    	return answer;
    }
    
    private void writeCombinations() throws IOException{

    	myDataWriter.WriteToFile("[Utilities]");
    	HashMap<String, Integer> dictionnary = getCombinationTable();
    	Iterator<HashMap<String,Integer>> it = combinations.iterator();
    	String home = "0";
    	String ptUser = "0";
    	
		while(it.hasNext()){
			
			HashMap<String,Integer> currCombination = it.next();
			String choice = processCase(currCombination, dictionnary);
			String output = new String();
			
			/*if(currCombination.get(Dictionnary.nAct) == 0 && !home){
				output = Dictionnary.stayedHome;
				home = true;
				output = choice + "	" + output + "	avail	"+ output +" * one";
				output = output + addCoefficients(currCombination);
				myDataWriter.WriteToFile(output);
			}
			else if(currCombination.get(Dictionnary.nAct)!= 0 && currCombination.get(Dictionnary.fidelPtRange)==0 && !ptUser){
				output = Dictionnary.noPT;
				ptUser = true;
				output = choice + "	" + output + "	avail	"+ output +" * one";
				output = output + addCoefficients(currCombination);
				myDataWriter.WriteToFile(output);
			}
			else if(currCombination.get(Dictionnary.nAct)!=0 && currCombination.get(Dictionnary.fidelPtRange)!=0){
				for(String key: currCombination.keySet()){
					output+= "_"+currCombination.get(key);
				}
				output = choice + "	C" + output + "	avail	C"+ output +" * one";
				output = output + addCoefficients(currCombination);
				myDataWriter.WriteToFile(output);
			}	*/
			
			if(shouldWrite(currCombination,home,ptUser)){
				for(String key: currCombination.keySet()){
					output+= "_"+currCombination.get(key);
				}
				output = choice + "	C" + output + "	avail	C"+ output +" * one";
				output = output + addCoefficients(currCombination);
				myDataWriter.WriteToFile(output);
			}
		}
    }
	
    
    private String addCoefficients(HashMap<String, Integer> currCombination) {
		// TODO Auto-generated method stub
    	String output = new String();
    	for(Hypothesis e: hypothesis){
    		String affectedDim = e.affectedDimensionName;
    		ArrayList<Integer> affectedCategories = e.affectedCategories;
    		boolean requiresCoefficient = false;
    		//System.out.println(affectedDim);
    		//System.out.println(currCombination.toString());
    		int category = currCombination.get(affectedDim);
    		for(int i: affectedCategories){
    			if(i == category){
    				requiresCoefficient = true;
    			}
    		}
    		if(requiresCoefficient){
    			output += " + " + e.coefName + " * " + e.affectingDimensionName;
    		}
    	}
		return output;
	}

	private void updateCombinations(String key) {
		// TODO Auto-generated method stub
    	ArrayList<HashMap<String,Integer>> tempChoices = new ArrayList<HashMap<String, Integer>>();
    	if(combinations.isEmpty()){
    		for(int i = 0; i < choiceDimensions.get(key); i++){
    			HashMap<String,Integer> nextChoice = new HashMap<String,Integer>();
    			nextChoice.put(key, i);
				tempChoices.add(nextChoice);
			}
    	}
    	else{
    		Iterator<HashMap<String,Integer>> it = combinations.iterator();
    		while(it.hasNext()){
    			HashMap<String,Integer> currChoice = it.next();
    			
    			for(int i = 0; i < choiceDimensions.get(key); i++){
    				HashMap<String,Integer> nextChoice = new HashMap<String,Integer>(currChoice);
    				nextChoice.put(key, i);
    				tempChoices.add(nextChoice);
    			}
    		}
    	}
    	combinations.removeAll(combinations);
		combinations.addAll(tempChoices);
	}

	
    public HashMap<String, Integer> getCombinationTable(){
    	HashMap<String, Integer> dictionnary = new HashMap<String,Integer>();
    	
    	Iterator<HashMap<String,Integer>> it = combinations.iterator();
    	int n = 0;
    	String home = "0";
    	String ptUser = "0";
    	
		while(it.hasNext()){
			
			HashMap<String,Integer> currCombination = it.next();
			String ref = new String();
			
			/*if(currCombination.get(Dictionnary.nAct) == 0 && !home){
				ref = Dictionnary.stayedHome;
				dictionnary.put(ref, n);
				n++;
				home = true;
			}
			else if(currCombination.get(Dictionnary.nAct)!= 0 && currCombination.get(Dictionnary.fidelPtRange)==0 && ptUser){
				ref = Dictionnary.noPT;
				dictionnary.put(ref, n);
				n++;
				ptUser = false;
			}
			else if(currCombination.get(Dictionnary.nAct)!=0 && currCombination.get(Dictionnary.fidelPtRange)!=0){
				for(String key: currCombination.keySet()){
					ref += Integer.toString(currCombination.get(key)) + Dictionnary.CATEGORY_DELIMITER;
				}
				ref = ref.substring(0, ref.length()-1);
				dictionnary.put(ref, n);
				n++;
			}
			else{
				n++;
			}*/
			
			if(shouldWrite(currCombination,home,ptUser)){
				for(String key: currCombination.keySet()){
					ref += Integer.toString(currCombination.get(key)) + Dictionnary.CATEGORY_DELIMITER;
				}
				ref = ref.substring(0, ref.length()-1);
				dictionnary.put(ref, n);
				n++;
			}
		}
    	System.out.println(dictionnary.toString());
    	return dictionnary;
    }
    
    public void writeUpperPart() throws IOException{
    	myDataWriter.WriteToFile("//Auto generated control file to use with Biogeme for windows");
    	myDataWriter.WriteToFile("//Author: Antoine Grapperon, antoine.grapperon@free.fr");
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = new Date();
    	myDataWriter.WriteToFile("//date: " + dateFormat.format(date)); 
    	myDataWriter.WriteToFile("[Choice]\r\n" + 
    			Dictionnary.choice);
    	
    }
    
    public void writeLowerPart() throws IOException{
    	myDataWriter.WriteToFile("[Expressions] ");
    	myDataWriter.WriteToFile("one = 1 ");
    	myDataWriter.WriteToFile("avail = 1 ");
    	myDataWriter.WriteToFile("[Exclude]");
    	//myDataWriter.WriteToFile("((P_GRAGE == 1) + (P_STATUT == 6) + (P_STATUT == 8) + (P_STATUT == 5) + (N_ACT == 0))  >= 1  //+ ((P_STATUT != 1) + (P_STATUT != 2)) / 2)");
    	myDataWriter.WriteToFile("[Model]");
    	myDataWriter.WriteToFile("$MNL");
    }
}
