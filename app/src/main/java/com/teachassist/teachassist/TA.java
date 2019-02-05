package com.teachassist.teachassist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.components.LimitLine;
import com.google.gson.JsonObject;

import org.decimal4j.util.DoubleRounder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.teachassist.teachassist.LaunchActivity.CREDENTIALS;
import static com.teachassist.teachassist.LaunchActivity.PASSWORD;
import static com.teachassist.teachassist.LaunchActivity.USERNAME;


public class TA{
    String student_id;
    String session_token;
    ArrayList<String> subjects = new ArrayList<>();
    LinkedHashMap<String, List<String>> Marks;
    String[] marksResponse;
    String username;
    String password;



    public LinkedHashMap<String, List<String>> GetTAData(String Username, String Password) {
        username = Username;
        password = Password;
        Crashlytics.log(Log.DEBUG, "username", Username);
        Crashlytics.log(Log.DEBUG, "password", Password);
        SendRequest sr = new SendRequest();
        try {
            //get token and student id
            JSONObject json = new JSONObject();
            json.put("student_number", Username);
            json.put("password", Password);
            JSONObject respJson = sr.sendJson("https://ta.yrdsb.ca/v4/students/json.php", json.toString()).getJSONObject(0);
            if(respJson == null){
                Crashlytics.log(Log.ERROR, "network request failed", "line 58 TA");
                return null;
            }

            session_token = respJson.getString("token");
            student_id = respJson.getString("student_id");


            //get marks and course code
            JSONObject marksJson = new JSONObject();
            marksJson.put("token", session_token);
            marksJson.put("student_id", student_id);
            marksJson.put("subject_id", 0);
            JSONArray marksResp = sr.sendJson("https://ta.yrdsb.ca/v4/students/json.php", marksJson.toString())
                    .getJSONObject(0)
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getJSONArray("subjects");
            if(marksResp == null){
                Crashlytics.log(Log.ERROR, "network request failed", "line 77 TA");
                return null;
            }

            Marks = new LinkedHashMap<>();
            for (int i = 0; i < marksResp.length(); i++) {
                ArrayList<String> fields = new ArrayList<>();
                JSONObject subject = marksResp.getJSONObject(i);
                if(subject.getString("mark").equals("Please see teacher for current status regarding achievement in the course")) {
                    fields.add(subject.getString("course"));
                    Marks.put("NA"+i, fields);
                }else{
                    fields.add(subject.getString("mark").replaceAll("%", "").replaceAll(" ", ""));
                    fields.add(subject.getString("course"));
                    Marks.put(subject.getString("subject_id"), fields);
                }
                subjects.add(subject.getString("subject_id"));

            }



            //get room number and course name
            String url = "https://ta.yrdsb.ca/live/index.php?";
            String path = "/live/index.php?";
            HashMap<String, String> headers = new HashMap<>();
            HashMap<String, String> parameters = new HashMap<>();
            HashMap<String, String> cookies = new HashMap<>();
            parameters.put("subject_id", "0");
            parameters.put("username", Username);
            parameters.put("password", Password);
            cookies.put("session_token", session_token);
            cookies.put("student_id", student_id);
            String[] resp = sr.send(url, headers, parameters, cookies, path);
            if(resp == null){
                Crashlytics.log(Log.ERROR, "network request failed", "line 112 TA");
                return null;
            }

            //parse return
            int courseCounter1 = 0;
            for(String i :resp[0].split("<td>")){
                if(i.contains("current mark =  ") || i.contains("Please see teacher for current status regarding achievement in the course")||i.contains("Click Here")) {
                    String Course_Name = i.split(":")[1].split("<br>")[0].trim();
                    String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                    //String Subject_id = i.split("subject_id=")[1].split("&")[0].trim();
                    int courseCounter2 = 0;
                    for (Map.Entry<String, List<String>> entry : Marks.entrySet()) {
                        if(courseCounter1 == courseCounter2) {
                            entry.getValue().add(Course_Name);
                            entry.getValue().add(Room_Number);
                        }
                        courseCounter2++;

                    }
                    courseCounter1++;
                }


                    //Marks.put(Subject_id, Stats);

            }
            System.out.println(Marks+"marks RESPONSE HERE<----");
            return Marks;



        }catch (Exception e){
            e.printStackTrace();
            Crashlytics.log(Log.ERROR, "Error in GetTaData in TA()", Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public LinkedHashMap<String, List<String>> GetTADataNotifications(String Username, String Password) {
        username = Username;
        password = Password;
        Crashlytics.log(Log.DEBUG, "username", Username);
        Crashlytics.log(Log.DEBUG, "password", Password);
        SendRequest sr = new SendRequest();
        try {
            //get token and student id
            JSONObject json = new JSONObject();
            json.put("student_number", Username);
            json.put("password", Password);
            JSONObject respJson = sr.sendJson("https://ta.yrdsb.ca/v4/students/json.php", json.toString()).getJSONObject(0);
            System.out.println(respJson + "JSON RESPONSE HERE<----");
            if(respJson == null){
                Crashlytics.log(Log.ERROR, "network request failed", "line 165 TA");
                return null;
            }

            session_token = respJson.getString("token");
            student_id = respJson.getString("student_id");


            //get marks and course code
            JSONObject marksJson = new JSONObject();
            marksJson.put("token", session_token);
            marksJson.put("student_id", student_id);
            marksJson.put("subject_id", 0);
            JSONArray marksResp = sr.sendJson("https://ta.yrdsb.ca/v4/students/json.php", marksJson.toString())
                    .getJSONObject(0)
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getJSONArray("subjects");
            if(marksResp == null){
                Crashlytics.log(Log.ERROR, "network request failed", "line 184 TA");
                return null;
            }

            LinkedHashMap<String, List<String>> MarksNotifications = new LinkedHashMap<>();
            for (int i = 0; i < marksResp.length(); i++) {
                ArrayList<String> fields = new ArrayList<>();
                JSONObject subject = marksResp.getJSONObject(i);
                if(subject.getString("mark").equals("Please see teacher for current status regarding achievement in the course")) {
                    fields.add(subject.getString("course"));
                    MarksNotifications.put("NA"+i, fields);
                }else{
                    if(false){ //for debugging
                        fields.add(subject.getString("mark").replaceAll("%", "").replaceAll(" ", "").replaceAll("8", "7"));
                    }else {
                        fields.add(subject.getString("mark").replaceAll("%", "").replaceAll(" ", ""));
                    }
                    fields.add(subject.getString("course"));
                    MarksNotifications.put(subject.getString("subject_id"), fields);
                }
                subjects.add(subject.getString("subject_id"));

            }
            System.out.println(MarksNotifications+"marks NOTIFICATION RESPONSE HERE<----");
            return MarksNotifications;



        }catch (Exception e){
            e.printStackTrace();
            Crashlytics.log(Log.ERROR, "error in GetTaNotifications", Arrays.toString(e.getStackTrace()));
            return null;
        }
    }


    public Double GetAverage(HashMap<String, List<String>> Marks){

        //Get average
        double Average = 0;
        int x = 0;
        try {
            for (Map.Entry<String, List<String>> entry : Marks.entrySet()) {
                if (!entry.getKey().contains("NA")) {
                    x++;
                }
            }
        }catch (Exception e){
            Crashlytics.log(Log.ERROR, "Error in GetAverage TA()", "line 232 TA");
            return null;
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


    public String GetCourse(int subject_number){
        String subject_id = subjects.get(subject_number);
        String course = Marks.get(subject_id).get(1);
        return course;
    }


    public List<JSONObject> newGetMarks(int subject_number) {
        SendRequest sr = new SendRequest();
        Crashlytics.log(Log.DEBUG, "username", username);
        Crashlytics.log(Log.DEBUG, "password", password);
        try {
            //get marks
            JSONObject json = new JSONObject();
            json.put("token", session_token);
            json.put("student_id", student_id);
            json.put("subject_id", subjects.get(subject_number));
            JSONObject respJsonAssignments = sr.sendJson("https://ta.yrdsb.ca/v4/students/json.php", json.toString())
                    .getJSONObject(0)
                    .getJSONObject("data");
            if(respJsonAssignments == null){
                Crashlytics.log(Log.ERROR, "network request failed", "line 275 TA");
                return null;
            }
            JSONObject respJsonName = respJsonAssignments;
            try {
                respJsonAssignments = respJsonAssignments
                        .getJSONObject("assessment")
                        .getJSONObject("data");
            }catch (Exception e){
                Crashlytics.log(Log.ERROR, "network request probably failed", "line 284 TA");
                return null;
            }
            System.out.println(respJsonAssignments + "JSON RESPONSE HERE<----");
            List respJsonList = Arrays.asList(respJsonAssignments, respJsonName);
            return respJsonList;

        }catch (Exception e){
            e.printStackTrace();
            Crashlytics.log(Log.ERROR, "Error in newGetMarks", "line 293 TA");
            return null;
        }

    }


    public LinkedHashMap<String, Double> GetCourseWeights(){ //doesnt need course parameter because its being called with the same class instance as GetMarks
        LinkedHashMap<String, Double> weights = new LinkedHashMap();
        String response = marksResponse[0];
        Double Knowledge;
        Double Thinking;
        Double Communication;
        Double Application;
        try {
            response = response.split("<th>Course Weighting</th>")[1].split("</table>")[0];
            String knowledge = response.split("Knowledge/Understanding")[1].split("\"right\">")[1].split("%</td>")[0];
            String thinking = response.split("Thinking")[1].split("\"right\">")[1].split("%</td>")[0];
            String communication = response.split("Communication")[1].split("\"right\">")[1].split("%</td>")[0];
            String application = response.split("Application")[1].split("\"right\">")[1].split("%</td>")[0];

            Knowledge = Double.parseDouble(knowledge);
            Thinking = Double.parseDouble(thinking);
            Communication = Double.parseDouble(communication);
            Application = Double.parseDouble(application);
        }catch (ArrayIndexOutOfBoundsException e){
            Knowledge = 1.0;
            Thinking = 1.0;
            Communication = 1.0;
            Application = 1.0;
        }



        weights.put("Knowledge", Knowledge);
        weights.put("Thinking", Thinking);
        weights.put("Communication", Communication);
        weights.put("Application", Application);
        return weights;
    }

}

