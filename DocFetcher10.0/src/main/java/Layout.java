import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;

public class Layout{
    private JFrame frame;
    private JLabel label;
    private JComboBox comboBox;
    private JButton directory;
    private JTextField textField1;
    private JButton search;
    private JList list;
    private JTextArea textArea;
    private JPanel rootPanel;
    private JFileChooser choose;
    private File chosed_directory,chosed_file;
    private JCheckBox pdfCheckBox,txtCheckBox,docCheckBox,docxCheckBox;

    public Layout(){
        frame = new JFrame();
        frame.setTitle("文档内容检索器");
        frame.setLayout(new FlowLayout(FlowLayout.LEADING));
        frame.setSize(825,645);
        Toolkit tk = frame.getToolkit();
        Dimension dm = tk.getScreenSize();
        frame.setLocation((int)(dm.getHeight()/2-825/2),(int)(dm.getHeight()/2-645/2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "确认退出吗?", "确认",JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if(result == JOptionPane.OK_OPTION){
                    System.exit(0);
                }
            }
        });

        frame.add(rootPanel);

        pdfCheckBox = new JCheckBox("PDF",false);
        txtCheckBox = new JCheckBox("TXT",false);
        docCheckBox = new JCheckBox("DOC",false);
        docxCheckBox = new JCheckBox("DOCX",false);
        Vector v = new Vector();
        v.add("可供选择的文件格式");
        v.add(pdfCheckBox);
        v.add(txtCheckBox);
        v.add(docCheckBox);
        v.add(docxCheckBox);
        comboBox = new JComboBox(v);

        //functions
        FileFilter docFilter = new FileTypeFilter(".docx", "Microsoft Word Documents");
        FileFilter pdfFilter = new FileTypeFilter(".pdf", "PDF Documents");
        FileFilter txtFilter = new FileTypeFilter(".txt", "Txt Documents");
        FileFilter docxFilter = new FileTypeFilter(".docx", "Docx Documents");
        choose = new JFileChooser();
        choose.setCurrentDirectory(new java.io.File("."));
        choose.setDialogTitle("选择文件夹");
        choose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        choose.addChoosableFileFilter(docFilter);
        choose.addChoosableFileFilter(pdfFilter);
        choose.addChoosableFileFilter(txtFilter);
        choose.addChoosableFileFilter(docxFilter);
        directory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JFileChooser.APPROVE_OPTION == choose.showDialog(frame,"确定")){
                    chosed_directory = choose.getCurrentDirectory();
                    chosed_file = choose.getSelectedFile();
                }
            }
        });
    }
}
