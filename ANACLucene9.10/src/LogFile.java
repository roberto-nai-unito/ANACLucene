
/**
 * 
 * Class with log data
 *
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;  

public class LogFile {
	
	private static final String LOGPATH = "log";
	private static final String SEPARATOR = System.getProperty("file.separator");
	
	public static String logInit(String taskType, String date)
	{
		String filename = date + "_log_" + taskType +  ".txt";
		
		String path = getExecution() +  SEPARATOR + LOGPATH + SEPARATOR + filename;
		
		try 
		{
			File myObj = new File(path);
			
		    if (myObj.createNewFile()) 
		    {
		        System.out.println("Log file created: " + myObj.getName());
		        return filename;
		    } 
		    else 
		    {
		    	System.out.println("File already exists.");
		    	return null;
		    }
		} 
		catch (IOException e) 
		{
			System.out.println("logInit(): An error occurred trying to create the log file with path \"" + path + "\"");
			System.out.println("Sistem error: \"" + e.getMessage() + "\"");
		    e.printStackTrace();
		    System.out.println("");
		    return null;
		}
	}
	
	public static void logWrite(String filename, String taskData)
	{
		String path = getExecution() + SEPARATOR + LOGPATH + SEPARATOR + filename;
		 try 
		 {
	         FileWriter fileWritter = new FileWriter(path,true);
	         BufferedWriter bw = new BufferedWriter(fileWritter);
	         bw.write(taskData);
	         bw.newLine();
	         bw.close();
	         //System.out.println("Done");
	      } 
		 catch(IOException e)
		 {
			System.out.println("logWrite(): an error occurred trying to append data in the log file with path \"" + path + "\"");
			System.out.println("Sistem error: \"" + e.getMessage() + "\"");
			e.printStackTrace();
			System.out.println("");
	     }
	}
	
	private static String getExecution()
	{
		String executionPath = null;
		
		try 
		{
			executionPath =  new File(TextFileIndexer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			// System.out.println(executionPath); // debug
			
		} catch (URISyntaxException e) {
			System.out.println("getExecution(): an error occurred trying to get execution path");
			System.out.println("Sistem error: \"" + e.getMessage() + "\"");
			e.printStackTrace();
			System.out.println("");
		}
		return executionPath;
	}

}
