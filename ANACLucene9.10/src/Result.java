import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Classe dei risultati
 * @author robertonai
 *
 */
public class Result 
{
	// Cartella con i CSV dei risultati della ricerca
	private static String outputDir = "/Volumes/SAMSUNG-DOC/PhD Informatica 2021/Lucene - Ricerca testi/output";
	private static String outputFileName = "results";
	private static String outputFileExt = "csv";
	private final static String CSVSEPARATOR = ";";
	
	// Variabili d'istanza	
	private String term; 	// termine cercato
	private String file;	// file in cui è stato trovato il termine
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
	 * 
	 * @return stringa in formato CSV
	 */
	public String toCSV(Research researchType) 
	{
		return researchType.toString()+CSVSEPARATOR+term+CSVSEPARATOR+fileToFileName(file,2)+CSVSEPARATOR+fileToFileName(file,1)+CSVSEPARATOR+scoreRound(score)+System.lineSeparator();
	}
	
	/**
	 * 
	 * @slice posizione del valore da estrarre; 1 il nome del file, 2 la cartella
	 * @return stringa contenente il nome del file (della sentenza)
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param n numero di partenza
	 * @return stringa formattata del numero di partenza
	 */
	public static String scoreRound(Number n)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		Double d = n.doubleValue();
	    return df.format(d);
	}
	
	
	/**
	 * 
	 * @param resultList lista dei risultati
	 * @param researchType tipo di ricerca (enum)
	 * @param scoreLimit score entro cui escludere il risultato
	 * @return numero di righe scritte nel file CSV
	 */
	public static int resultCSV(List<Result> resultList, Research researchType, double scoreLimit)
	{
		String fileName = outputDir + "/" + outputFileName + "_" + researchType.toString() + "." + outputFileExt;
		
		int i = 0; // contatore delle line scritte
		int j = 0; // numero di elementi esclusi (score basso)
		
		// cancella il file se esiste già 
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
			System.out.println("Unable to delete file already existing \": " + file.toPath() +"\"");
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
			// System.out.println(result.toString());
			// System.out.println("");
			BufferedWriter writer;
			try 
			{
				writer = new BufferedWriter(new FileWriter(fileName, true)); // true è per la modalità append
				writer.write(csvLine);
				i++;
				writer.close(); // scrive il contenuto sul file
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Results excluded: " + j);
		System.out.println("");
		return i;
	}
	
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
