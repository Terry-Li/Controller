/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author lyf
 */
public class Directory {
    public static String database = "C:/Users/admin/Desktop/Database/";
    public static String directory = "C:/Users/admin/Desktop/Directory/";
    public static void main(String[] args) throws IOException {
        File[] files = new File(database).listFiles();
        for (File file: files) {
            makeDir(file.getName());
        }
    }
    
    public static void makeDir(String filename) throws IOException{
        StringBuilder sb = new StringBuilder();
        List<List<String>> schools = Controller.getDepartments(filename);
        if (schools != null) {
            for (List<String> school : schools) {
                for (String row : school) {
                    String[] tokens = row.split("==");
                    //if (tokens.length < 4) System.out.println(filename+": "+row);
                    if (tokens.length == 4 && !tokens[3].equals("null")) {
                        sb.append(tokens[3] + "\n");
                    }
                }
            }
            FileUtils.writeStringToFile(new File(directory + filename), sb.toString());
        }
    }
}
