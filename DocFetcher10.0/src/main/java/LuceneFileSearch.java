
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import IKAnalyzerIntegerated.IKAnalyzer4Lucene7;
import org.apache.james.mime4j.dom.Body;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class LuceneFileSearch {

    private Directory indexDirectory;
    private Analyzer analyzer;

    public LuceneFileSearch(Directory fsDirectory, StandardAnalyzer analyzer) {
        super();
        this.indexDirectory = fsDirectory;
        this.analyzer = analyzer;
    }

    public LuceneFileSearch(Directory fsDirectory, IKAnalyzer4Lucene7 analyzer) {
        super();
        this.indexDirectory = fsDirectory;
        this.analyzer = analyzer;
    }

    public void addDirToIndex(String Dirpath) throws IOException, TikaException, SAXException {
        Path path = Paths.get(Dirpath);
        File file = path.toFile();
        File files[] = file.listFiles();
        if(file.isDirectory()){
            for(File f : files){
                this.addFileToIndex(f.getCanonicalPath());
            }
        }
        else
            this.addFileToIndex(file.getCanonicalPath());
    }

    public void addFileToIndex(String filepath) throws IOException, TikaException, SAXException {

        Path path = Paths.get(filepath);
        File file = path.toFile();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
        Document document = new Document();

        Tika tika = new Tika();
        document.add(new TextField("contents", tika.parseToString(file), Field.Store.YES));
        document.add(new StringField("path", file.getPath(), Field.Store.YES));
        document.add(new TextField("filename",file.getName(), Field.Store.YES));

        indexWriter.addDocument(document);

        indexWriter.close();
    }

    public List<Document> searchFiles(String inField, String queryString) {
        try {
            Query query = new QueryParser(inField, analyzer).parse(queryString);
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(indexReader);
            TopDocs topDocs = searcher.search(query, 10);
            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearIndex() throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
        indexWriter.deleteAll();
        indexWriter.close();
    }

}


