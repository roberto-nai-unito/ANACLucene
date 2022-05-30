// Librerie Lucene 9.1.0
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
// import org.apache.lucene.index.DirectoryReader;
// import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
// import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.search.Explanation;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
// import org.apache.lucene.search.TopDocs; // attualmente non utilizzata
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

// Librerie Java standard
import java.io.*;
import java.util.ArrayList;
//import java.util.List;

/**
 * 
 * Main class indexing files with Lucene and calling up searches
 *
 */
public class TextFileIndexer 
{
	
	private static StandardAnalyzer analyzer = new StandardAnalyzer(); // --> IndexWriterConfig
	
	private static int fileExc = 0; // counter of files excluded from indexing
	
	// ArrayList containing the list of text files (to be indexed)
    private ArrayList<File> queueTxt = new ArrayList<File>();
    
    //  ArrayList containing the list of doc/docx files (to be indexed)
    private ArrayList<File> queueDoc = new ArrayList<File>();
    
    // ArrayList containing the list of pdf files (to be indexed)
    private ArrayList<File> queuePdf = new ArrayList<File>();
    
	// Instance variable (Lucene)
    private IndexWriter writer; // --> IndexWriter
    
    private final static int SCORETOP = 25; // limit to best scores (get the firs best SCORETOP scores)
    
    private final static double SCORELIMIT = 0; // limit below which not to consider the result

	public static void main(String[] args) throws IOException 
	{
		// Folder where Lucene will save file indexing (sentences)
		String indexLocation = "/Volumes/SAMSUNG-DOC/PhD Informatica 2021/Lucene - Ricerca testi/sentences_txt/Lucene-Index"; 
		
		// Folder containing the files to be indexed (the sentences folders)
        String fileLocation = "/Volumes/SAMSUNG-DOC/PhD Informatica 2021/Python - Giustizia Amministrativa v2/sentences";

        // Creating the index file
        TextFileIndexer indexer = null;
        
        // Start indexing (0 = no, 1 = yes)
        int indexing = 1;
        
        // Start search (0 = no, 1 = yes)
        int searching = 0;
        
        
        // Type of data sought, see Research and ResearchType
        Research researchType = new Research(ResearchType.APP_DEN); 
        
        // 1 - INDEXING
        if (indexing == 1)
        {
        	System.out.println("");
            System.out.println("************************");
            System.out.println("1 - INDEXING TASK");
            System.out.println("");
            long startI = System.currentTimeMillis();
            System.out.println("Timing initial: " + startI);
            System.out.println("");
            
            try 
            {
            	// 1.1 Create the index file
                indexer = new TextFileIndexer(indexLocation); 
            } 
            catch (Exception e) 
            {
                System.out.println("Cannot create index file: " + e.getMessage());
                System.exit(-1);
            }  
            try 
            {              
                // 1.1 Indexing files (the sentences files)
                indexer.indexFileOrDirectory(fileLocation);  
            } 
    		catch (Exception e) 
            {
    			System.out.println("Error indexing the file \"" + fileLocation + "\": " + e.getMessage());
            }
            
            // 1.3 Always call closeIndex, otherwise the index is not created
            indexer.closeIndex();
            
            // timing to finish
            long endI = System.currentTimeMillis();
        	// finding the time difference
            float diffI = endI - startI;
            // converting it into seconds
            float secI = diffI / 1000F;
            // converting it into minutes
            float minI = secI / 60F;
            
            System.out.println("Total time to generate index: " + minI + " minutes (" + secI + " seconds)");
            System.out.println("");
        }
        
        
        // 2 - SEACRH
        if (searching == 1)
        {
        	TextFileSearcher.searchPerform(researchType, indexLocation, SCORETOP, analyzer, SCORELIMIT);
        }
        
        System.out.println("");
        System.out.println("Program ended");
        System.out.println("");
	} // main()
	
	
	/**
     * Constructor
     * 
     * @param indexDir: path to the folder where the index is saved
     * @throws java.io.IOException in case of exceptions in the creation of the index
     */
    TextFileIndexer(String indexDir) throws IOException 
    {
        
        FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        writer = new IndexWriter(dir, config);
        
    }
	
