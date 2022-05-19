/**
 * 
 * @author robertonai
 *
 */

// Librerie Java standard
import java.io.*;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Classe dei bandi pubblici
 * @author robertonai
 *
 */
public class Procurement 
{
	// Cartella dei CSV contenenti i termini da ricercare
	private static String inputDir = "/Volumes/SAMSUNG-DOC/PhD Informatica 2021/Lucene - Ricerca testi/input"; 
	
	// File CSV
	private static String inputFileCIG = "cig_vs_appaltanti.csv"; // [0] bando_cig / CIG
	private static String inputFileAppCF = "cf_vs_appaltanti.csv"; 	// [1] bando_cig / CF
	private static String inputFileAppDe = "denominazioni_vs_appaltanti.csv"; // [2] stazione_appaltante / denominazione
	// private static String inputFileAppDe = "denominazioni_vs_appaltanti_TEST.csv";  // [2] stazione_appaltante / denominazione
	private static String inputFileAggDe = "denominazioni_vs_aggiudicatari.csv"; // [3] aggiudicatari / denominazione
	private static String inputFileAggCF = "cf_vs_aggiudicatari.csv"; // [4] aggiudicatari / cf
	
	/**
	 * Estrare la lista dei dati dal csv
	 * @input data (0 --> CIG, 1 --> CF)
	 * @return lista dei dati (CIG o CF)
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
				
				// procurementList.remove(0); // se l'elemento 0 contiene l'intestazione viene rimosso
				
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
	
	
} // fine classe
