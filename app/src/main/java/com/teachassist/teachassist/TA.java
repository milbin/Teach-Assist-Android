package com.teachassist.teachassist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.components.LimitLine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            JSONObject respJson = sr.sendJson("https://ta.yrdsb.ca/v4/students/json-20180628.php", json.toString()).getJSONObject(0);
            if(respJson == null){
                LinkedHashMap<String, List<String>> resp2 = GetTAData2(username, password);
                if(resp2 != null){
                    return resp2;
                }
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
            JSONArray marksResp = sr.sendJson("https://ta.yrdsb.ca/v4/students/json-20180628.php", marksJson.toString())
                    .getJSONObject(0)
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getJSONArray("subjects");
            if(marksResp == null){
                LinkedHashMap<String, List<String>> resp2 = GetTAData2(username, password);
                if(resp2 != null){
                    return resp2;
                }
                Crashlytics.log(Log.ERROR, "network request failed", "line 77 TA");
                return null;
            }
            Marks = new LinkedHashMap<>();
            for (int i = 0; i < marksResp.length(); i++) {
                ArrayList<String> fields = new ArrayList<>();
                JSONObject subject = marksResp.getJSONObject(i);
                subjects.add(subject.getString("subject_id"));
                if(subject.getString("mark").equals("Please see teacher for current status regarding achievement in the course")) {
                    fields.add(subject.getString("course"));
                    Marks.put("NA"+i, fields);
                }else{
                    if(subject.getString("mark").contains("Level") || subject.getString("mark").contains("level")|| subject.getString("mark").contains("Click") || subject.getString("mark").contains("click")) {
                        fields.add(CalculateAverageFromMarksView(newGetMarks(i).get(0), 0));
                    }else {
                        fields.add(subject.getString("mark").replaceAll("%", "").replaceAll(",", ".").replaceAll(" ", ""));
                    }
                    fields.add(subject.getString("course"));
                    Marks.put(subject.getString("subject_id"), fields);
                }

            }



            //get room number and course name
            String url = "https://ta.yrdsb.ca/live/index.php?";
            String path = "/live/index.php?";
            HashMap<String, String> headers = new HashMap<>();
            LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
            HashMap<String, String> cookies = new HashMap<>();
            parameters.put("subject_id", "0");
            parameters.put("username", Username);
            parameters.put("password", Password);
            cookies.put("session_token", session_token);
            cookies.put("student_id", student_id);
            String[] resp = sr.send(url, headers, parameters, cookies, path, true);
            if(resp == null){
                LinkedHashMap<String, List<String>> resp2 = GetTAData2(username, password);
                if(resp2 != null){
                    return resp2;
                }
                Crashlytics.log(Log.ERROR, "network request failed", "line 112 TA");
                return null;
            }

            //parse return
            int courseCounter1 = 0;
            for(String i :resp[0].split("<td>")){
                if((i.contains("current mark = ") || i.contains("Please see teacher for current status regarding achievement in the course")||i.contains("Click Here")||i.contains("Level")||i.contains("Block")) && !i.contains("0000-00-00")) {
                    String Course_Name = i.split(":")[1].split("<br>")[0].trim();
                    String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                    String course = i.split(" :")[0].trim();
                    //String Subject_id = i.split("subject_id=")[1].split("&")[0].trim();
                    int courseCounter2 = 0;
                    if(courseCounter1>=Marks.size()){
                        ArrayList<String> fields = new ArrayList<>();
                        fields.add(course);
                        fields.add(Course_Name);
                        fields.add(Room_Number);
                        Marks.put("NA"+courseCounter1, fields);
                    }else {
                        for (Map.Entry<String, List<String>> entry : Marks.entrySet()) {
                            if (courseCounter1 == courseCounter2) {
                                entry.getValue().add(Course_Name);
                                entry.getValue().add(Room_Number);
                            }
                            courseCounter2++;

                        }
                    }
                    courseCounter1++;
                }


                    //Marks.put(Subject_id, Stats);

            }
            System.out.println(Marks+"marks RESPONSE HERE<----");
            return Marks;



        }catch (Exception e){
            LinkedHashMap<String, List<String>> resp2 = GetTAData2(username, password);
            if(resp2 != null){
                return resp2;
            }
            e.printStackTrace();
            Crashlytics.log(Log.ERROR, "Error in GetTaData in TA()", Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public LinkedHashMap<String, List<String>> GetTAData2(String Username, String Password){
        try {
            Crashlytics.log(Log.DEBUG, "username", Username);
            Crashlytics.log(Log.DEBUG, "password", Password);

            //get session token and student id
            SendRequest sr = new SendRequest();
            String url = "https://ta.yrdsb.ca/live/index.php?";
            String path = "/live/index.php?";
            HashMap<String, String> headers = new HashMap<>();
            LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
            HashMap<String, String> cookies = new HashMap<>();
            parameters.put("subject_id", "0");
            parameters.put("username", Username);
            parameters.put("password", Password);
            parameters.put("submit", "Login");

            String[] response = sr.send(url, headers, parameters, cookies, path, false);
            if(response == null){
                return null;
            }
            for(String i:response){
                if(i.contains("session_token")) {
                    session_token = i.split("=")[1].split(";")[0];
                }else if(i.contains("student_id")){
                    student_id = i.split("=")[1].split(";")[0];
                }
            }
            if(session_token == null || session_token.isEmpty() || student_id == null || student_id.isEmpty()){
                return null;
            }

            try {
                cookies.put("session_token", session_token);
                cookies.put("student_id", student_id);
                parameters.put("student_id ", student_id);

                String[] resp = sr.send(url, headers, parameters, cookies, path, true);
                if(resp == null){
                    return null;
                }

                Marks = new LinkedHashMap();

                int courseNum = 0;
                int numberOfEmptyCourses = 0;
                for(String i :resp[0].split("<td>")){
                    System.out.println("HERE");
                    if(i.contains("<td align=\"right\">\n\t\t</td>")){
                        System.out.println(i);
                    }
                    if(i.contains("current mark = ")){
                        String Subject_id = i.split("subject_id=")[1].split("&")[0].trim();
                        String Current_mark = i.split("current mark = ")[1].split("%")[0].trim();
                        System.out.println(Current_mark);
                        String Course_Name = i.split(":")[0].trim();
                        String Course_code = i.split(":")[1].split("<br>")[0].trim();
                        String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                        List Stats = new ArrayList<>();
                        Stats.add(Current_mark);
                        Stats.add(Course_Name);
                        Stats.add(Course_code);
                        Stats.add(Room_Number);
                        courseNum++;

                        subjects.add(Subject_id);
                        Marks.put(Subject_id, Stats);
                    }
                    else if(i.contains("Please see teacher for current status regarding achievement in the course")|| i.contains("<td align=\"right\">\n\t\t</td>")){
                        ArrayList<String> Stats = new ArrayList<>();
                        String Course_Name = i.split(":")[0].trim();
                        String Course_code = i.split(":")[1].split("<br>")[0].trim();
                        String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                        Stats.add(Course_Name);
                        Stats.add(Course_code);
                        Stats.add(Room_Number);

                        courseNum++;
                        numberOfEmptyCourses++;
                        subjects.add("0");
                        Marks.put("NA"+numberOfEmptyCourses, Stats);
                    }
                    else if(i.contains("Click Here") || i.contains("Level")){
                        String Subject_id = i.split("subject_id=")[1].split("&")[0].trim();
                        subjects.add(Subject_id);


                        String Current_mark = CalculateAverageFromMarksView(newGetMarks(courseNum).get(0), 0);
                        String Course_Name = i.split(":")[0].trim();
                        String Course_code = i.split(":")[1].split("<br>")[0].trim();
                        String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                        List Stats = new ArrayList<>();
                        Stats.add(Current_mark);
                        Stats.add(Course_Name);
                        Stats.add(Course_code);
                        Stats.add(Room_Number);
                        courseNum++;

                        Marks.put(Subject_id, Stats);
                    }

                }
                return Marks;

            }
            catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
                return null;
            }




        }
        catch(IOException e) {
            e.printStackTrace();
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
                if(entry.getValue().get(0).contains("Level") || entry.getValue().get(0).contains("level") || entry.getValue().get(0).contains("Click") || entry.getValue().get(0).contains("click")){
                    grades[i] = Double.parseDouble(CalculateAverageFromMarksView(newGetMarks(counter).get(0), 0));
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
            JSONObject respJsonAssignments = sr.sendJson("https://ta.yrdsb.ca/v4/students/json-20180628.php", json.toString())
                    .getJSONObject(0)
                    .getJSONObject("data");
            if(respJsonAssignments == null){
                List<JSONObject> resp2 = GetMarks(subject_number);
                if(resp2 != null){
                    return resp2;
                }
                Crashlytics.log(Log.ERROR, "network request failed", "line 275 TA");
                return null;
            }
            JSONObject respJsonName = respJsonAssignments;
            try {
                respJsonAssignments = respJsonAssignments
                        .getJSONObject("assessment")
                        .getJSONObject("data");
            }catch (Exception e){
                List<JSONObject> resp2 = GetMarks(subject_number);
                if(resp2 != null){
                    return resp2;
                }
                Crashlytics.log(Log.ERROR, "network request probably failed", "line 284 TA");
                return null;
            }
            System.out.println(respJsonAssignments + "JSON RESPONSE HERE<----");
            List respJsonList = Arrays.asList(respJsonAssignments, respJsonName);
            return respJsonList;

        }catch (Exception e){
            List<JSONObject> resp2 = GetMarks(subject_number);
            if(resp2 != null){
                return resp2;
            }
            e.printStackTrace();
            Crashlytics.log(Log.ERROR, "Error in newGetMarks", "line 293 TA");
            return null;
        }

    }

    public List<JSONObject> GetMarks(int subject_number){
        try {
            if(subject_number >=0) {
                String url = "https://ta.yrdsb.ca/live/students/viewReport.php?";
                String path = "/live/students/viewReport.php?";
                LinkedHashMap<String, String> headers = new LinkedHashMap<>();
                LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
                LinkedHashMap<String, String> cookies = new LinkedHashMap<>();
                parameters.put("subject_id", subjects.get(subject_number));
                cookies.put("token", session_token);
                parameters.put("student_id", student_id);
                cookies.put("session_token", session_token);
                cookies.put("student_id", student_id);

                //get response
                SendRequest sr = new SendRequest();
                marksResponse = sr.send(url, headers, parameters, cookies, path, true);
                if(marksResponse == null || marksResponse.length == 0 || marksResponse[0].split("<h2>").length <= 1){
                    return null;
                }
                String courseName = marksResponse[0].split("<h2>")[1].split("</h2>")[0];
                int assignmentNumber = 0;
                JSONObject assignments = new JSONObject();
                for (String i : marksResponse[0].split("rowspan=")){
                    JSONObject assignment = new JSONObject();

                    if(i.contains("bgcolor=\"white\"")) {
                        try {
                            String title = i.split("\"2\">")[1].split("</td>")[0];
                            assignment.put("title", title);
                            assignment.put("feedback", "");
                            ArrayList<String> categories = new ArrayList<String>(Arrays.asList("K", "T", "C", "A", ""));
                            int categoryNumber = -1;
                            for (String j : i.split(" align=\"center\">")) {
                                if (categoryNumber < 0) {
                                    categoryNumber++;
                                    continue;
                                } else if (j.contains("<td colspan=\"4\"")) { //feedback
                                    if (j.contains("Feedback:")) {
                                        String feedback = j.split("Feedback:")[1].split("</td>")[0]
                                                .replace("<br>", "")
                                                .replace("\n", "");
                                        assignment.put("feedback", feedback);
                                    }
                                }
                                String regexFloat = "[+-]?([0-9]*[.])?[0-9]+";
                                Pattern regex = Pattern.compile(regexFloat + "\\s/\\s" + regexFloat + "\\s.\\s"); //this will capture any number not just an int
                                Matcher matcher = regex.matcher(j);
                                if (matcher.find()) {
                                    assignment.put(categories.get(categoryNumber), new JSONObject());
                                    String markString = matcher.group(0);
                                    String mark = markString.split(" / ")[0].trim();
                                    String outOf = markString.split(" / ")[1].split("=")[0].trim();
                                    String weight = j.split("<font size=\"-2\">")[1].split("</")[0].trim();
                                    try {
                                        Double.parseDouble(mark); //see if this raises an error
                                        ((JSONObject) assignment.get(categories.get(categoryNumber))).put("mark", mark);
                                    } catch (NumberFormatException e) {
                                        ((JSONObject) assignment.get(categories.get(categoryNumber))).put("mark", "");
                                    }
                                    try {
                                        Double.parseDouble(outOf); //see if this raises an error
                                        ((JSONObject) assignment.get(categories.get(categoryNumber))).put("outOf", outOf);
                                    } catch (NumberFormatException e) {
                                        ((JSONObject) assignment.get(categories.get(categoryNumber))).put("outOf", "");
                                    }
                                    if (weight.contains("weight=")) {
                                        ((JSONObject) assignment.get(categories.get(categoryNumber))).put("weight", weight.split("weight=")[1]);
                                    } else if (weight.contains("no weight")) {
                                        ((JSONObject) assignment.get(categories.get(categoryNumber))).put("weight", "0");
                                    }
                                } else if (j.contains("No Mark") || j.contains("No mark") || j.contains("no Mark") || j.contains("no mark")) {
                                    assignment.put(categories.get(categoryNumber), new JSONObject());
                                    ((JSONObject) assignment.get(categories.get(categoryNumber))).put("mark", "no mark");
                                    ((JSONObject) assignment.get(categories.get(categoryNumber))).put("outOf", "");
                                    ((JSONObject) assignment.get(categories.get(categoryNumber))).put("weight", "");
                                }
                                assignments.put(String.valueOf(assignmentNumber), assignment);
                                categoryNumber++;

                            }
                        }catch (JSONException e){
                            System.out.println("HERE");
                        }
                        assignmentNumber++;
                    }
                    if(marksResponse[0].split("rowspan=").length == assignmentNumber+1){
                        Double k;
                        Double t;
                        Double c;
                        Double a;
                        try {
                            k = Double.parseDouble(i.split("<td>Knowledge/Understanding</td>")[1].split(">")[1].split("%<")[0]) / 100;
                            t = Double.parseDouble(i.split("<td>Thinking</td>")[1].split(">")[1].split("%<")[0]) / 100;
                            c = Double.parseDouble(i.split("<td>Communication</td>")[1].split(">")[1].split("%<")[0]) / 100;
                            a = Double.parseDouble(i.split("<td>Application</td>")[1].split(">")[1].split("%<")[0]) / 100;
                        }catch (ArrayIndexOutOfBoundsException ignore){
                            k = 25.0;
                            t = 25.0;
                            c = 25.0;
                            a = 25.0;
                        }
                        try {
                            JSONObject categories = new JSONObject();
                            categories.put("K", k);
                            categories.put("T", t);
                            categories.put("C", c);
                            categories.put("A", a);
                            assignments.put("categories", categories);
                        }catch (JSONException e){}
                    }

                }
                List<JSONObject> returnList = new ArrayList<>();
                returnList.add(assignments);
                JSONObject courseNameJson = new JSONObject();
                try{
                    courseNameJson.put("course", courseName);
                }catch (JSONException e){}
                returnList.add(courseNameJson); //as far as i can tell this second item in the list is probably useless
                return returnList;
            }else{
                return null;
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String CalculateAverageFromMarksView(JSONObject marks, int numberOfRemovedAssignments) { //CalculateTotalAverage
        DecimalFormat round = new DecimalFormat(".#");
        try {
            boolean markExists = false;
            JSONObject weights = marks.getJSONObject("categories");
            Double weightK = weights.getDouble("K") * 10 * 0.7;
            Double weightT = weights.getDouble("T") * 10 * 0.7;
            Double weightC = weights.getDouble("C") * 10 * 0.7;
            Double weightA = weights.getDouble("A") * 10 * 0.7;
            Double weightS = 0.3*10;
            Double Kmark = 0.0;
            Double Tmark = 0.0;
            Double Cmark = 0.0;
            Double Amark = 0.0;
            Double Smark = 0.0;
            Double KweightAssignment = 0.0;
            Double TweightAssignment = 0.0;
            Double CweightAssignment = 0.0;
            Double AweightAssignment = 0.0;
            Double SweightAssignment = 0.0;
            Double KweightAssignmentTemp;
            Double TweightAssignmentTemp;
            Double CweightAssignmentTemp;
            Double AweightAssignmentTemp;
            Double SweightAssignmentTemp;
            for (int i = 0; i < marks.length()-1+numberOfRemovedAssignments; i++) {
                JSONObject assignment;
                try {
                    assignment = marks.getJSONObject(String.valueOf(i));
                }catch (JSONException e){
                    continue;}

                try {
                    if (!assignment.getJSONObject("K").isNull("mark") && !assignment.getJSONObject("K").getString("mark").equals("no mark")) {
                        Double assignmentK = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                        markExists = true;
                        KweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("K").getString("weight"));
                        if(KweightAssignmentTemp != -1.0) {
                            Kmark += assignmentK * KweightAssignmentTemp;
                            KweightAssignment += KweightAssignmentTemp;
                        }
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("T").isNull("mark") && !assignment.getJSONObject("T").getString("mark").equals("no mark")) {
                        Double assignmentT = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                        TweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("T").getString("weight"));
                        markExists = true;
                        if(TweightAssignmentTemp != -1.0) {
                            Tmark += assignmentT * TweightAssignmentTemp;
                            TweightAssignment += TweightAssignmentTemp;
                        }
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("C").isNull("mark") && !assignment.getJSONObject("C").getString("mark").equals("no mark")) {
                        Double assignmentC = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                        CweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("C").getString("weight"));
                        markExists = true;
                        if(CweightAssignmentTemp != -1.0) {
                            Cmark += assignmentC * CweightAssignmentTemp;
                            CweightAssignment += CweightAssignmentTemp;
                        }
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("A").isNull("mark") && !assignment.getJSONObject("A").getString("mark").equals("no mark")) {
                        Double assignmentA = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                        AweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("A").getString("weight"));
                        markExists = true;
                        if(AweightAssignmentTemp != -1.0) {
                            Amark += assignmentA * AweightAssignmentTemp;
                            AweightAssignment += AweightAssignmentTemp;
                        }
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("").isNull("mark") && !assignment.getJSONObject("").getString("mark").equals("no mark")) {
                        Double assignmentS = Double.parseDouble(assignment.getJSONObject("").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("").getString("outOf"));
                        SweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("").getString("weight"));
                        markExists = true;
                        if(SweightAssignmentTemp != -1.0) {
                            Smark += assignmentS * SweightAssignmentTemp;
                            SweightAssignment += SweightAssignmentTemp;
                        }
                    }
                }catch (JSONException e){}

            }

            if(KweightAssignment == 0.0){
                Kmark = 0.0;
                weightK = 0.0;
            }else {
                Kmark /= KweightAssignment;
            }

            if(TweightAssignment == 0.0){
                Tmark = 0.0;
                weightT = 0.0;
            }else {
                Tmark /= TweightAssignment;
            }

            if(CweightAssignment == 0.0){
                Cmark = 0.0;
                weightC = 0.0;
            }else {
                Cmark /= CweightAssignment;
            }

            if(AweightAssignment == 0.0){
                Amark = 0.0;
                weightA = 0.0;
            }else {
                Amark /= AweightAssignment;
            }
            if(SweightAssignment == 0.0){
                Smark = 0.0;
                weightS = 0.0;
            }else {
                Smark /= SweightAssignment;
            }

            Kmark *= weightK;
            Tmark *= weightT;
            Cmark *= weightC;
            Amark *= weightA;
            Smark *= weightS;

            Double AverageDouble = (Kmark + Tmark + Cmark + Amark +Smark) / (weightK + weightT + weightC + weightA +weightS) * 100;
            if(AverageDouble.isNaN() && markExists){
                return "0.0";
            }
            return String.valueOf(round.format(AverageDouble).replaceAll(",", "."));

        }catch (JSONException e){
            e.printStackTrace();
            Crashlytics.log(Log.ERROR, "MarksViewMaterial Calculate total average returns null", Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

}