	 /**
     * Indexing files in a folder
     * 
     * @param fileName the name of a text file or a folder we wish to add to the index
     * @throws java.io.IOException when exception occurs
     */
    public void indexFileOrDirectory(String fileName) throws IOException 
    {
    	int i = 0;
    	
        // obtains the list of files in a folder
        addFiles(new File(fileName));
        
        System.out.println("");
        System.out.println("File indexed from the input directory: " + queueTxt.size());
        System.out.println("");
        System.out.println("File exluded from the input directory: " + fileExc);
        System.out.println("");
        
        int originalNumDocs = writer.getDocStats().numDocs; // already indexed documents
        
        System.out.println("Documents already indexed: " + originalNumDocs);
        System.out.println("");
        
        // indexing TXT files
        for (File f : queueTxt) 
        {
            FileReader fr = null;
            try 
            {
                Document doc = new Document();
                
                // Contents of a file to be indexed
                fr = new FileReader(f);
                doc.add(new TextField("contents", fr));
                doc.add(new StringField("path", f.getPath(), Field.Store.YES));
                doc.add(new StringField("filename", f.getName(), Field.Store.YES));

                writer.addDocument(doc);
                i++;
                System.out.println(i+") Added to index file: " + f);
            } 
            catch (Exception e) 
            {
                System.out.println("Could not add to index file: " + f);
            } 
            finally 
            {
                fr.close();
            }
        }
        
        // indexing DOC / DOCX files
        for (File f : queueDoc) 
        {
            try 
            {
                Document doc = new Document();
                 
                // Contents of a file to be indexed
                String text = DocFileParser.DocxFileContentParser(f.getAbsolutePath()); // get text from DOC / DOCX
                doc.add(new TextField("contents", text, Field.Store.YES));
                doc.add(new StringField("path", f.getPath(), Field.Store.YES));
                doc.add(new StringField("filename", f.getName(), Field.Store.YES));

                writer.addDocument(doc);
                i++;
                System.out.println(i+") Added to index file: " + f);
            } 
            catch (Exception e) 
            {
                System.out.println("Impossible to add file doc to index file: " + f + "\n" + "Error: " + e.getMessage());
            } 
        }
        

        int newNumDocs = writer.getDocStats().numDocs; // documents added to the index
        
        System.out.println("");
        System.out.println("Number of files TXT to be indexed: " + queueTxt.size());
        System.out.println("");
        System.out.println("Number of files DOC to be indexed: " + queueDoc.size());
        System.out.println("");
        System.out.println("Number of files PDF to be indexed: " + queuePdf.size());
        System.out.println("");
        System.out.println("Number of files indexed: " + (newNumDocs - originalNumDocs));
        System.out.println("************************");

        queueTxt.clear();
    }

    /**
     * Recursive method that adds the files present in the folder to be indexed to the queue list
     * 
     * @param file: folder or file name
     */
    
    private void addFiles(File file) 
    {
    	
        if (!file.exists()) 
        {
            System.out.println(file + " does not exist.");
        }
        
        if (file.isDirectory()) 
        {
            for (File f : file.listFiles()) 
            {
                addFiles(f);
            }
        } 
        else 
        {
            String filename = file.getName().toLowerCase();
            int ok = 0;
            
            if (!filename.startsWith("._")) // avoid MacOS temporary files
            { 
            	 // Text files
                if (filename.endsWith(".htm") || filename.endsWith(".html") || filename.endsWith(".txt")) 
                {
                    queueTxt.add(file);
                    ok = 1;
                }
                
                // Word files
                if (filename.endsWith(".doc") || filename.endsWith(".docx")) 
                {
                    queueDoc.add(file);
                    ok = 1;
                }
                
                // PDF files
                if (filename.endsWith(".pdf")) 
                {
                    queuePdf.add(file);
                    ok = 1;
                }
                
                if (ok == 0)
                {
                	fileExc++;
                    System.out.println(fileExc + ") File skipped (not in htm / txt / doc (docx) / pdf) from indexing: \"" + filename +"\"");
                } 
            }
        }
    }

    /**
     * Closes the index (mandatory to generate the index)
     * 
     * @throws java.io.IOException 
     */
    public void closeIndex() throws IOException 
    {
        writer.close();
    }
    

} // end class TextFileIndexer
