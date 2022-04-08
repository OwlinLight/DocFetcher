import javax.swing.text.TextAction;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class quickfact_func{

    public static void add(String query) {
        //operations after getting the ClipboardString
        try {
            addToQuickFact(query);
        } catch (IOException ex) {
            ex.printStackTrace();
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

    public static void addToQuickFact(String query_name) throws IOException {
        String output_path = "./quick_fact/"+ query_name + ".txt";
        String fact;
        fact = getClipboardString() + "\n";
        FileWriter fw = new FileWriter(output_path, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(fact);
        bw.close();
        fw.close();
    }

}

