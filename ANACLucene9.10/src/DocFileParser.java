import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.hwpf.HWPFDocument; // HWPFDocument --> DOC files
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;  // XWPFDocument --> DOCX files
import org.apache.poi.xwpf.usermodel.XWPFDocument;  
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.collections4.*;

// Apache POI documentation: https://poi.apache.org/index.html

public class DocFileParser {
	
	 /**
     * This method parses the content of the .docx file and return all the text of the file passed to it.
     *
     * @param fileName: file name of which you want the content of
     * @return returns the content of the file in String
     */
    public static String DocxFileContentParser(String fileName)
    {
        // System.out.println("Filename in DocParser:" + fileName); // debug
       
        POIFSFileSystem fs = null;
        String extension = null;
        
        int index = fileName.lastIndexOf('.');
        
        if (index > 0) 
        {
          extension = fileName.substring(index + 1);
         //  System.out.println("File extension is " + extension); // debug
        }
        else
        {
        	return "";
        }
        
        if (extension.equals("docx"))
        {
        	try 
            {
                FileInputStream inputstream =  new FileInputStream(new File(fileName));
                XWPFDocument file   = new XWPFDocument(OPCPackage.open(inputstream));  
                XWPFWordExtractor ext = new XWPFWordExtractor(file);  
                
                String content  = ext.getText();
                
                // System.out.println("WordExtractor:" + content); // debug
                
                ext.close();
                
                return content; 
            }
            catch (Exception e) 
            {
            	System.out.println("Exception in DocFileContentParser (docx file): " + e.getMessage());
            }
        }
        
        if (extension.equals("doc"))
        {
        	try 
            {
	        	FileInputStream inputstream =  new FileInputStream(new File(fileName));
	            
	            fs = new POIFSFileSystem(inputstream);
	            // HWPFDocument doc = new HWPFDocument(fs); 
	            // System.out.println("HWPFDocument:" + doc);
	            WordExtractor we = new WordExtractor(fs);
	            String content = we.getText();
	            
	            // System.out.println("WordExtractor:" + content);
	            
	            we.close();
	            
	            return content;
            }
        	catch (Exception e) 
        	{
            	System.out.println("Exception in DocFileContentParser (doc file): " + e.getMessage());
            }
        }
        
        
        return "";
    }

}
