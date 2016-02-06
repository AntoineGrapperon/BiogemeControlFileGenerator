/**
 * 
 */
package Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Antoine
 *
 */
public class Reader {


   public BufferedReader myFileReader;
   LineNumberReader reader;

   public void OpenFile(String fileName)
   {
       FileReader fstream = null;
       try
       {
    	   fstream = new FileReader(fileName);
           myFileReader = new BufferedReader(fstream);
           reader = new LineNumberReader(myFileReader);
        } 
       catch (IOException ex)
       {
    	   Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
   
   public void CloseFile() throws IOException
   {
       myFileReader.close();
   }

   public String GetNextRow() throws IOException
   {
       return myFileReader.readLine();
   }
   
   public ArrayList<String> StoreLineByLine() throws IOException
   {
   	String str;
       ArrayList<String> Line = new ArrayList<String>();
       	
       while ((str = myFileReader.readLine()) != null) 
       {
       	Line.add(str);
       }
       myFileReader.close();
       return Line;
    }
}
