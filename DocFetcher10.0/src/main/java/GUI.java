import IKAnalyzerIntegerated.IKAnalyzer4Lucene7;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

public class GUI{
    private JPanel rootPanel;
    private JPanel panel1,panel1_sub;
    private JLabel label;
    private JPanel panel2;
    private JButton directoryButton,QuickFactButton;
    private JTextField textField;
    private JButton searchButton;
    private JPanel panel3;
    private JList list;
    private DocTextArea textArea;//customed text area
    private JPanel panel4;
    private JPanel panel5;
    private JFrame frame;
    private JComboCheckBox ccBox;
    private JCheckBox pdfCheckBox,txtCheckBox,docCheckBox,docxCheckBox,pptxCheckBox,xlsxCheckBox;
    private JFileChooser chooser;
    private File chosed_directory,chosed_file;
    private DefaultListModel listModel;
    private List<Document> docs;
    private JRadioButton rb_Name,rb_Content;
    private ButtonGroup group;
    private JPopupMenu pop_list,pop_textField;
    private JScrollPane sp_text,sp_list;
    //Lucene--Directory and Searching class
    String searchingField = "filename";
    String indexPath = "index";//indexPath: path to store the lucene index;
    String factPath = "fact_index";//datapath: path to search;
    String query;//to highlight the query keywords
    String searchingResult = null;

    Directory directory;
    LuceneFileSearch luceneFileSearch;

    public GUI() throws IOException{
        //initialize the lucene library;
        directory = FSDirectory.open(Paths.get(indexPath));
        luceneFileSearch = new LuceneFileSearch(directory, new IKAnalyzer4Lucene7(true));
        chosed_directory = new File(SerializeIndex.DeSerialize());

        //frame UI settings
        frame = new JFrame();
        frame.setTitle("文档内容检索器");
        FlowLayout lay = new FlowLayout();
        lay.setVgap(3);
        frame.setLayout(lay);
        frame.setSize(825,680);
        Toolkit tk = frame.getToolkit();
        Dimension dm = tk.getScreenSize();
        frame.setLocation((int)(dm.getWidth()/2-frame.getWidth()/2),(int)(dm.getHeight()/2-frame.getHeight()/2));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "确认退出吗?", "确认",JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if(result == JOptionPane.OK_OPTION){
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }else if(result == JOptionPane.CANCEL_OPTION){
                }
            }
        });

        //first add a root Panel
        rootPanel = new JPanel();
        frame.add(rootPanel);

        //then add panel1 to the root Panel
        panel1 = new JPanel();
        panel1.setPreferredSize(new Dimension(782,30));
        panel1.setLayout(new BorderLayout());
        rootPanel.add(panel1);
        //put label in the panel1
        label.setText("选择检索的文件格式 ");
        panel1.add(label,BorderLayout.WEST);
        //create a combocheckbox
        pdfCheckBox = new JCheckBox("PDF",true);
        txtCheckBox = new JCheckBox("TXT",true);
        docCheckBox = new JCheckBox("DOC",true);
        docxCheckBox = new JCheckBox("DOCX",true);
        pptxCheckBox = new JCheckBox("PPTX",true);
        xlsxCheckBox = new JCheckBox("XLSX",true);
        Vector<JCheckBox> v = new Vector<JCheckBox>();
        v.add(pdfCheckBox);
        v.add(txtCheckBox);
        v.add(docCheckBox);
        v.add(docxCheckBox);
        v.add(pptxCheckBox);
        v.add(xlsxCheckBox);
        ccBox = new JComboCheckBox(v);
        ccBox.setPreferredSize(new Dimension(500,25));//(670,25));
        panel1.add(ccBox,BorderLayout.CENTER);
        //radiobutton gruop
        rb_Name = new JRadioButton();
        rb_Content = new JRadioButton();
        rb_Name.setSelected(true);
        rb_Content.setSelected(false);

        group = new ButtonGroup();
        rb_Name.setText("按文件名检索");
        rb_Content.setText("按文件内容检索");
        panel1_sub = new JPanel();
        panel1_sub.add(rb_Name);
        panel1_sub.add(rb_Content);
        group.add(rb_Name);
        group.add(rb_Content);
        panel1.add(panel1_sub,BorderLayout.EAST);
        panel1.revalidate();
        //panel1.repaint();

        //panel2 components
        panel2 = new JPanel();
        BorderLayout p2layout = new BorderLayout();
        p2layout.setHgap(5);
        panel2.setLayout(p2layout);
        panel2.setPreferredSize(new Dimension(782,30));
        frame.add(panel2);
        directoryButton = new JButton("选择文件或者文件夹");
        directoryButton.setText("选择文件或者文件夹("+chosed_directory+")");
        directoryButton.setPreferredSize(new Dimension(620,26));
        //QuickFact
        QuickFactButton = new JButton("打开QuickFact");
        QuickFactButton.setPreferredSize(new Dimension(180,26));
        panel2.add(QuickFactButton,BorderLayout.EAST);
        panel2.add(directoryButton,BorderLayout.CENTER);
        panel2.revalidate();
        FileFilter docFilter = new FileTypeFilter(".docx", "Microsoft Word Documents");
        FileFilter pdfFilter = new FileTypeFilter(".pdf", "PDF Documents");
        FileFilter txtFilter = new FileTypeFilter(".txt", "Txt Documents");
        FileFilter docxFilter = new FileTypeFilter(".docx", "Docx Documents");
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("选择文件夹");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.addChoosableFileFilter(docFilter);
        chooser.addChoosableFileFilter(pdfFilter);
        chooser.addChoosableFileFilter(txtFilter);
        chooser.addChoosableFileFilter(docxFilter);

        //panel3 components
        panel3 = new JPanel();
        BorderLayout p3layout = new BorderLayout();
        p3layout.setHgap(5);
        panel3.setLayout(p3layout);//FlowLayout(FlowLayout.LEADING));
        panel3.setPreferredSize(new Dimension(782,35));
        frame.add(panel3);
        textField = new JTextField();
        //textField.setMaximumSize(new Dimension(600,26));
        textField.setText("输入检索的内容");
        panel3.add(textField,BorderLayout.CENTER);
        searchButton = new JButton();
        searchButton.setText("开始检索");
        searchButton.setPreferredSize(new Dimension(180,30));
        panel3.add(searchButton,BorderLayout.EAST);
        panel3.revalidate();
        //popup menu
