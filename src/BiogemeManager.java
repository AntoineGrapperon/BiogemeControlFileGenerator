import java.io.IOException;

import Biogeme.BiogemeControlFileGenerator;
import Biogeme.BiogemeSimulator;
import Utils.Utils;

/**
 * The Control File Generator for Biogeme project was made to generate control file for the open source software BIOGEME.
 * Biogeme is an open source freeware designed for the maximum likelihood estimation of parametric models in general, with a special emphasis on discrete choice models. 
 * Control file for joint models can be exhausting to produce. The BIogemeControlFileGenerator software was made to make it easier.
 * Biogeme is available in various versions here: http://biogeme.epfl.ch/home.html
 */

/**
 * @date 28 January 2016
 * @author Antoine
 *
 */
public class BiogemeManager {

	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();
		BiogemeControlFileGenerator myCtrlGenerator;
		myCtrlGenerator = new BiogemeControlFileGenerator();
		BiogemeSimulator mySimulator;
		String pathControlFile =Utils.WRK_DIR + "test\\biogeme_ctrl_file.txt";
		String pathOutput = Utils.WRK_DIR + "test\\test.mod";
		String pathHypothesis = Utils.WRK_DIR + "test\\biogeme_hypothesis_desc.txt";
		try {
			myCtrlGenerator.initialize(pathControlFile, pathOutput, pathHypothesis);
			myCtrlGenerator.generateBiogemeControlFile();
			//BE CAREFUL : HYPOTHESIS SHOULD NOT BE CHANGED, HOWEVER IT IS IMPORTANT TO EDIT THE CONTROL FILE
			//BEFORE CALIBRATING THE MODEL WITH BIOGEME : THE FIXED PARAMETER SHOULD BE CHOOSEN, DUMMIES SHOULD SPECIFIED
			//AND 
			
			mySimulator = new BiogemeSimulator(myCtrlGenerator);
			mySimulator.initialize(Utils.WRK_DIR + "test\\data.csv");
			mySimulator.importBiogemeModel(Utils.WRK_DIR + "test\\model.F12");
			mySimulator.applyModelOnTravelSurveyPopulation(Utils.WRK_DIR + "test\\simulationResults.csv");
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("--process ended: "+ (endTime-startTime) + "ms");
	}
}