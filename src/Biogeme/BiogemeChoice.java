/**
 * 
 */
package Biogeme;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Antoine
 *
 */
public class BiogemeChoice {
	
	int biogeme_id;
	HashMap<String,Integer> choiceCombination = new HashMap<String,Integer> ();
	
	public BiogemeChoice(){
		
	}

	public boolean isAffecting(BiogemeHypothesis currH, BiogemeAgent currAgent) {
		// TODO Auto-generated method stub
		
		for(int i = 0; i < currH.affectingCategories.size();i++){
			if(choiceCombination.containsKey(currH.affectingDimensionName)){
				if(choiceCombination.get(currH.affectingDimensionName) == currH.affectingCategories.get(i)){
					return true;
				}
			}
			else if(currAgent.myAttributes.containsKey(currH.affectingDimensionName)){
				if(currH.affectingCategories.get(i) == Integer.parseInt(currAgent.myAttributes.get(currH.affectingDimensionName))){
					return true;
				}
			}
			else{
				//System.out.println("oups " + currH.affectingDimensionName);	
			}
		}
		return false;
	}
	
	
	public boolean isAffected(BiogemeHypothesis currH){
		// TODO Auto-generated method stub
		
		for(int i = 0; i < currH.affectedCategories.size();i++){
			if(choiceCombination.get(currH.affectedDimensionName) == currH.affectedCategories.get(i)){
				return true;
			}
		}
		//System.out.println("oups " + currH.affectingDimensionName);
		return false;
	}
	
	public boolean isCst(BiogemeHypothesis currH){
		// TODO Auto-generated method stub
		if(currH.coefName.equals(getConstantName())){
			return true;
		}
		
		return false;
	}

	public double getAffectingValue(BiogemeHypothesis currH, BiogemeAgent currAgent) {
		// TODO Auto-generated method stub
		for(int i = 0; i < currH.affectingCategories.size();i++){
			if(choiceCombination.get(currH.affectingDimensionName) == currH.affectingCategories.get(i)){
				return currH.affectingCategories.get(i);
			}
			if(choiceCombination.get(currH.affectingDimensionName) == Integer.parseInt(currAgent.myAttributes.get(currH.affectingDimensionName))){
				return Double.parseDouble(currAgent.myAttributes.get(currH.affectingDimensionName));
			}
		}
		return (Double) null;
	}
	
	public String getConstantName(){
		String constantName = new String();
		
		if(choiceCombination.get(UtilsTS.nAct) == 0){
			constantName = "C_HOME";
		}
		else if(choiceCombination.get(UtilsTS.nAct)!= 0 && choiceCombination.get(UtilsTS.fidelPtRange)==0){
			constantName = "C_NOT_PT_RIDER";
		}
		else if(choiceCombination.get(UtilsTS.nAct)!=0 && choiceCombination.get(UtilsTS.fidelPtRange)!=0){
			constantName = "C";
			for(String key: choiceCombination.keySet()){
				constantName+= "_"+choiceCombination.get(key);
			}
		}
		return constantName;
	}
	
	public String toString(){
		String answer = Integer.toString(biogeme_id);
		for(String key: choiceCombination.keySet()){
			answer+= Utils.Utils.COLUMN_DELIMETER + key + Utils.Utils.COLUMN_DELIMETER + choiceCombination.get(key);
		}
		return answer;
	}
}
