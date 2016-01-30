/**
 * 
 */
package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Antoine
 *
 */
public class Writer {

    public BufferedWriter myFileWritter;

    public void OpenFile(String fileName)
    {
        FileWriter fstream = null;
        try {
            fstream = new FileWriter(fileName);
            myFileWritter = new BufferedWriter(fstream);
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void CloseFile() throws IOException
    {
        myFileWritter.close();
    }

    public void WriteToFile(String currOutput) throws IOException
    {
        myFileWritter.write(currOutput);
        myFileWritter.newLine();
    }
}