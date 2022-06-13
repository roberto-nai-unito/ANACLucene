// Librerie Lucene 9.1.0
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
// import org.apache.lucene.search.TopDocs; // attualmente non utilizzata
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

// Librerie Java standard
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextFileSearcher 
{
	/**
	 * 
	 * @param researchType
	 * @param indexLocation
	 * @param scoretop: value of the top scores included in the result
	 * @param analyzer
	 * @param scorelimit: score within which to exclude the result
	 */
	public static void searchPerform(Research researchType, String indexLocation, int scoretop, StandardAnalyzer analyzer, double scorelimit)
	{

		// 2 - SEARCH
        
        System.out.println("");
        System.out.println("************************");
        System.out.println("2 - QUERY TASK");
        System.out.println("");
        
        // 2.1 - Get terms to search
        
        // List<String> procurementList = Procurement.getProcurementData(researchType); 
        List<Procurement> procurementList = Procurement.getProcurementData(researchType); 
        System.out.println("Number of terms to be searched: " + procurementList.size());
        System.out.println("");
        
        // 2.2 Search in Lucene
       
        List<Result> resultList = new ArrayList<Result>(); 
        List<Result> resultListTemp = new ArrayList<Result>(); // tempoaray results in which delete duplicates
        
        long startI = System.currentTimeMillis();
        System.out.println("Timing initial (ms): " + startI);
        System.out.println("");
        
        int j = 0; // counter
                
        // for (String searchTerm : procurementList) 
        for (Procurement p : procurementList) 
		{
        	String searchTerm = null;		// Search to be searched in Lucene
        	String searchTermResult = null; // Result string (CF for APP or AGG, else CIG)
        	j++;
        	System.out.println("");
        	
        	if (researchType.getResearchType() == ResearchType.CIG)
        	{
        		searchTerm = p.getCig();
        		searchTermResult = p.getCig();
        	}
        	
        	if (researchType.getResearchType() == ResearchType.APP_CF)
        	{
        		searchTerm = p.getFiscalCode();
        		searchTermResult = p.getFiscalCode();
        	}
        	
        	if (researchType.getResearchType() == ResearchType.APP_DEN)
        	{
        		searchTerm = p.getDenomination();
        		searchTermResult = p.getFiscalCode();
        	}
        		
        	if (researchType.getResearchType() == ResearchType.AGG_CF)
        	{
        		searchTerm = p.getFiscalCode();
        		searchTermResult = p.getFiscalCode();
        	}
        	
        	if (researchType.getResearchType() == ResearchType.AGG_DEN)
        	{
        		searchTerm = p.getDenomination();
        		searchTermResult = p.getFiscalCode();
        	}
        	
			System.out.println(j + ") Searched term: " + searchTerm + " ("+searchTermResult+")");
	        System.out.println("");
	        try 
	        {
	        	// Search objects
	        	IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation).toPath()));
	            IndexSearcher searcher = new IndexSearcher(reader);
	            TopScoreDocCollector collector = TopScoreDocCollector.create(scoretop, Integer.MAX_VALUE); 
	            // numHits --> scoretop, totalHitsThreshold --> max ==> get all results whit score over scoretop
	            // numHits --> 5, totalHitsThreshold --> 20 ==> get first 20 results whit score over 5 (original)

	        	// Query  
	        	Query q = new QueryParser("contents", analyzer).parse(searchTerm); // QueryParser(Field, Analyzer)
		        searcher.search(q, collector);
		        ScoreDoc[] hits = collector.topDocs().scoreDocs;
		        
		        // Results
		        
		        System.out.println("Hits found: " + hits.length);
		        // System.out.println("Total hits (overall): " + collector.getTotalHits());
		        
		        for(int i=0; i<hits.length;++i) 
		        {
		            int docId = hits[i].doc;
		            Document d = searcher.doc(docId);
		            // Explanation ex = searcher.explain(q, docId);
		            // ex.getDescription() --> Explanation short
		            // ex.getValue() --> score
		            // ex.toString() --> Explanation long
		            System.out.println((i + 1) + ") " + d.get("path") + " | score=" + hits[i].score); 
		            
		            // save the result (CF for APP or AGG, else CIG)            
		            
		            Result r = new Result(searchTermResult,d.get("path"),hits[i].score);
		            
		            int checkR = Result.checkBestScore(resultListTemp, r); 
		            
		            if (checkR == 1)
		            {
		            	System.out.println("WARNING: term in same path with lower score found and deleted"); 
		            }
		            else
		            {
		            	// System.out.println("Term in same path already with lower score not found"); 
		            }
		          
		        }
		        
		        System.out.println("Distinct results that will be added for term \""+searchTermResult+"\": " + resultListTemp.size()); 
		        System.out.println("");
		        
		        for(Result r : resultListTemp)
		        {
		        	 // add the distinct results to list of results
			        resultList.add(r);
		        }
		        
		        resultListTemp.clear(); // empty the resultListTemp for the new query results
	        }
	        catch (Exception e) 
	        {
				System.out.println("Error searching \"" + searchTerm + "\": " + e.getMessage() + " - " + e.getLocalizedMessage());
	        }
		}
		
        long endI = System.currentTimeMillis();
        
        // finding the time difference
        float diffI = endI - startI;
        // converting it into seconds
        float secI = diffI / 1000F;
        // converting it into minutes
        float minI = secI / 60F;
        
        System.out.println("");
        System.out.println("-----------");
        System.out.println("");
        System.out.println("Timing final (ms): " + endI);
        System.out.println("");
        // System.out.println("Total time to search: " + (endI - startI) / (100 * 60));
        System.out.println("Total time to search: " + minI + " minutes (" + secI + " seconds)");
        System.out.println("");
        // System.out.println("Total hits over all the terms: " + collector.getTotalHits());
        // System.out.println("");
        System.out.println("Total results in list: " + resultList.size());
        System.out.println("");
        
        System.out.println("Min score result in list: " + Result.scoreMin(resultList));
        System.out.println("");
        
        System.out.println("Max score result in list: " + Result.scoreMax(resultList));
        System.out.println("");
       
        System.out.println("Writing results to file...");
		System.out.println("");
        int rows = Result.resultCSV(resultList, researchType, scorelimit, scoretop);
        System.out.println("Rows written from list to CSV file: " + rows);
		System.out.println("");
		System.out.println("End process");
		System.out.println("");
        System.out.println("************************");
	}
}
