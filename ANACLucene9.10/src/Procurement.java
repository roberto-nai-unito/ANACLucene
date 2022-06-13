import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class for public tenders (procurement)
 */
public class Procurement 
{

	private String cig;
	private String fiscalCode;
	private String denomination;
	
	// Folder containing the input CSV with terms to be found
	private static String inputDir = "/Volumes/SAMSUNG-DOC/PhD Informatica 2021/Lucene - Ricerca testi/input"; 
	
	// CSV
	private static String inputFileCIG = "cig.csv"; 	// Tender ID (CIG)
	private static String inputFileApp = "appaltanti.csv"; 			// Public entities
	private static String inputFileAgg = "aggiudicatari.csv"; 		// Private companies
	
	/**
	 * Get the list of terms to be found following the research type
	 * @input researchType
	 * @return list of terms
	 */
	// public static List<String> getProcurementData(int data)
	public static List<Procurement> getProcurementData(Research researchType)
	{
		String inputPath = null; 
		
		if (researchType.getResearchType() == ResearchType.CIG) 
		{
			inputPath = inputDir + "/" + inputFileCIG;
		}
		
		if (researchType.getResearchType() == ResearchType.APP_CF) 
		{
			inputPath = inputDir + "/" + inputFileApp;
		}
		
		if (researchType.getResearchType() == ResearchType.APP_DEN) 
		{
			inputPath = inputDir + "/" + inputFileApp;
		}
		
		if (researchType.getResearchType() == ResearchType.AGG_CF) 
		{
			inputPath = inputDir + "/" + inputFileAgg;
		}
		
		if (researchType.getResearchType() == ResearchType.AGG_DEN) 
		{
			inputPath = inputDir + "/" + inputFileAgg;
		}
		
		if (inputPath!=null)
		{
			List<Procurement> procurementList = new ArrayList<Procurement>();
					
			try 
			{
				// List<String> procurementList = Files.readAllLines(Paths.get(inputPath));
				List<String> allLines =  Files.readAllLines(Paths.get(inputPath));
				
				for (String line : allLines) 
				{
					// System.out.println("Line: "+ line); // debug
					if (researchType.getResearchType() == ResearchType.CIG)
					{
						String cig = line;
						Procurement p = new Procurement(cig);
						procurementList.add(p);
					}
					else
					{
						String[] temp = line.split(";");
						if (temp.length==2) // if it is not 2 bad data
						{
							String cf = temp[0];
							String denomination = temp[1];
							Procurement p = new Procurement(cf, denomination);
							procurementList.add(p);
						}
					}
				}
				
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

	public String getCig() {
		return cig;
	}

	public void setCig(String cig) {
		this.cig = cig;
	}

	public String getFiscalCode() {
		return fiscalCode;
	}

	public void setFiscalCode(String fiscal_code) {
		this.fiscalCode = fiscal_code;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	
	public Procurement(String cig) 
	{
		super();
		this.cig = cig;
		this.fiscalCode = null;
		this.denomination = null;
	}
	
	public Procurement(String fiscalCode, String denomination) 
	{
		super();
		this.fiscalCode = fiscalCode;
		this.denomination = denomination;
		this.cig = null;
	}
	
	
} 

