/**
 * 
 */
package Biogeme;

import java.util.ArrayList;
import java.util.HashMap;

import Utils.RandomNumberGen;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class BiogemeAgent {
	
	HashMap<String, String> myAttributes;
	protected static RandomNumberGen randGen = new RandomNumberGen();
	
	public BiogemeAgent(){
		myAttributes = new HashMap<String, String>();
	}

	public void setAttributes(ArrayList<String> attributeNames, ArrayList<String> attributeValues) {
		// TODO Auto-generated method stub
		for(int i = 0; i < attributeNames.size(); i++){
			myAttributes.put(attributeNames.get(i), attributeValues.get(i));
		}
	}

	public void applyModel(ArrayList<Integer> choiceSet) {
		// TODO Auto-generated method stub
		//rigth I am just able to apply dummies
		ArrayList<Double> utilities = new ArrayList<Double>();
		for(int i = 0; i < choiceSet.size(); i++){
			
			double utility = 0;
			BiogemeChoice currChoice =BiogemeControlFileGenerator.choiceIndex.get(choiceSet.get(i));
			
			for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
				if(currH.isCst()){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH) && currChoice.isAffecting(currH, this) && currH.isDummy){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH) && !currH.isDummy){
					utility += currH.getCoefficientValue() * currChoice.getAffectingValue(currH, this);
				}
			}
			utilities.add(utility);
		}
		
		ArrayList<Double> cumProbabilities = processUtilities(utilities);
		int choiceIndex = antitheticDraw(cumProbabilities);
		myAttributes.put(Utils.sim, Integer.toString(choiceSet.get(choiceIndex)));
	}

	/*private boolean isCst(BiogemeHypothesis currH) {
		// TODO Auto-generated method stub
		for(BiogemeChoice currChoice: BiogemeControlFileGenerator.choiceIndex){
			if(currChoice.isCst(currH)){
				return true;
			}
		}
		return false;
	}*/

	private ArrayList<Double> processUtilities(ArrayList<Double> utilities) {
		// TODO Auto-generated method stub
		ArrayList<Double> cumProbabilities = new ArrayList<Double>();
		Double logsum = 0.0;
		for(int i = 0; i < utilities.size(); i++){
			logsum += Math.exp(utilities.get(i));
		}
		double currProbability = 0;
		for(int i = 0; i < utilities.size(); i++){
			currProbability += Math.exp(utilities.get(i)) / logsum;
			cumProbabilities.add(currProbability);
		}
		
		return cumProbabilities;
	}

	public ArrayList<Integer> processChoiceSet() {
		// TODO Auto-generated method stub
		ArrayList<Integer> choiceSet = new ArrayList<Integer>();
		for(String header: myAttributes.keySet()){
			//System.out.println(header);
			//System.out.println(header);
			if(header.contains(Utils.alternative)){
				choiceSet.add(Integer.parseInt(myAttributes.get(header)));
			}
		}
		return choiceSet;
	}
	
	public int antitheticDraw(ArrayList<Double> cumulativeProbabilities){

		double randVal = randGen.NextDoubleInRange(0, 1);
		int index =0;
		for(int i =0; i<cumulativeProbabilities.size(); i++){
			if(randVal>cumulativeProbabilities.get(i)){
			}
			else{
				index = i;
				i+=cumulativeProbabilities.size()+1;
			}
		}
		//System.out.println(utilities);
		//System.out.println(cumulativeProbabilities);
		//System.out.println("choice : " + index + " rand value  " + randVal);
		return index;
	}

}
