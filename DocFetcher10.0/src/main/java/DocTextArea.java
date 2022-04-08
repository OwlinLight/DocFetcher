import org.apache.lucene.document.Document;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class DocTextArea extends JTextArea {


    public void ShowContents(List<Document> docs, int number) throws IOException, TikaException {

        File file = new File(docs.get(number).get("path"));
        Tika tika = new Tika();

        //textArea.append("\n\n");
        this.append(tika.parseToString(file).trim());
    }

    public void appendContents_fact(String query_name) throws IOException, TikaException {
        String input_path = "./quick_fact/"+ query_name + ".txt";
        Tika tika = new Tika();
        this.append("\n\n");
        this.append("QUICK FACT\n");
        this.taHighlighter("QUICK FACT");
        File file = new File(input_path);
        if(file.exists())
            this.append(tika.parseToString(Paths.get(input_path)).trim());
        else
            file.createNewFile();
    }

    public void taHighlighter(String keyWord){
        Highlighter highLighter = this.getHighlighter();
        String text = this.getText();
        DefaultHighlighter.DefaultHighlightPainter p = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        int pos = 0;
        while ((pos = text.indexOf(keyWord, pos)) >= 0){
            try{
                highLighter.addHighlight(pos, pos + keyWord.length(), p);
                pos += keyWord.length();
            } catch (BadLocationException e){
                e.printStackTrace();
            }
        }
    }
    public static String getClipboardString() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable trans = clipboard.getContents(null);

        if (trans != null) {
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
                    return text;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
