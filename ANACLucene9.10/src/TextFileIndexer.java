// Librerie Lucene 9.10
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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

// Librerie Java standard
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextFileIndexer 
{
	
	// Variabile statica (di classe)
	private static StandardAnalyzer analyzer = new StandardAnalyzer(); // --> IndexWriterConfig
	
	private static int fileExc = 0; // contatore dei file esclusi dall'indicizzazione
	
	// Variabile d'istanza Java ArrayList contenente la lista dei file (da indicizzare) contenuti in una cartella
    private ArrayList<File> queue = new ArrayList<File>();
    
	// Variabile d'istanza (Lucene)
    private IndexWriter writer; // --> IndexWriter
    

	public static void main(String[] args) throws IOException 
	{
		// Cartella dove Lucene salverà l'indicizzazione
		String indexLocation = "/Volumes/SAMSUNG-DOC/PhD Informatica 2021/Lucene - Ricerca testi/sentences_txt/Lucene-Index"; 
		
		// Cartella contenente i file da indicizzare (le cartelle delle sentenze --> _Pagina_xxx)
        String fileLocation = "/Volumes/SAMSUNG-DOC/PhD Informatica 2021/Lucene - Ricerca testi/sentences_txt/_Pagina_0-500";

        // Creazione del file indice
        TextFileIndexer indexer = null;
        
        // Stringa contenente il testo da cercare
        String searchTerm = null;
        
        // 1 - INDICIZZAZIONE
        
        System.out.println("");
        System.out.println("************************");
        System.out.println("1 - INDEXING TASK");
        System.out.println("");
        
        try 
        {
        	// 1.1 Crea il file indice
            indexer = new TextFileIndexer(indexLocation); 
        } 
        catch (Exception e) 
        {
            System.out.println("Cannot create index..." + e.getMessage());
            System.exit(-1);
        }  
        try 
        {              
            // 1.1 Indicizza i file
            indexer.indexFileOrDirectory(fileLocation);  
        } 
		catch (Exception e) 
        {
			System.out.println("Error indexing " + fileLocation + " : " + e.getMessage());
        }
        
     
        // Richiamare sempre closeIndex, altrimenti l'indice non viene creato
        indexer.closeIndex();
        
        // 2 - RICERCA
        
        System.out.println("");
        System.out.println("************************");
        System.out.println("2 - QUERY TASK");
        System.out.println("");
        
        searchTerm = "Termoidraulica"; // @TODO-1: estrarre i searchTerm dai file CSV dei CIG e CF
        
        System.out.println("Searched term: " + searchTerm);
        System.out.println("");
     
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation).toPath()));
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(5, 20);
        
        try 
        {
        	Query q = new QueryParser("contents", analyzer).parse(searchTerm);
	        searcher.search(q, collector);
	        ScoreDoc[] hits = collector.topDocs().scoreDocs;
	
	        // Visualizza i risultati
	        
	        System.out.println("Hits found: " + hits.length);
	        System.out.println("");
	        for(int i=0;i<hits.length;++i) 
	        {
	            int docId = hits[i].doc;
	            Document d = searcher.doc(docId);
	            System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score); 
	            // @TODO-2: salvare i risultati su CSV nella forma searchTerm;_Pagina_xxx/file;score
	        }
        }
        catch (Exception e) 
        {
			System.out.println("Error searching " + searchTerm + " : " + e.getMessage());
        }
        
        System.out.println("************************");
        
		
	} // fine main()
	
	
	/**
     * Costruttore
     * @param indexDir percorso alla cartella dove salvare l'indice
     * @throws java.io.IOException in caso di eccezioni nella creazione dell'indice
     */
    TextFileIndexer(String indexDir) throws IOException 
    {
        
        FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        writer = new IndexWriter(dir, config);
        
    }
	
	 /**
     * Indicizza i file di una cartella
     * @param fileName the name of a text file or a folder we wish to add to the index
     * @throws java.io.IOException when exception
     */
    public void indexFileOrDirectory(String fileName) throws IOException 
    {
    	int i = 0;
    	
        // ottiene la lista dei file presenti in una cartella
        addFiles(new File(fileName));
        
        System.out.println("");
        System.out.println("File indexed from the input directory: " + queue.size());
        System.out.println("");
        System.out.println("File exluded from the input directory: " + fileExc);
        System.out.println("");
        

        int originalNumDocs = writer.getDocStats().numDocs; // documenti già indicizzati
        
        System.out.println("Documents already indexed: " + originalNumDocs);
        System.out.println("");
        
        for (File f : queue) {
            FileReader fr = null;
            try {
                Document doc = new Document();

                // Contenuti di un file
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

        int newNumDocs = writer.getDocStats().numDocs; // documenti aggiunti all'indice
        
        System.out.println("");
        System.out.println("Number of files indexed: " + (newNumDocs - originalNumDocs));
        // @TODO-0: inserire un timing per verificare il tempo impiegato ad indicizzare
        System.out.println("************************");

        queue.clear();
    }

    /**
     * Metodo ricorsivo che aggiunge alla lista queue i file presenti nella cartella da indicizzare
     * @param file nome della cartella o del file
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
            
            if (!filename.startsWith("._")) // evita i file temporanei di MacOS
            { 
            	 // solo file di testo
                if (filename.endsWith(".htm") || filename.endsWith(".html") || filename.endsWith(".xml") || filename.endsWith(".txt")) 
                {
                    queue.add(file);
                } 
                else 
                {
                	fileExc++;
                    System.out.println(fileExc + ") File skipped (not in htm / txt / xml format) from indexing: " + filename);
                } 
                // @TODO-3: verificare se e come indicizzare doc/docx e pdf
            }
        }
    }

    /**
     * Chiude l'indice
     * @throws java.io.IOException 
     */
    public void closeIndex() throws IOException 
    {
        writer.close();
    }

} // fine classe TextFileIndexer
