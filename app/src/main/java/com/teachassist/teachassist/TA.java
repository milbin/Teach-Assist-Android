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
import java.text.DecimalFormat;
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
            System.out.println(marksResp);
            Marks = new LinkedHashMap<>();
            for (int i = 0; i < marksResp.length(); i++) {
                ArrayList<String> fields = new ArrayList<>();
                JSONObject subject = marksResp.getJSONObject(i);
                subjects.add(subject.getString("subject_id"));
                if(subject.getString("mark").equals("Please see teacher for current status regarding achievement in the course")) {
                    fields.add(subject.getString("course"));
                    Marks.put("NA"+i, fields);
                }else{
                    if(subject.getString("mark").contains("Level") || subject.getString("mark").contains("level")) {
                        fields.add(String.valueOf(CalculateAverageFromMarksView(i)));
                    }else {
                        fields.add(subject.getString("mark").replaceAll("%", "").replaceAll(" ", ""));
                    }
                    fields.add(subject.getString("course"));
                    Marks.put(subject.getString("subject_id"), fields);
                }

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
        int counter = 0;
        for (Map.Entry<String, List<String>> entry : Marks.entrySet()) {
            if (!entry.getKey().contains("NA")) {
                if(entry.getValue().get(0).contains("Level") || entry.getValue().get(0).contains("level")){
                    grades[i] = CalculateAverageFromMarksView(counter);
                }else {
                    grades[i] = Double.parseDouble(entry.getValue().get(0));
                }

                i++;
            }
            counter++;


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

            json.put("student_id", student_id);
            json.put("token", session_token);
            json.put("subject_id", subjects.get(subject_number));
            System.out.println(subjects);
            System.out.println(json);
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

    private Double CalculateAverageFromMarksView(int subjectNumber){
        List<JSONObject> marks = newGetMarks(subjectNumber);
        Double knowledge = 0.0;
        Double thinking = 0.0;
        Double communication = 0.0;
        Double application = 0.0;

        Double totalWeightKnowledge = 0.0;
        Double totalWeightThinking = 0.0;
        Double totalWeightCommunication = 0.0;
        Double totalWeightApplication = 0.0;
        try {
            JSONObject weights = marks.get(0).getJSONObject("categories");
            System.out.println(marks.get(0).length()-1);
            for(int i = 0; i <marks.get(0).length()-1; i++){
                JSONObject assignment = marks.get(0).getJSONObject(String.valueOf(i));
                Double markK = 0.0;
                Double outOfK = 0.0;
                Double weightK = -0.1;
                try{markK = Double.parseDouble(assignment.getJSONObject("K").getString("mark"));}catch(Exception e){weightK = 0.0;}
                try{outOfK = Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));}catch(Exception e){}
                if(weightK == -0.1){
                    try{weightK = Double.parseDouble(assignment.getJSONObject("K").getString("weight"));}catch(Exception e){}
                }
                if(outOfK != 0.0) {
                    knowledge += markK / outOfK * weightK;
                    totalWeightKnowledge += weightK;
                }

                Double markT = 0.0;
                Double outOfT = 0.0;
                Double weightT = -0.1;
                try{markT = Double.parseDouble(assignment.getJSONObject("T").getString("mark"));}catch(Exception e){weightT = 0.0;}
                try{outOfT = Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));}catch(Exception e){}
                if(weightT == -0.1){
                    try{weightT = Double.parseDouble(assignment.getJSONObject("T").getString("weight"));}catch(Exception e){}
                }
                if(outOfT != 0.0) {
                    thinking += markT / outOfT * weightT;
                    totalWeightThinking += weightT;
                }
                Double markC = 0.0;
                Double outOfC = 0.0;
                Double weightC = -0.1;
                try{markC = Double.parseDouble(assignment.getJSONObject("C").getString("mark"));}catch(Exception e){weightC = 0.0;}
                try{outOfC = Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));}catch(Exception e){}
                if(weightC == -0.1){
                    try{weightC = Double.parseDouble(assignment.getJSONObject("C").getString("weight"));}catch(Exception e){}
                }
                if(outOfC != 0.0) {
                    communication += markC / outOfC * weightC;
                    totalWeightCommunication += weightC;
                }

                Double markA = 0.0;
                Double outOfA = 0.0;
                Double weightA = -0.1;
                try{markA = Double.parseDouble(assignment.getJSONObject("A").getString("mark"));}catch(Exception e){weightA = 0.0;}
                try{outOfA = Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));}catch(Exception e){}
                if(weightA == -0.1){
                    try{weightA = Double.parseDouble(assignment.getJSONObject("A").getString("weight"));}catch(Exception e){}
                }
                if(outOfA != 0.0) {
                    application += markA / outOfA * weightA;
                    totalWeightApplication += weightA;
                }
            }
            Double Knowledge = Double.parseDouble(weights.getString("K"));
            Double Thinking = Double.parseDouble(weights.getString("T"));
            Double Communication = Double.parseDouble(weights.getString("C"));
            Double Application = Double.parseDouble(weights.getString("A"));

            Double finalKnowledge;
            Double finalThinking;
            Double finalCommunication;
            Double finalApplication;

            //omit category if there is no assignment in it
            if(totalWeightKnowledge != 0.0) {
                finalKnowledge = knowledge / totalWeightKnowledge;
            }else{
                finalKnowledge = 0.0;
                Knowledge = 0.0;
            }
            if(totalWeightThinking != 0.0) {
                finalThinking = thinking/totalWeightThinking;
            }else{
                finalThinking = 0.0;
                Thinking = 0.0;
            }
            if(totalWeightCommunication != 0.0) {
                finalCommunication = communication/totalWeightCommunication;
            }else{
                finalCommunication = 0.0;
                Communication = 0.0;
            }
            if(totalWeightApplication != 0.0) {
                finalApplication = application/totalWeightApplication;
            }else{
                finalApplication = 0.0;
                Application = 0.0;
            }
            finalKnowledge = finalKnowledge*Knowledge;
            finalThinking = finalThinking*Thinking;
            finalCommunication = finalCommunication*Communication;
            finalApplication = finalApplication*Application;

            Double Average = (finalApplication + finalKnowledge + finalThinking +finalCommunication) / (Knowledge+Thinking+Communication+Application)*100;
            return DoubleRounder.round(Average, 1);
        }catch (JSONException e){
            e.printStackTrace();
            return 0.0;
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

