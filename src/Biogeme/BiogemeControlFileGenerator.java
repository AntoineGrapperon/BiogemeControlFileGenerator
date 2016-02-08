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

import Utils.Reader;
import Utils.Writer;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class BiogemeControlFileGenerator {
	Reader descReader = new Reader();
	Reader hypothesisReader = new Reader();
    Writer myDataWriter =  new Writer();
    HashMap<String, Integer> choiceDimensions = new HashMap<String,Integer>();
    ArrayList<String> order = new ArrayList<String>();
    ArrayList<HashMap<String,Integer>> combinations = new ArrayList<HashMap<String,Integer>>();
    public static ArrayList<BiogemeHypothesis> hypothesis = new ArrayList<BiogemeHypothesis>();
    public static ArrayList<BiogemeChoice> choiceIndex = new ArrayList<BiogemeChoice>();
    
    public BiogemeControlFileGenerator(){
    	
    }
    
    public void initialize(String pathControleFile, String pathOutput, String pathToHypothesis) throws IOException{
    	descReader.OpenFile(pathControleFile);
    	hypothesisReader.OpenFile(pathToHypothesis);
    	myDataWriter.OpenFile(pathOutput);
    	choiceDimensions = getTripChainAlternatives();
    	hypothesis = getHypothesis();
    	generateCombinations();
    	choiceIndex.addAll(getChoiceIndex());
    }
    
  private ArrayList<BiogemeHypothesis> getHypothesis() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
	  ArrayList<BiogemeHypothesis> answer = new ArrayList<BiogemeHypothesis> ();
	  String strTok;
	  while ((strTok = hypothesisReader.myFileReader.readLine()) != null){
		  String[] tok = strTok.split(Utils.COLUMN_DELIMETER);
		  BiogemeHypothesis currHypothesis = new BiogemeHypothesis();
		  
		  
		  boolean isDummy = tok[3].trim().equals("dummy");
		  currHypothesis.isDummy = isDummy;
		  String coefName = tok[0];
		  currHypothesis.setCoefName(coefName);
		  
		  if(isDummy){
			  String[] affectingTok = tok[1].split("=");
			  String dimName = affectingTok[0].trim();
			  String[]affectingCategoriesTok =affectingTok[1].split("-");
			  ArrayList<Integer> categories = new ArrayList<Integer>();
			  for(String e: affectingCategoriesTok){
				  categories.add(Integer.parseInt(e.trim()));
			  }
			  currHypothesis.setAffectingDimension(dimName, categories);
		  }
		  
		  else if(!isDummy){
			  String[] affectingTok = tok[1].split("=");
			  String dimName = affectingTok[0].trim();
			  currHypothesis.setAffectingDimension(dimName, null);
		  }
		  
		  String[] affectedTok = tok[2].split("=");
		  String dimName = affectedTok[0].trim();
		  String[]affectedCategoriesTok = affectedTok[1].split("-");
		  ArrayList<Integer> categories = new ArrayList<Integer>();
		  for(String e: affectedCategoriesTok){
			  categories.add(Integer.parseInt(e.trim()));
		  }
		  currHypothesis.setAffectedDimension(dimName, categories);
		  
		  answer.add(currHypothesis);
	  }
	  
		return answer;
	}

    public HashMap<String, Integer> getTripChainAlternatives() throws IOException{
    	HashMap<String, Integer> answer = new HashMap<String, Integer>();
    	String strTok;
    	while ((strTok = descReader.myFileReader.readLine()) != null){
    		String[] tok = strTok.split(Utils.COLUMN_DELIMETER);
    		answer.put(tok[0], Integer.parseInt(tok[1].trim()));
        }
    	return answer;
    }
    
    public void generateBiogemeControlFile() throws IOException{
    	writeUpperPart();
    	writeBetaPart();
    	writeCombinations();
    	writeLowerPart();
    	descReader.CloseFile();
    	myDataWriter.CloseFile();
    }
    
    private void writeBetaPart() throws IOException {
		// TODO Auto-generated method stub
    	myDataWriter.WriteToFile("[Beta]");
    	writeConstants();
    	writeHypothesisBeta();
	}

	public void generateCombinations() throws IOException{
    	for(String key: choiceDimensions.keySet()){
    		updateCombinations(key);
    	}
    }
    
    public void writeConstants() throws IOException{
    	String headers = "// ";
    	for(String key: combinations.get(0).keySet()){
			headers+= "_"+key;
		}
    	myDataWriter.WriteToFile(headers);	
    	
    	ArrayList<String> alreadyWritten = new ArrayList<String>();
    	Iterator<BiogemeChoice> it = choiceIndex.iterator();
    	while(it.hasNext()){
    		BiogemeChoice currChoice = it.next();
    		String choiceName = currChoice.getConstantName();
    		if(!alreadyWritten.contains(choiceName)){
    			alreadyWritten.add(choiceName);
    			myDataWriter.WriteToFile(choiceName + " 	    0.0          -10.0     10.0         0");	
    		}
    	}
    }
    
    public void writeHypothesisBeta() throws IOException{
    	for(BiogemeHypothesis h: hypothesis){
    		myDataWriter.WriteToFile(h.coefName + " 	    0.0          -10.0     10.0         0");
    	}
    }
    
   /* public String processChoice(HashMap<String,Integer> combination, HashMap<String, Integer> dictionnary){
	
		String ref = new String();
		
		if((int)combination.get(Utils.nAct) == 0){
			ref = Utils.stayedHome;
		}
		else if(combination.get(Utils.fidelPtRange)==0){
			ref = Utils.noPT;
		}
		else{
			Iterator<String> it = order.iterator();
			while(it.hasNext()){
				String curr = it.next();
				ref+=combination.get(curr);
			}
		}
		String choice = Integer.toString(dictionnary.get(ref));
		return choice;
	}*/
    
    private void writeCombinations() throws IOException{

    	myDataWriter.WriteToFile("[Utilities]");
    	ArrayList<String> alreadyWritten = new ArrayList<String>();
    	Iterator<BiogemeChoice> it = choiceIndex.iterator();
    	while(it.hasNext()){
    		BiogemeChoice currChoice = it.next();
    		String choiceName = currChoice.getConstantName();
    		Integer choiceId = currChoice.biogeme_id;
    		String output = new String();
    		if(!alreadyWritten.contains(choiceName)){
    			alreadyWritten.add(choiceName);
    			output = choiceId + "	" + choiceName + "	avail" + choiceName + " * one" + addCoefficients(currChoice.choiceCombination);	
    			myDataWriter.WriteToFile(output);
    		}
    	}
    }
    
    private String addCoefficients(HashMap<String, Integer> currCombination) {
		// TODO Auto-generated method stub
    	String output = new String();
    	for(BiogemeHypothesis e: hypothesis){
    		String affectedDim = e.affectedDimensionName;
    		ArrayList<Integer> affectedCategories = e.affectedCategories;
    		boolean requiresCoefficient = false;
    		int category = currCombination.get(affectedDim);
    		for(int i: affectedCategories){
    			if(i == category){
    				requiresCoefficient = true;
    			}
    		}
    		if(requiresCoefficient){
    			output += " + " + e.coefName + " * " + e.coefName + Utils.var;
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

	
	 public ArrayList<BiogemeChoice> getChoiceIndex(){
		 ArrayList<BiogemeChoice> choiceIndex = new ArrayList<BiogemeChoice>();
	    	
	    	Iterator<HashMap<String,Integer>> it = combinations.iterator();
	    	int n = 0;
	    	
			while(it.hasNext()){
				
				HashMap<String,Integer> currCombination = it.next();
				String ref = new String();
				BiogemeChoice currChoice = new BiogemeChoice();
				
				if(currCombination.get(Utils.nAct) == 0){
					currChoice.biogeme_id = n;
					currChoice.choiceCombination = currCombination;
					n++;
				}
				else if(currCombination.get(Utils.nAct)!= 0 && currCombination.get(Utils.fidelPtRange)==0){
					currChoice.biogeme_id = n;
					currChoice.choiceCombination = currCombination;
					n++;
				}
				else if(currCombination.get(Utils.nAct)!=0 && currCombination.get(Utils.fidelPtRange)!=0){
					currChoice.biogeme_id = n;
					currChoice.choiceCombination = currCombination;
					n++;
				}
				else{
					System.out.println("there was a problem in the index generation");
					n++;
				}
				choiceIndex.add(currChoice);
			}
	    	return choiceIndex;
	    }
	
    /*public HashMap<String, Integer> getCombinations(){
    	HashMap<String, Integer> dictionnary = new HashMap<String,Integer>();
    	
    	Iterator<HashMap<String,Integer>> it = combinations.iterator();
    	int n = 0;
    	boolean home = false;
    	boolean ptUser = true;
    	
		while(it.hasNext()){
			
			HashMap<String,Integer> currCombination = it.next();
			String ref = new String();
			
			if(currCombination.get(Utils.nAct) == 0 && !home){
				ref = Utils.stayedHome;
				dictionnary.put(ref, n);
				n++;
				home = true;
			}
			else if(currCombination.get(Utils.nAct)!= 0 && currCombination.get(Utils.fidelPtRange)==0 && ptUser){
				ref = Utils.noPT;
				dictionnary.put(ref, n);
				n++;
				ptUser = false;
			}
			else if(currCombination.get(Utils.nAct)!=0 && currCombination.get(Utils.fidelPtRange)!=0){
				for(String key: currCombination.keySet()){
					ref += Integer.toString(currCombination.get(key));
				}
				dictionnary.put(ref, n);
				n++;
			}
			else{
				n++;
			}
		}
    	System.out.println(dictionnary.toString());
    	return dictionnary;
    }*/
    
    public void writeUpperPart() throws IOException{
    	myDataWriter.WriteToFile("//Auto generated control file to use with Biogeme for windows");
    	myDataWriter.WriteToFile("//Author: Antoine Grapperon, antoine.grapperon@free.fr");
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = new Date();
    	myDataWriter.WriteToFile("//date: " + dateFormat.format(date)); 
    	myDataWriter.WriteToFile("[Choice]\r\n" + 
    			Utils.choice);
    	
    }
    
    public void writeLowerPart() throws IOException{
    	myDataWriter.WriteToFile("[Expressions] ");
    	myDataWriter.WriteToFile("one = 1 ");
    	myDataWriter.WriteToFile("avail = 1 ");
    	myDataWriter.WriteToFile("EARLY_WORKER_var = (OCCUP == 0 ) * (FIRST_DEPShort * 0 )");
    	myDataWriter.WriteToFile("RETIRE_FIRST_DEP_var = (OCCUP == 2 ) * (FIRST_DEPShort * 2 )");
    	myDataWriter.WriteToFile("[Exclude]");
    	myDataWriter.WriteToFile("(GRPAGE == 0) >= 1");		
    	myDataWriter.WriteToFile("[Model]");
    	myDataWriter.WriteToFile("$MNL");
    }
    

	public void printChoiceIndex(String path) throws IOException{
		Writer tempWriter = new Writer();
		tempWriter.OpenFile(path);
		for(BiogemeChoice c : choiceIndex){
			tempWriter.WriteToFile(c.toString());
		}
		tempWriter.CloseFile();
	}
	
	private void writeDummies() throws IOException {
		// TODO Auto-generated method stub
		for(BiogemeHypothesis currH : hypothesis){
			if(currH.isDummy){
				String newExpression = currH.affectingDimensionName + Utils.var + " = ";
				for(int i : currH.affectingCategories){
					newExpression+= " (" + currH.affectingDimensionName + " == " + i + " ) *";
				}
				newExpression = newExpression.trim().substring(0, newExpression.length() -1);
				myDataWriter.WriteToFile(newExpression);
			}
		}
	}
}
