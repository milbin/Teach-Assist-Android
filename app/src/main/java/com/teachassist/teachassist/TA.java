package com.teachassist.teachassist;

import org.decimal4j.util.DoubleRounder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class TA {
    String student_id;
    String session_token;
    ArrayList<String> subjects = new ArrayList<>();

    /*
    public String Get_session_token_and_student_ID(String Username, String Password) {
        try {
        //get sesison token and studentID
        String url = "https://ta.yrdsb.ca/live/index.php?";
        String path = "/live/index.php?";
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> parameters = new HashMap<>();
        HashMap<String, String> cookies = new HashMap<>();
        parameters.put("username", Username);
        parameters.put("password", Password);
        parameters.put("subject_id", "0");

        //get response
        SendRequest sr = new SendRequest();
        String[] response = sr.send(url, headers, parameters, cookies, path);


        String session_token = response[1].split("=")[1].split(";")[0];
        String student_id = response[2].split("=")[1].split(";")[0];
        return  Login(session_token, student_id, Username, Password);




        }
        catch(IOException e) {
            e.printStackTrace();
            //String[] returnString = {"ERROR! Check in SendRequest"};
            return "ERROR! Check in SendRequest";
        }



        }

    public String Login(String session_token, String student_id, String Username, String Password){

        //add headers parameters and cookies
        String url = "https://ta.yrdsb.ca/live/index.php?";
        String path = "/live/index.php?";
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> parameters = new HashMap<>();
        HashMap<String, String> cookies = new HashMap<>();
        parameters.put("username", Username);
        parameters.put("password", Password);
        parameters.put("subject_id", "0");
        cookies.put("session_token", session_token);
        cookies.put("student_id", student_id);
        parameters.put("student_id ", student_id);


        SendRequest sr = new SendRequest();
        try {
            String[] response = sr.send(url, headers, parameters, cookies, path);
            return response.toString();
        }
        catch(IOException e) {
            e.printStackTrace();

            return "ERROR! Check in SendRequest";
        }

    }
    */
    public LinkedHashMap<String, List<String>> GetTAData(String Username, String Password){
        try {
            //get sesison token and studentID
            String url = "https://ta.yrdsb.ca/live/index.php?";
            String path = "/live/index.php?";
            HashMap<String, String> headers = new HashMap<>();
            HashMap<String, String> parameters = new HashMap<>();
            HashMap<String, String> cookies = new HashMap<>();
            parameters.put("username", Username);
            parameters.put("password", Password);
            parameters.put("subject_id", "0");

            //get response
            SendRequest sr = new SendRequest();
            String[] response = sr.send(url, headers, parameters, cookies, path);

            try {

            session_token = response[1].split("=")[1].split(";")[0];
            student_id = response[2].split("=")[1].split(";")[0];
            cookies.put("session_token", session_token);
            cookies.put("student_id", student_id);
            parameters.put("student_id ", student_id);

            String[] resp = sr.send(url, headers, parameters, cookies, path);

            LinkedHashMap<String, List<String>> Marks = new LinkedHashMap();

            int numberOfEmptyCourses = 0;
            for(String i :resp[0].split("<td>")){
                if(i.contains("current mark =  ")){
                    String Subject_id = i.split("subject_id=")[1].split("&")[0].trim();
                    String Current_mark = i.split("current mark =  ")[1].split("%")[0].trim();
                    String Course_Name = i.split(":")[0].trim();
                    String Course_code = i.split(":")[1].split("<br>")[0].trim();
                    String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                    List Stats = new ArrayList<>();
                    Stats.add(Current_mark);
                    Stats.add(Course_Name);
                    Stats.add(Course_code);
                    Stats.add(Room_Number);

                    subjects.add(Subject_id);

                    Marks.put(Subject_id, Stats);
                }
                if(i.contains("Please see teacher for current status regarding achievement in the course")){
                    System.out.println("Please see teacher for current status regarding achievement in the course");
                    ArrayList<String> Stats = new ArrayList<>();
                    String Course_Name = i.split(":")[0].trim();
                    String Course_code = i.split(":")[1].split("<br>")[0].trim();
                    String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                    Stats.add(Course_Name);
                    Stats.add(Course_code);
                    Stats.add(Room_Number);

                    numberOfEmptyCourses++;
                    Marks.put("NA"+numberOfEmptyCourses, Stats);
                }
            }
            return Marks;

            }
            catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
                LinkedHashMap<String, List<String>> returnMap = new LinkedHashMap<>();
                return returnMap;
            }




        }
        catch(IOException e) {
            e.printStackTrace();
            //String[] returnString = {"ERROR! Check in SendRequest"};
            LinkedHashMap<String, List<String>> returnMap = new LinkedHashMap<>();
            return returnMap;
        }

    }


    public Double GetAverage(HashMap<String, List<String>> Marks){

        //Get average
        double Average = 0;
        int x = 0;
        for (Map.Entry<String, List<String>> entry : Marks.entrySet()) {
            if (!entry.getKey().contains("NA")) {
                x++;
            }
        }
        double[] grades = new double[x];
        int i = 0;
        for (Map.Entry<String, List<String>> entry : Marks.entrySet()) {
            if (!entry.getKey().contains("NA")) {
                grades[i] = Double.parseDouble(entry.getValue().get(0));
                i++;
            }


        }
        for (double value:grades)
            Average += value;
        Average = DoubleRounder.round(Average/grades.length, 1);
        return Average;

    }

    public LinkedHashMap<String,List<Map<String,String>>> GetMarks(int subject_number){
        try {
            String url = "https://ta.yrdsb.ca/live/students/viewReport.php?";
            String path = "/live/students/viewReport.php?";
            HashMap<String, String> headers = new HashMap<>();
            HashMap<String, String> parameters = new HashMap<>();
            HashMap<String, String> cookies = new HashMap<>();
            parameters.put("subject_id", subjects.get(subject_number));
            parameters.put("student_id", student_id);
            cookies.put("session_token", session_token);
            cookies.put("student_id", student_id);

            Map<String,String> fields = new HashMap<>();
            fields.put("knowledge","ffffaa");
            fields.put("thinking","c0fea4");
            fields.put("communication","afafff");
            fields.put("application","ffd490");
            fields.put("other","#dedede");

            //get response
            SendRequest sr = new SendRequest();
            String[] response = sr.send(url, headers, parameters, cookies, path);

            LinkedHashMap<String,List<Map<String,String>>> marks = new LinkedHashMap<>();
            System.out.println("knowledge = "+fields.get("knowledge"));

            for(String i:response[0].split("rowspan")){
                ArrayList<Map<String,String>> stats = new ArrayList<>();
                if (i.charAt(0) == '=') {
                    System.out.println("HERE"+i);
                    String assignment = i.split(">")[1].split("<")[0].trim().replaceAll("&eacute;","é");
                    String knowledge;
                    String thinking;
                    String communication;
                    String application;
                    String other;
                    try {
                        Map<String, String> mark = new HashMap<>();
                        if (i.split("bgcolor=\"" + fields.get("knowledge"))[1].split("</td>")[0].contains("border")) {
                            knowledge = i.split("bgcolor=\"" + fields.get("knowledge"))[2].split("id=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                        } else {
                            knowledge = "";
                        }
                        mark.put("knowledge",knowledge);
                        stats.add(mark);
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("eek");
                    }
                    try {
                        Map<String, String> mark = new HashMap<>();
                        if (i.split("bgcolor=\"" + fields.get("thinking"))[1].split("</td>")[0].contains("border")) {
                            thinking = i.split("bgcolor=\"" + fields.get("thinking"))[2].split("id=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                        } else {
                            thinking = "";
                        }
                        mark.put("thinking",thinking);
                        stats.add(mark);
                    }
                    catch (ArrayIndexOutOfBoundsException e){

                    }
                    try {
                        Map<String, String> mark = new HashMap<>();
                        if (i.split("bgcolor=\"" + fields.get("communication"))[1].split("</td>")[0].contains("border")) {
                            communication = i.split("bgcolor=\"" + fields.get("communication"))[2].split("id=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                        } else {
                            communication = "";
                        }
                        mark.put("communication",communication);
                        stats.add(mark);
                    }
                    catch (ArrayIndexOutOfBoundsException e){

                    }
                    try {
                        Map<String, String> mark = new HashMap<>();
                        if (i.split("bgcolor=\"" + fields.get("application"))[1].split("</td>")[0].contains("border")) {
                            application = i.split("bgcolor=\"" + fields.get("application"))[2].split("id=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                        } else {
                            application = "";
                        }
                        mark.put("application",application);
                        stats.add(mark);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {

                    }
                    try {
                        Map<String, String> mark = new HashMap<>();
                        if (i.split("bgcolor=\"" + fields.get("other"))[1].split("</td>")[0].contains("border")) {
                            //System.out.println("FUCK "+i.split("bgcolor=\"" + fields.get("other"))[2]);
                            //System.out.println("CUCK = "+ i.split("bgcolor=\""+fields.get("other"))[2].split("id=")[1].split(">")[1]);
                            other = i.split("bgcolor=\"" + fields.get("other"))[2].split("id=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                            //other = "";
                        } else {
                            other = "";
                        }
                        mark.put("other",other);
                        stats.add(mark);
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                    }

                    marks.put(assignment, stats);
                }

            }
            System.out.println(marks);
            LinkedHashMap<String,List<HashMap<String,String>>> returnMap = new LinkedHashMap<>();
            return marks;
        }
        catch(IOException e) {
            e.printStackTrace();
            //String[] returnString = {"ERROR! Check in SendRequest"};
            LinkedHashMap<String,List<Map<String,String>>> returnMap = new LinkedHashMap<>();
            return returnMap;
        }

    }

}
