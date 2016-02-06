/**
 * 
 */
package Biogeme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Utils.Reader;

/**
 * @author Antoine
 *
 */
public class BiogemeModelReader {
	Reader myInputDataReader = new Reader();
	//public HashMap<String, Object> myTravelSurvey;
	HashMap<String, ArrayList<Object>> myData = new HashMap<String, ArrayList<Object>>();
	
	public BiogemeModelReader(){
	}
	
	public void loadModel(String path) throws IOException{
		/*myInputDataReader.OpenFile(path);
		ArrayList <String> lines = myInputDataReader.StoreLineByLine();
		String[] list;
		int cur = 0;
		while(!(lines.get(cur).trim().equals("END"))){
			cur++;
		}
	 	while(!(lines.get(cur).trim().equals("-1"))){
	 		cur++;
	 		String[] strTok = lines.get(cur).split("\\t");
	 		String coefName = strTok[1].trim();
	 		double coefValue = Double.parseDouble(strTok[3]);
	 		//BiogemeSimulator.updateHypothesis(coefName,coefValue);
	 	}*/
	}
	

	
}
