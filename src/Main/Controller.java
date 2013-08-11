/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author lyf
 */
public class Controller {
    public static String database = "C:/Users/admin/Desktop/Database/";
    public static String insert = "C:/Users/admin/Desktop/Insert/";
    public static String faculty = "C:/Users/admin/Desktop/96 Faculty/";
    
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
        int index = 0;
        StringBuilder deptSB = new StringBuilder();
        String folder = filename.split("\\.")[0];
        if (departments != null) {
            for (List<String> dept : departments) {
                if (dept.size() == 1) {
                    sb.append("insert Faculties \"" + dept.get(0).split("==")[1] + "\" (\"" + univName + "\")[\n");
                    String schoolUrl = dept.get(0).split("==")[2];
                    String schoolName = dept.get(0).split("==")[1];
                    if (!schoolUrl.equals("null")) {
                        sb.append("@website:\"" + schoolUrl + "\",\n");
                    }
                    String[] tokens = dept.get(0).split("==");
                    if (tokens.length == 4 && !tokens[3].equals("null")) {
                        if (new File(faculty+folder+"/"+index+".txt").exists()) {
                            insertSchoolFaculty(sb, univName, schoolName, index, filename);
                            sb.append("\n");
                        }
                        index++;
                    }
                    sb.append("];\n\n");
                } else {
                    sb.append("insert Faculties \"" + dept.get(0).split("==")[1] + "\" (\"" + univName + "\")[\n");
                    sb.append("@website:\"" + dept.get(0).split("==")[2] + "\",\n");
                    String[] parts = dept.get(0).split("==");
                    if (parts.length == 4 && !parts[3].equals("null")) {
                        if (new File(faculty+folder+"/"+index+".txt").exists()) {
                            insertSchoolFaculty(sb, univName, parts[1], index, filename);
                            sb.append(",\n");
                        }
                        index++;
                    }
                    sb.append("contain departments: {\n");
                    for (int j = 1; j < dept.size(); j++) {
                        if (j != dept.size() - 1) {
                            sb.append("\"" + dept.get(j).split("==")[1] + "\",\n");
                        } else {
                            sb.append("\"" + dept.get(j).split("==")[1] + "\"\n");
                        }
                        String[] tokens = dept.get(j).split("==");
                        if (tokens.length == 4 && !tokens[2].equals("null")) {
                            if (!tokens[3].equals("null")) {
                                deptSB.append("insert Departments \""+tokens[1]+"\"(\""+dept.get(0).split("==")[1]+"("+univName+")\")[\n");
                                deptSB.append("@website:\""+tokens[2]+"\",\n");
                                if (new File(faculty+folder+"/"+index+".txt").exists()) {
                                    insertDeptFaculty(deptSB, univName, dept.get(0).split("==")[1], tokens[1], index, filename);
                                }
                                index++;
                                deptSB.append("];\n\n");
                            } else {
                                deptSB.append("insert Departments \""+tokens[1]+"\"(\""+dept.get(0).split("==")[1]+"("+univName+")\")[\n");
                                deptSB.append("@website:\""+tokens[2]+"\"\n");
                                deptSB.append("];\n\n");
                            }
                        }
                    }
                    sb.append("}(\"" + dept.get(0).split("==")[1] + "(" + univName + ")\")\n");
                    sb.append("];\n\n");
                }
            }
        }
        FileUtils.write(new File(insert+filename), sb.toString()+deptSB.toString());
    }
    
    public static void insertSchoolFaculty(StringBuilder sb, String univName, String schoolName, int index, String filename){
        String folder = filename.split("\\.")[0];
        //if (!new File(faculty+folder+"/"+index+".txt").exists()) return;
        sb.append("role Academics Faculty ->\n");
        sb.append("{\n");
        sb.append("Professor:{\n");
        insertPersons(sb, folder, index);
        sb.append("}(\""+schoolName+"("+univName+")\")\n");
        sb.append("}");
    }
    
    public static void insertDeptFaculty(StringBuilder sb, String univName, String schoolName, String deptName, int index, String filename){
        String folder = filename.split("\\.")[0];
        //if (!new File(faculty+folder+"/"+index+".txt").exists()) return;
        sb.append("role Academics Faculty ->\n");
        sb.append("{\n");
        sb.append("Professor:{\n");
        insertPersons(sb, folder, index);
        sb.append("}(\""+deptName+"("+schoolName+"("+univName+"))\")\n");
        sb.append("}\n");
    }
    
    public static void insertPersons(StringBuilder sb, String folder, int index) {
        try {
            String text = FileUtils.readFileToString(new File(faculty+folder+"/"+index+".txt"));
            String[] persons =text.split("\r\n\r\n");
            Pattern p = Pattern.compile(".*\"(.*)\".*");
            for (int i=0; i<persons.length; i++) {
                String photo = null;
                String name = null;
                String web = null;
                String position = null;
                String email = null;
                String phone = null;
                String[] attrs = persons[i].split("\r\n");
                for (String attr: attrs) {
                    String[] pair = attr.split("==");
                    if (pair.length == 2 ) {
                        if (pair[0].equals("photo")) {
                            Matcher m = p.matcher(pair[1]);
                            if (m.find()) {
                                photo = m.group(1);
                            }
                        } else if (pair[0].equals("name")) {
                            name = pair[1];
                        } else if (pair[0].equals("web")) {
                            web = pair[1];
                        } else if (pair[0].equals("position")) {
                            position = pair[1];
                        } else if (pair[0].equals("email")) {
                            email = pair[1];
                        } else if (pair[0].equals("phone")) {
                            phone = pair[1];
                        }
                    }
                }
                if (name == null) { name = "Not Found";}
                sb.append("\""+name.replaceAll("\"", "")+"\"");
                List<String> temp = new ArrayList<String>();
                if (photo != null) {
                    temp.add("@photo:\""+photo.replaceAll("\"", "") +"\"");
                }
                if (web != null) {
                    temp.add("@homepage:\""+web.replaceAll("\"", "")+"\"");
                }
                if (position != null) {
                    temp.add("@position:\""+position.replaceAll("\"", "")+"\"");
                }
                if (email != null) {
                    temp.add("@email:\""+email.replaceAll("\"", "")+"\"");
                }
                if (phone != null) {
                    temp.add("@phone:\""+phone.replaceAll("\"", "")+"\"");
                }
                if (temp.size()!=0) sb.append("[");
                for (int j=0; j<temp.size(); j++) {
                    if (j != temp.size()-1) {
                        sb.append(temp.get(j)+",");
                    } else {
                        sb.append(temp.get(j));
                    }
                }
                if (temp.size() != 0) {
                    if (i != persons.length - 1) {
                        sb.append("],\n");
                    } else {
                        sb.append("]\n");
                    }
                } else {
                    if (i != persons.length - 1) {
                        sb.append(",\n");
                    } else {
                        sb.append("\n");
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
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