//        pop_textField = new JPopupMenu();
//        JMenuItem quickFact = new JMenuItem("quickFact");
//        pop_textField.add(quickFact);
//        textField.setComponentPopupMenu(pop_textField);
//        quickFact.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    quick_func(query);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });

        panel4 = new JPanel();
        panel4.setLayout(new FlowLayout());
        frame.add(panel4);
        listModel = new DefaultListModel();
        listModel.addElement(String.format("|%1$-27s|%2$-71s|%3$-15s","文件名","文件路径","文件大小"));
        list = new JList(listModel);
        list.setSelectedIndex(0);

        //popup menu
        pop_list = new JPopupMenu("Open");
        JMenuItem open = new JMenuItem("打开源文件");
        pop_list.add(open);
        list.setComponentPopupMenu(pop_list);
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int temp = list.getSelectedIndex()-1;
                if(temp!=-1) {
                    String file_path = docs.get(temp).get("path");
                    File f = new File(file_path);
                    Desktop desktop = Desktop.getDesktop();
                    if (f.exists()) {
                        try {
                            desktop.open(f);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        sp_list = new JScrollPane(list);
        sp_list.setPreferredSize(new Dimension(780,250));
        sp_list.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sp_list.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel4.add(sp_list);
        panel4.revalidate();
        //listing out the targetted files

        panel5 = new JPanel();
        panel5.setLayout(new FlowLayout());
        frame.add(panel5);
        textArea = new DocTextArea();
        textArea.setText("检索结果将在此显示");
        sp_text = new JScrollPane(textArea);
        sp_text.setPreferredSize(new Dimension(780,250));
        sp_text.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel5.add(sp_text);
        panel5.revalidate();

        CutCopyPastActionSupport support = new CutCopyPastActionSupport();
        support.setPopup(textArea);
        support.addAction((TextAction) textArea.getActionMap().get(DefaultEditorKit.selectAllAction),KeyEvent.VK_A,"select all");//get addAction public
//        support.addAction(new quickfact_func("Add To QuickFact"), KeyEvent.VK_SPACE, "Add To QuickFact" );
        directoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JFileChooser.APPROVE_OPTION == chooser.showDialog(frame,"确定")){
                    //chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    chosed_directory = chooser.getCurrentDirectory();
                    chosed_file = chooser.getSelectedFile();
                        try {
                        SerializeIndex.Serialize(chosed_file.getCanonicalPath());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        luceneFileSearch.clearIndex();
                        luceneFileSearch.addDirToIndex(chosed_file.getCanonicalPath());
                    } catch (IOException | TikaException | SAXException ex) {
                        ex.printStackTrace();
                    }
                    directoryButton.setText("选择文件或者文件夹("+chosed_file+")");
                }
            }
        });

        QuickFactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(query != null){
                    String fact_path = "./quick_fact/"+ query + ".txt";
                    File f = new File(fact_path);
                    Desktop desktop = Desktop.getDesktop();
                    if (f.exists()) {
                        try {
                            desktop.open(f);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                query = textField.getText();
                if(rb_Name.isSelected()){
                    searchingField = "filename";
                }
                else if(rb_Content.isSelected()){
                    searchingField = "contents";
                }
                int n = 0;

                textArea.setText("");
                long start = System.currentTimeMillis();
                docs = luceneFileSearch.searchFiles(searchingField, query);
                long end = System.currentTimeMillis();

                listModel.clear();
                listModel.addElement(String.format("|%1$-27s|%2$-71s|%3$-15s","文件名","文件路径","文件大小"));

                //list items
                for(Document doc:docs) {
                    String path = doc.get("path");
                    File file = new File(path);
                    path = "..." + path.substring(path.length()-40);
                    if(typesetter(doc.get("filename")))
                        listModel.addElement(String.format("|%1$-30s|%2$-75s|%3$-20s",doc.get("filename"),path, String.valueOf(file.length()/1024+1)+" KB"));
                }
                searchingResult = "Current Directory: " + chosed_directory +
                        "\n"+ "total search time: " + (end -start) + "ms"+
                        "\n"+docs.size()+" results";
                textArea.setText(searchingResult);
                try {
                    textArea.appendContents_fact(query);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (TikaException ex) {
                    ex.printStackTrace();
                }

//                int i = 0;
//                for(Document doc : docs) {
//                    i++;
//                    textArea.append("\n" + "result " + i + "\t" + doc.get("filename"));
//                }
            }
        });

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseReleased(e);
                int selected_index = list.getSelectedIndex()-1;
                if (e.getModifiers() == MouseEvent.BUTTON1_MASK && e.getClickCount() == 1) {
                    textArea.setText("");
                    if(selected_index!=-1){//avoid select first row in list
                        try {
                            textArea.ShowContents(docs, selected_index);
                            textArea.taHighlighter(query);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (TikaException ex) {
                            ex.printStackTrace();
                        }
                    }
                    else{
                        try {
                        textArea.setText(searchingResult);
                        textArea.appendContents_fact(query);
                        } catch (IOException|TikaException ex) {
                            ex.printStackTrace();
                        }

                    }
                }else if(e.getModifiers() == MouseEvent.BUTTON3_MASK && e.getClickCount() == 1){
                    if (SwingUtilities.isRightMouseButton(e)        // if right mouse button clicked
                            && !list.isSelectionEmpty()             // and list selection is not empty
                            && list.locationToIndex(e.getPoint())   // and clicked point is
                            == list.getSelectedIndex()) {           //   inside selected item bounds
                        pop_list.show(list, e.getX(), e.getY());
                    }
                }
            }
        });

        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(textField.getText().equals("输入检索的内容"))
                    textField.setText("");
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        });
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_F12) {
                    try {
                        quickfact_func.addToQuickFact(query);
                        textArea.setText(searchingResult);
                        textArea.appendContents_fact(query);
                    } catch (IOException | TikaException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

//    void quick_func(String query_name) throws IOException{
////        String output_path = "./quick_fact/"+ query_name + ".txt";
////        String fact;
////        fact = getClipboardString() + "\n";
////        FileWriter fw = new FileWriter(output_path, true);
////        BufferedWriter bw = new BufferedWriter(fw);
////        bw.write(fact);
////        bw.close();
////        fw.close();
////    }

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

    boolean typesetter(String filename){
        String post = filename.substring(filename.lastIndexOf(".")+1);
        switch (post)
        {
            case "doc":
                if(docCheckBox.isSelected())
                    return true;
                break;
            case "docx":
                if(docxCheckBox.isSelected())
                    return true;
                break;
            case "pdf":
                if(pdfCheckBox.isSelected())
                    return true;
                break;
            case "txt":
                if(txtCheckBox.isSelected())
                    return true;
                break;
            case "pptx":
                if(pptxCheckBox.isSelected())
                    return true;
                break;
            case "xlsx":
                if(xlsxCheckBox.isSelected())
                    return true;
                break;
        }
        return false;
    }

    List <Document> docFilter(List<Document> docs, List<String> selectedTypes){
        List <Document> tmpDocs = null;
        String fileType = null;
        for(Document doc : docs){
            fileType = doc.get("type");
            if(selectedTypes.contains(fileType))
                tmpDocs.add(doc);
        }
        return tmpDocs;
    }
}
