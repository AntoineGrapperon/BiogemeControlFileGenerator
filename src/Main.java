import java.io.IOException;

import Biogeme.ControlFileGenerator;
import Utils.Dictionnary;

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
public class Main {

	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ControlFileGenerator biogeme;
		biogeme = new ControlFileGenerator();
		String pathControlFile =Dictionnary.WRK_DIR + "\\ctrl\\biogeme_ctrl_file.txt";
		String pathOutput = Dictionnary.WRK_DIR + "\\biogeme\\test.mod";
		String pathHypothesis = Dictionnary.WRK_DIR + "\\ctrl\\biogeme_hypothesis_desc.txt";
		try {
			biogeme.generateBiogemeControlFile(pathControlFile, pathOutput, pathHypothesis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}