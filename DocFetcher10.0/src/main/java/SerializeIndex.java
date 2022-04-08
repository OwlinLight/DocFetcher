import java.io.*;

public class SerializeIndex {
    public static void Serialize(String file_path)
    {
        try
        {
            File file = new File("./tmp/file_path.ser");
            if(!file.exists())
                file.createNewFile();
            FileOutputStream fileOut =
                    new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(file_path);
            out.close();
            fileOut.close();
//            System.out.printf("Serialized data is saved in /tmp/employee.ser");
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }
    public static String DeSerialize()
    {
        String file_path = null;
        File file = new File("./tmp/file_path.ser");
        try
        {

            if(file.exists()) {
                FileInputStream fileIn = new FileInputStream("./tmp/file_path.ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                file_path = (String) in.readObject();
                in.close();
                fileIn.close();
            }
            else
                file.createNewFile();

        }catch(IOException i)
        {
            i.printStackTrace();
        }catch(ClassNotFoundException c)
        {
            System.out.println("String class not found");
            c.printStackTrace();
        }
        return file_path;
    }

}
