package com.teachassist.teachassist;

import java.io.IOException;
import java.util.HashMap;


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

}
