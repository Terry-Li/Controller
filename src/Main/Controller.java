/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author lyf
 */
public class Controller {
    public static String database = "C:/Users/admin/Desktop/Database/";
    public static String insert = "C:/Users/admin/Desktop/Insert/";
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        List<String> univs = getKeywords("Elite96.txt");
        for (int i=0; i<univs.size(); i++) {
            String filename = i+1+".txt";
            if (i+1<10) {
                filename = "0"+filename;
            }
            insertUniv(univs.get(i),filename);
        }
    }
    
    public static List<String> getSchools(String filename) throws IOException{
        if (!new File(database+filename).exists()) return null;
        String file = FileUtils.readFileToString(new File(database+filename));
        String[] blocks = file.split("\r\n\r\n");
        if (blocks[0].split("\r\n").length == 1) return null;
        List<String> schools = new ArrayList<String>();
        for (int i=0;i<blocks.length;i++) {
            //System.out.println(blocks[i]);
            //System.out.println("===========================");
            if (i==0) {
                schools.add(blocks[i].split("\r\n")[1].split("==")[1]);
            } else {
                schools.add(blocks[i].split("\r\n")[0].split("==")[1]);
            }
        }
        return schools;
    }
    
    public static List<List<String>> getDepartments(String filename) throws IOException {
        if (!new File(database+filename).exists()) return null;
        String file = FileUtils.readFileToString(new File(database+filename));
        String[] blocks = file.split("\r\n\r\n");
        if (blocks[0].split("\r\n").length == 1) return null;
        List<List<String>> schools = new ArrayList<List<String>>();
        for (int i=0;i<blocks.length;i++) {
            if (i==0) {
                List<String> school = new ArrayList<String>();
                String[] rows = blocks[i].split("\r\n");
                for (int j=1;j<rows.length;j++) {
                    school.add(rows[j]);
                }
                schools.add(school);
            } else {
                List<String> school = new ArrayList<String>();
                String[] rows = blocks[i].split("\r\n");
                for (int j=0;j<rows.length;j++) {
                    school.add(rows[j]);
                }
                schools.add(school);
            }
        }
        return schools;
    }
    
    public static void insertUniv(String univ, String filename) throws IOException{
        StringBuilder sb = new StringBuilder();
        String univName = univ.split("==")[0];
        String univUrl = univ.split("==")[1];
        sb.append("insert University \""+univName+"\" [\n");
        sb.append("@webSite:\""+univUrl+"\",\n");
        List<String> schools = getSchools(filename);
        if (schools != null) {
            sb.append("contain academics -> {\n");
            sb.append("Faculties : \n");
            sb.append("{\n");
            for (int i=0; i<schools.size(); i++) {
                if (i != schools.size()-1) {
                    sb.append("\""+schools.get(i) +"\",\n");
                } else {
                    sb.append("\""+schools.get(i) +"\"\n");
                }
            }
            sb.append("} (\""+univName+"\")\n");
            sb.append("}\n");
        }
        sb.append("];\n\n");
        List<List<String>> departments = getDepartments(filename);
        if (departments != null) {
            for (List<String> dept : departments) {
                if (dept.size() == 1) {
                    sb.append("insert Faculties \"" + dept.get(0).split("==")[1] + "\" (\"" + univName + "\")[\n");
                    String schoolUrl = dept.get(0).split("==")[2];
                    if (!schoolUrl.equals("null")) {
                        sb.append("@website:\"" + schoolUrl + "\",\n");
                    }
                    sb.append("];\n\n");
                } else {
                    sb.append("insert Faculties \"" + dept.get(0).split("==")[1] + "\" (\"" + univName + "\")[\n");
                    sb.append("@website:\"" + dept.get(0).split("==")[2] + "\",\n");
                    sb.append("contain departments: {\n");
                    for (int j = 1; j < dept.size(); j++) {
                        if (j != dept.size() - 1) {
                            sb.append("\"" + dept.get(j).split("==")[1] + "\",\n");
                        } else {
                            sb.append("\"" + dept.get(j).split("==")[1] + "\"\n");
                        }
                    }
                    sb.append("}(\"" + dept.get(0).split("==")[1] + "(" + univName + ")\")\n");
                    sb.append("];\n\n");
                }
            }
        }
        FileUtils.write(new File(insert+filename), sb.toString());
    }
    
    public static List<String> getKeywords(String file) throws FileNotFoundException, IOException {
        List<String> keywords = new ArrayList<String>();
        FileInputStream fstream = null;
        fstream = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        while ((strLine = br.readLine()) != null && !strLine.trim().equals("")) {
            keywords.add(strLine.trim());
        }
        return keywords;
    }
}
