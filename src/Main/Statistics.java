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
public class Statistics {
    public static String schoolBase = "C:/Users/admin/Desktop/Database/";
    public static String profBase = "C:/Users/admin/Desktop/96 Faculty/";
    
    public static int schoolCount() throws IOException{
        int count = 0;
        File[] files = new File(schoolBase).listFiles();
        for (File file: files) {
            List<String> schools = Controller.getSchools(file.getName());
            if (schools != null) {
                count += schools.size();
            }
        }
        return count;
    }
    
    public static int deptCount() throws IOException{
        int count = 0;
        File[] files = new File(schoolBase).listFiles();
        for (File file: files) {
            List<List<String>> depts = Controller.getDepartments(file.getName());
            if (depts != null) {
                for (List<String> dept: depts) {
                    count += dept.size() - 1;
                }
            }
        }
        return count;
    }
    
    public static int profCount() throws IOException{
        int count = 0;
        File[] folders = new File(profBase).listFiles();
        for (File folder: folders) {
            File[] files = new File(profBase+folder.getName()).listFiles();
            for (File file: files) {
                String text = FileUtils.readFileToString(file);
                String[] persons = text.split("\r\n\r\n");
                count += persons.length;
            }
        }
        return count;
    }
    
    public static int univCount() {
        File[] files = new File(schoolBase).listFiles();
        return files.length;
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println("Total number of univs: "+univCount());
        System.out.println("Total number of schools: "+schoolCount());
        System.out.println("Total number of depts: "+deptCount());
        System.out.println("Total number of profs: "+profCount());
    }
}
