import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Class Results: collects the results from the Lucene query
 *
 */
public class Result 
{
	// Folder with CSV search results
	private static String outputDir = "/Volumes/SAMSUNG-DOC/PhD Informatica 2021/Lucene - Ricerca testi/output";
	private static String outputFileName = "results";
	private static String outputFileExt = "csv";
	private final static String CSVSEPARATOR = ";";
	
	// Instance variables	
	private String term; 	// search terms
	private String file;	// file where the term was found
	private double score; 	// Lucene score
	
	// Costruttore
	public Result(String term, String file, double score) {
		super();
		this.term = term;
		this.file = file;
		this.score = score;
	}
	
	// Getter e Setter
	public double getScore() 
	{
		return score;
	}

	public void setScore(double score) 
	{
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "Result [term=" + term + ", file=" + file + ", score=" + score + "]";
	}
	
	/**
	 * String in CSV format
	 * @input researchType: research type
	 * @return String in CSV format
	 */
	public String toCSV(Research researchType) 
	{
		return researchType.toString()+CSVSEPARATOR+term+CSVSEPARATOR+fileToFileName(file,2)+CSVSEPARATOR+fileToFileName(file,1)+CSVSEPARATOR+scoreRound(score)+System.lineSeparator();
	}
	
	/**
	 * Given a complete path, extract filename and folder of the found term
	 * 
	 * @slice location of the value to be extracted; 1 the file name, 2 the folder
	 * @return string containing the file name (of the judgment)
	 */
	public static String fileToFileName(String file, int slice)
	{
		try 
		{
			String[] parts = file.split("/");
			String name = parts[parts.length-slice];
			return name;
		} 
		catch (Exception e) 
		{
			System.out.println("Exception in fileToFileName: " + e.getMessage());
			System.out.println("");
		}
		return null;
	}
	
	/**
	 * Given a double number, returns the rounded number
	 * 
	 * @param n: number in input
	 * @return formatted string of the number in input
	 */
	public static String scoreRound(Number n)
	{
		DecimalFormat df = new DecimalFormat("#.###");
		Double d = n.doubleValue();
	    return df.format(d);
	}
	
	
	/**
	 * Given a list of results, save on CSV file the data
	 * 
	 * @param resultList: results list
	 * @param researchType: search type (enum)
	 * @param scoreLimit: score within which to exclude the result
	 * @return number of lines written in the CSV file
	 */
	public static int resultCSV(List<Result> resultList, Research researchType, double scoreLimit)
	{
		String fileName = outputDir + "/" + outputFileName + "_" + researchType.toString() + "." + outputFileExt;
		
		int i = 0; // written lines counter
		int j = 0; // number of items excluded (score < scoreLimit)
		
		// delete the file if it already exists 
		File file = new File(fileName);
		try 
		{
			boolean resultDel = Files.deleteIfExists(file.toPath());
			if (resultDel)
			{
				System.out.println("File already existing: \"" + file.toPath() +"\" deleted");
				System.out.println("");
			}
			
		} 
		catch (IOException e1) 
		{
			System.out.println("Exception in resultCSV --> Unable to delete file already existing \": " + file.toPath() +"\"");
			System.out.println("");
			e1.printStackTrace();
		}
		
		for (Result result : resultList) 
		{
			if (result.getScore() < scoreLimit)
			{
				j++;
				continue;
			}
			String csvLine = result.toCSV(researchType);
			
			// System.out.println(csvLine.toString()); // debug
			// System.out.println("");
			
			BufferedWriter writer;
			try 
			{
				writer = new BufferedWriter(new FileWriter(fileName, true)); // true is for append mode
				writer.write(csvLine);
				i++;
				writer.close(); // writes the content to the file
			} 
			catch (IOException e) 
			{
				System.out.println("Exception in resultCSV --> Saving data to CSV: " + e.getMessage());
				System.out.println("");
			}
		}
		
		System.out.println("Results excluded: " + j);
		System.out.println("");
		return i;
	}
	
	/**
	 * Returns the maximum score
	 * 
	 * @param resultList: results list
	 * @return maximum value
	 */
	public static double scoreMax(List<Result> resultList)
	{
		double maxScore = resultList.get(0).getScore();
		for (Result result : resultList) 
		{
			if (result.getScore() > maxScore)
			{
				maxScore = result.getScore();
			}
		}
		return maxScore;
	}
	
	/**
	 * Returns the minimum score
	 * 
	 * @param resultList: results list
	 * @return minimum value
	 */
	public static double scoreMin(List<Result> resultList)
	{
		double minScore = resultList.get(0).getScore();
		for (Result result : resultList) 
		{
			if (result.getScore() < minScore)
			{
				minScore = result.getScore();
			}
		}
		return minScore;
	}
	
}
