import java.io.*;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class for public tenders (procurement)
 */
public class Procurement 
{
	// Folder containing the input CSV with terms to be found
	private static String inputDir = "/Volumes/SAMSUNG-DOC/PhD Informatica 2021/Lucene - Ricerca testi/input"; 
	
	// CSV
	private static String inputFileCIG = "cig_vs_appaltanti.csv"; // [0] bando_cig / CIG
	private static String inputFileAppCF = "cf_vs_appaltanti.csv"; 	// [1] bando_cig / CF
	private static String inputFileAppDe = "denominazioni_vs_appaltanti.csv"; // [2] stazione_appaltante / denominazione
	private static String inputFileAggDe = "denominazioni_vs_aggiudicatari.csv"; // [3] aggiudicatari / denominazione
	private static String inputFileAggCF = "cf_vs_aggiudicatari.csv"; // [4] aggiudicatari / cf
	
	/**
	 * Get the list of terms to be found following the research type
	 * @input researchType
	 * @return list of terms
	 */
	// public static List<String> getProcurementData(int data)
	public static List<String> getProcurementData(Research researchType)
	{
		String inputPath = null; 
		
		if (researchType.getResearchType() == ResearchType.CIG) 
		{
			inputPath = inputDir + "/" + inputFileCIG;
		}
		
		if (researchType.getResearchType() == ResearchType.APP_CF) 
		{
			inputPath = inputDir + "/" + inputFileAppCF;
		}
		
		if (researchType.getResearchType() == ResearchType.APP_DEN) 
		{
			inputPath = inputDir + "/" + inputFileAppDe;
		}
		
		if (researchType.getResearchType() == ResearchType.AGG_CF) 
		{
			inputPath = inputDir + "/" + inputFileAggCF;
		}
		
		if (researchType.getResearchType() == ResearchType.AGG_DEN) 
		{
			inputPath = inputDir + "/" + inputFileAggDe;
		}
		
		if (inputPath!=null)
		{
			try 
			{
				List<String> procurementList =  Files.readAllLines(Paths.get(inputPath));
				
				// procurementList.remove(0); // remove element(0) if it's the header
				
				// debug
				/*
				for (String line : procurementList) 
				{
					System.out.println(line);
				}
				*/
				return procurementList;
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
		
	}
	
	
} 
