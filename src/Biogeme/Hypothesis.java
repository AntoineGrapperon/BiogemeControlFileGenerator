/**
 * 
 */
package Biogeme;

import java.util.ArrayList;

/**
 * @author Antoine
 *
 */
public class Hypothesis {


/**
 * 
 */

	String coefName;
	String affectedDimensionName;
	ArrayList<Integer> affectedCategories;
	String affectingDimensionName;	
	ArrayList<Integer> affectingCategories;
	
	public Hypothesis(){
		
	}
	
	public void setCoefName(String name){
		coefName = name;
	}
	
	public void setAffectedDimension(String dimName, ArrayList<Integer> categories){
		affectedDimensionName = dimName;
		affectedCategories = categories;
	}
	
	public void setAffectingDimension(String dimName, ArrayList<Integer> categories){
		affectingDimensionName = dimName;
		affectingCategories = categories;
	}
}
