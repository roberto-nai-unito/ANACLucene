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
	public static void searchPerform(Research researchType, String indexLocation, int scoretop, StandardAnalyzer analyzer, double scorelimit)
	{

		// 2 - RICERCA
        
        System.out.println("");
        System.out.println("************************");
        System.out.println("2 - QUERY TASK");
        System.out.println("");
        
        // 2.1 - Estrazione dei dati da cercare
        
        // List<String> procurementList = Procurement.getProcurementData(data_in); 
        List<String> procurementList = Procurement.getProcurementData(researchType); 
        System.out.println("Number of terms to be searched: " + procurementList.size());
        System.out.println("");
        
        // 2.2 ricerca in Lucene
       
        List<Result> resultList = new ArrayList<Result>(); 
        
        long startI = System.currentTimeMillis();
        System.out.println("Timing initial (ms): " + startI);
        System.out.println("");
        
        // ricerca su più termini
        int j = 0; // contatore della posizione della ricerca
                
        for (String searchTerm : procurementList) 
		{
        	j++;
        	System.out.println("");
			System.out.println(j + ") Searched term: " + searchTerm);
	        System.out.println("");
	        try 
	        {
	        	// Attiva gli oggetti per la ricerca
	        	IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation).toPath()));
	            IndexSearcher searcher = new IndexSearcher(reader);
	            TopScoreDocCollector collector = TopScoreDocCollector.create(scoretop, Integer.MAX_VALUE); 
	            // i primi 3 top score fino al massimo (l'originale era 5,20)
	            
	        	// Esegue la query sul termine
	        	Query q = new QueryParser("contents", analyzer).parse(searchTerm);
		        searcher.search(q, collector);
		        ScoreDoc[] hits = collector.topDocs().scoreDocs;
		        
		        
		        // Visualizza i risultati
		        
		        System.out.println("Hits found: " + hits.length);
		        // System.out.println("Total hits (overall): " + collector.getTotalHits());
		        
		        for(int i=0; i<hits.length;++i) 
		        {
		            int docId = hits[i].doc;
		            Document d = searcher.doc(docId);
		            // Explanation ex = searcher.explain(q, docId);
		            // ex.getDescription() --> spiegazione
		            // ex.getValue() --> score
		            // ex.toString() --> spiegazione completa
		            System.out.println((i + 1) + ") " + d.get("path") + " | score=" + hits[i].score); 
		            Result r = new Result(searchTerm,d.get("path"),hits[i].score);
		            resultList.add(r);
		        }
		        
	        }
	        catch (Exception e) 
	        {
				System.out.println("Error searching \"" + searchTerm + "\": " + e.getMessage() + " - " + e.getLocalizedMessage());
	        }
		}
		
        long endI = System.currentTimeMillis();
        
        System.out.println("");
        System.out.println("-----------");
        System.out.println("");
        System.out.println("Timing final (ms): " + endI);
        System.out.println("");
        System.out.println("Total time to search: " + (endI - startI) / (100 * 60));
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
        int rows = Result.resultCSV(resultList, researchType, scorelimit);
        System.out.println("Rows written from list to CSV file: " + rows);
		System.out.println("");
		System.out.println("End process");
		System.out.println("");
        System.out.println("************************");
	}
}
