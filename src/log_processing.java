import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files

public class log_processing {

    public static void main(String[] args) {
        try {
            long ts = 0;
            long tj = 0;

            int line = 0;
            File myObj = new File(args[0]);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                line ++;
                String data = myReader.nextLine();
                String[] dataVals = data.replaceAll("/n", "").split(",");
                ts += Long.parseLong(dataVals[0]);
                tj += Long.parseLong(dataVals[1]);
            }

            System.out.println("TS avg = " + ts/line);
            System.out.println("TJ avg = " + tj/line);
            myReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
