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
    public LinkedHashMap GetTAData(String Username, String Password){
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
            cookies.put("session_token", session_token);
            cookies.put("student_id", student_id);
            parameters.put("student_id ", student_id);

            String[] resp = sr.send(url, headers, parameters, cookies, path);

            LinkedHashMap<String, List<String>> Marks = new LinkedHashMap();

            for(String i :resp[0].split("<td>")){
                if(i.contains("current mark =  ")){
                    //System.out.println(i);
                    String Subject_id = i.split("subject_id=")[1].split("&")[0];
                    String Current_mark = i.split("current mark =  ")[1].split("%")[0];
                    String Course_Name = i.split(":")[0];
                    String Course_code = i.split(":")[1].split("<br>")[0];
                    List Stats = new ArrayList<>();
                    Stats.add(Current_mark);
                    Stats.add(Course_Name);
                    Stats.add(Course_code);


                    Marks.put(Subject_id, Stats);
                }
                if(i.contains("Please see teacher for current status regarding achievement in the course")){
                    List<String> Stats = new ArrayList<>();
                    String Course_Name = i.split(":")[0];
                    String Course_code = i.split(":")[1].split("<br>")[0];
                    Stats.add(Course_Name);
                    Stats.add(Course_code);


                    Marks.put("NA", Stats);
                }
            }


            return Marks;




        }
        catch(IOException e) {
            e.printStackTrace();
            //String[] returnString = {"ERROR! Check in SendRequest"};
            LinkedHashMap<String, String> returnMap = new LinkedHashMap<>();
            return returnMap;
        }

    }


    public Double GetAverage(HashMap<String, List<String>> Marks){

        //Get average
        double Average = 0;
        double[] grades = new double[4];
        int i = 0;
        for (Map.Entry<String, List<String>> entry : Marks.entrySet()) {
            if (entry.getKey() != "NA") {
                grades[i] = Double.parseDouble(entry.getValue().get(0));


            }
            i++;

        }
        for (double value:grades)
            Average += value;
        Average = DoubleRounder.round(Average/grades.length, 1);
        return Average;

    }

}
