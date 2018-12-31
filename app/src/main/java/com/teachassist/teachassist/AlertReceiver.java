package com.teachassist.teachassist;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.decimal4j.util.DoubleRounder;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.teachassist.teachassist.App.CHANNEL_1_ID;
import static com.teachassist.teachassist.App.CHANNEL_2_ID;
import static com.teachassist.teachassist.App.CHANNEL_3_ID;
import static com.teachassist.teachassist.App.CHANNEL_4_ID;
import static com.teachassist.teachassist.LaunchActivity.CREDENTIALS;
import static com.teachassist.teachassist.LaunchActivity.PASSWORD;
import static com.teachassist.teachassist.LaunchActivity.USERNAME;
import static com.teachassist.teachassist.SettingsActivity.PrefsFragment.ALLNOTIFICATIONS;
import static com.teachassist.teachassist.SettingsActivity.PrefsFragment.NOTIFICATION1;
import static com.teachassist.teachassist.SettingsActivity.PrefsFragment.NOTIFICATION2;
import static com.teachassist.teachassist.SettingsActivity.PrefsFragment.NOTIFICATION3;
import static com.teachassist.teachassist.SettingsActivity.PrefsFragment.NOTIFICATION4;

public class AlertReceiver extends BroadcastReceiver {

    LinkedHashMap<String, List<String>> response;
    String username;
    String password;
    Context Globalcontext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Globalcontext = context;
        Crashlytics.setUserIdentifier(username);
        Crashlytics.setString("username", username);
        Crashlytics.setString("password", password);

        //get old response
        SharedPreferences prefs = context.getSharedPreferences("RESPONSE", MODE_PRIVATE);
        String str = prefs.getString("RESPONSE", "");
        Gson gson = new Gson();
        Type entityType = new TypeToken<LinkedHashMap<String, List<String>>>() {
        }.getType();
        response = gson.fromJson(str, entityType);
/*
        ArrayList list1 = new ArrayList<>(Arrays.asList("64.2", "AVI3M1-01", "Visual Arts", "169"));
        ArrayList list2 = new ArrayList<>(Arrays.asList("93.7", "SPH3U1-01", "Physics", "167"));
        ArrayList list3 = new ArrayList<>(Arrays.asList("80.6", "FIF3U1-01", "", "214"));
        ArrayList list4 = new ArrayList<>(Arrays.asList("87.5", "MCR3U1-01", "Functions and Relations", "142"));
        response = new LinkedHashMap<>();
        response.put("283098", list1);
        response.put("283003", list2);
        response.put("283001", list3);
        response.put("NA", list4);*/


        System.out.println("NOTIFICATION" + response);


        //get username and password
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME, "");
        password = sharedPreferences.getString(PASSWORD, "");
        if(!username.isEmpty() && !password.isEmpty()) {
            new GetTaData().execute(username, password);
        }


    }

    private class GetTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params) {
            TA ta = new TA();
            LinkedHashMap<String, List<String>> newResponse = ta.GetTADataNotifications(username, password);
/*
            ta.GetTAData(username, password);
            ArrayList list1 = new ArrayList<>(Arrays.asList("64.2", "AVI3M1-01", "Visual Arts", "169"));
            ArrayList list2 = new ArrayList<>(Arrays.asList("93.7", "SPH3U1-01", "Physics", "167"));
            ArrayList list3 = new ArrayList<>(Arrays.asList("80.6", "FIF3U1-01", "", "214"));
            ArrayList list4 = new ArrayList<>(Arrays.asList("87.4", "MCR3U1-01", "Functions and Relations", "142"));
            LinkedHashMap<String, List<String>> newResponse = new LinkedHashMap<>();
            newResponse.put("283098", list1);
            newResponse.put("283003", list2);
            newResponse.put("283001", list3);
            newResponse.put("283152", list4);
            */




            try {
                Date StartTime = new SimpleDateFormat("HH:mm:ss").parse("7:00:00");
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(StartTime);

                Date endTime = new SimpleDateFormat("HH:mm:ss").parse("24:00:00");
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(endTime);
                calendarEnd.add(Calendar.DATE, 1);

                //if you dont have this the currentTime calendar is in the current year while the other calendars are in 1970
                String someRandomTime = "01:00:00";
                Date d = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);
                Calendar calendar3 = Calendar.getInstance();
                calendar3.setTime(d);
                calendar3.add(Calendar.DATE, 1);

                Date currentTime = calendar3.getTime();

                if (currentTime.after(calendarStart.getTime()) && currentTime.before(calendarEnd.getTime())) {


                    LinkedList<String> toSend = new LinkedList<String>();
                    for (LinkedHashMap.Entry<String, List<String>> entry : response.entrySet()) {
                        if (entry.getKey().contains("NA")) {
                            toSend.add(entry.getKey());
                        } else {
                            toSend.add(entry.getValue().get(0));
                        }

                    }
                    int course = 0;
                    for (LinkedHashMap.Entry<String, List<String>> entry : newResponse.entrySet()) {
                        if (entry.getKey().contains("NA")) {
                            //toSend.remove(entry.getKey());
                        } else if (!entry.getValue().get(0).equals(toSend.get(course)) || toSend.get(course).toString().contains("NA")) { // idk why u gotta add toString here
                            System.out.println(entry.getValue().get(0));
                            System.out.println(toSend.get(course)+"HERE");
                            String courseName = entry.getValue().get(1);
                            JSONObject marks;
                            List<JSONObject> returnVal;

                            if (course == 0) {
                                returnVal = ta.newGetMarks(0);
                                if(returnVal == null){
                                    continue;
                                }else{
                                    marks = returnVal.get(0);
                                }
                                String assignmentName = "";
                                String assignmentAverage = "";
                                try {
                                    int assignmentNumber = marks.length() - 2;
                                    JSONObject assignment = marks.getJSONObject(String.valueOf(assignmentNumber));
                                        if (assignmentNumber == marks.length() - 2) {
                                            assignmentName = assignment.getString("title");
                                            assignmentAverage = CalculateAverage(marks, String.valueOf(assignmentNumber));
                                        }

                                    SendNotifications sendNotifications = new SendNotifications(Globalcontext);
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Globalcontext);
                                    Boolean enabledNotifications = sharedPreferences.getBoolean(NOTIFICATION1, true);
                                    if (enabledNotifications) {
                                        Notification notification = sendNotifications.sendOnChannel(CHANNEL_1_ID,
                                                MarksView.class, 0, "New Assignment posted in: " + courseName,
                                                "You Got a " + assignmentAverage + "% in " + assignmentName, toSend.get(course));
                                        sendNotifications.getManager().notify(1, notification);
                                        System.out.println("SENT NOTIFICATION");

                                    }

                                } catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }

                            else if (course == 1) {
                                returnVal = ta.newGetMarks(1);
                                if(returnVal == null){
                                    continue;
                                }else{
                                    marks = returnVal.get(0);
                                }
                                String assignmentName = "";
                                String assignmentAverage = "";
                                try {
                                    int assignmentNumber = marks.length() - 2;
                                    JSONObject assignment = marks.getJSONObject(String.valueOf(assignmentNumber));
                                    if (assignmentNumber == marks.length() - 2) {
                                        assignmentName = assignment.getString("title");
                                        assignmentAverage = CalculateAverage(marks, String.valueOf(assignmentNumber));
                                    }

                                    SendNotifications sendNotifications = new SendNotifications(Globalcontext);
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Globalcontext);
                                    Boolean enabledNotifications = sharedPreferences.getBoolean(NOTIFICATION2, true);
                                    if (enabledNotifications) {
                                        Notification notification = sendNotifications.sendOnChannel(CHANNEL_2_ID,
                                                MarksView.class, 1, "New Assignment posted in: " + courseName,
                                                "You Got a " + assignmentAverage + "% in " + assignmentName, toSend.get(course));
                                        sendNotifications.getManager().notify(2, notification);
                                        System.out.println("SENT NOTIFICATION");

                                    }

                                } catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }
                            else if (course == 2) {
                                returnVal = ta.newGetMarks(2);
                                if(returnVal == null){
                                    continue;
                                }else{
                                    marks = returnVal.get(0);
                                }
                                String assignmentName = "";
                                String assignmentAverage = "";
                                try {
                                    int assignmentNumber = marks.length() - 2;
                                    JSONObject assignment = marks.getJSONObject(String.valueOf(assignmentNumber));
                                    if (assignmentNumber == marks.length() - 2) {
                                        assignmentName = assignment.getString("title");
                                        assignmentAverage = CalculateAverage(marks, String.valueOf(assignmentNumber));
                                    }

                                    SendNotifications sendNotifications = new SendNotifications(Globalcontext);
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Globalcontext);
                                    Boolean enabledNotifications = sharedPreferences.getBoolean(NOTIFICATION3, true);
                                    if (enabledNotifications) {
                                        Notification notification = sendNotifications.sendOnChannel(CHANNEL_3_ID,
                                                MarksView.class, 2, "New Assignment posted in: " + courseName,
                                                "You Got a " + assignmentAverage + "% in " + assignmentName, toSend.get(course));
                                        sendNotifications.getManager().notify(3, notification);
                                        System.out.println("SENT NOTIFICATION");

                                    }

                                } catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }
                            else if (course == 3) {
                                returnVal = ta.newGetMarks(3);
                                if(returnVal == null){
                                    continue;
                                }else{
                                    marks = returnVal.get(0);
                                }
                                String assignmentName = "";
                                String assignmentAverage = "";
                                try {
                                    int assignmentNumber = marks.length() - 2;
                                    JSONObject assignment = marks.getJSONObject(String.valueOf(assignmentNumber));
                                    if (assignmentNumber == marks.length() - 2) {
                                        assignmentName = assignment.getString("title");
                                        assignmentAverage = CalculateAverage(marks, String.valueOf(assignmentNumber));
                                    }

                                    SendNotifications sendNotifications = new SendNotifications(Globalcontext);
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Globalcontext);
                                    Boolean enabledNotifications = sharedPreferences.getBoolean(NOTIFICATION2, true);
                                    if (enabledNotifications) {
                                            Notification notification = sendNotifications.sendOnChannel(CHANNEL_4_ID,
                                                    MarksView.class, 3, "New Assignment posted in: " + courseName,
                                                    "You Got a " + assignmentAverage + "% in " + assignmentName, toSend.get(course));
                                            sendNotifications.getManager().notify(4, notification);
                                            System.out.println("SENT NOTIFICATION");

                                    }

                                } catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }
                            /*
                            else if (course == 1) {
                                marks = ta.GetMarks(1);
                                LinkedHashMap<String, Double> weights = ta.GetCourseWeights();
                                Double weightKnowledge = 1.0;
                                Double weightCommunication = 1.0;
                                Double weightThinking = 1.0;
                                Double weightApplication = 1.0;
                                String assignmentName = "";
                                double assignmentAverage = 0.0;
                                double totalWeight = 0.0;
                                int usedCategories = 0;
                                Boolean gotAZero = false;
                                for (LinkedHashMap.Entry<String, Double> weight : weights.entrySet()) {
                                    if(weight.getKey().equals("Knowledge")){
                                        weightKnowledge = weight.getValue();
                                    }
                                    if(weight.getKey().equals("Communication")){
                                        weightCommunication = weight.getValue();
                                    }
                                    if(weight.getKey().equals("Thinking")){
                                        weightThinking = weight.getValue();
                                    }
                                    if(weight.getKey().equals("Application")){
                                        weightApplication = weight.getValue();
                                    }

                                }
                                int assignmentNumber = 0;
                                for (LinkedHashMap.Entry<String, List<Map<String, List<String>>>> assignment : marks.entrySet()) {
                                    if (assignmentNumber == marks.size() - 1) {
                                        assignmentName = assignment.getKey();
                                        for (Map<String, List<String>> cat : assignment.getValue()) {
                                            for (Map.Entry<String, List<String>> category : cat.entrySet()) {
                                                if (category.getKey().equals("thinking") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightThinking;
                                                                usedCategories++;
                                                                totalWeight += weightThinking;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightThinking;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                                if (category.getKey().equals("communication") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightCommunication;
                                                                usedCategories++;
                                                                totalWeight += weightCommunication;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightCommunication;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                                if (category.getKey().equals("application") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightApplication;
                                                                usedCategories++;
                                                                totalWeight += weightApplication;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightApplication;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                                if (category.getKey().equals("knowledge") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightKnowledge;
                                                                usedCategories++;
                                                                totalWeight += weightKnowledge;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightKnowledge;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }

                                            }

                                        }
                                    }
                                    assignmentNumber++;

                                }
                                if(totalWeight == 0.0){
                                    totalWeight = usedCategories;
                                }

                                if (assignmentAverage != 0.0 || gotAZero || totalWeight !=0.0) {
                                    SendNotifications sendNotifications = new SendNotifications(Globalcontext);
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Globalcontext);
                                    Boolean enabledNotifications = sharedPreferences.getBoolean(NOTIFICATION2, true);
                                    if (enabledNotifications) {
                                        Notification notification = sendNotifications.sendOnChannel(CHANNEL_2_ID,
                                                MarksView.class, 1, "New Assignment posted in: " + courseName,
                                                "You Got a " + DoubleRounder.round(assignmentAverage / totalWeight, 1)+ "% in " + assignmentName);
                                        sendNotifications.getManager().notify(2, notification);
                                        System.out.println("SENT NOTIFICATION");

                                    }
                                }

                            }
                            else if (course == 2) {
                                marks = ta.GetMarks(2);
                                LinkedHashMap<String, Double> weights = ta.GetCourseWeights();
                                Double weightKnowledge = 1.0;
                                Double weightCommunication = 1.0;
                                Double weightThinking = 1.0;
                                Double weightApplication = 1.0;
                                String assignmentName = "";
                                double assignmentAverage = 0.0;
                                double totalWeight = 0.0;
                                int usedCategories = 0;
                                Boolean gotAZero = false;
                                for (LinkedHashMap.Entry<String, Double> weight : weights.entrySet()) {
                                    if(weight.getKey().equals("Knowledge")){
                                        weightKnowledge = weight.getValue();
                                    }
                                    if(weight.getKey().equals("Communication")){
                                        weightCommunication = weight.getValue();
                                    }
                                    if(weight.getKey().equals("Thinking")){
                                        weightThinking = weight.getValue();
                                    }
                                    if(weight.getKey().equals("Application")){
                                        weightApplication = weight.getValue();
                                    }

                                }
                                int assignmentNumber = 0;
                                for (LinkedHashMap.Entry<String, List<Map<String, List<String>>>> assignment : marks.entrySet()) {
                                    if (assignmentNumber == marks.size() - 1) {
                                        assignmentName = assignment.getKey();
                                        for (Map<String, List<String>> cat : assignment.getValue()) {
                                            for (Map.Entry<String, List<String>> category : cat.entrySet()) {
                                                if (category.getKey().equals("thinking") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightThinking;
                                                                usedCategories++;
                                                                totalWeight += weightThinking;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightThinking;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                                if (category.getKey().equals("communication") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightCommunication;
                                                                usedCategories++;
                                                                totalWeight += weightCommunication;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightCommunication;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                                if (category.getKey().equals("application") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightApplication;
                                                                usedCategories++;
                                                                totalWeight += weightApplication;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightApplication;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                                if (category.getKey().equals("knowledge") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightKnowledge;
                                                                usedCategories++;
                                                                totalWeight += weightKnowledge;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightKnowledge;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                            }


                                        }
                                    }
                                    assignmentNumber++;

                                }
                                if(totalWeight == 0.0){
                                    totalWeight = usedCategories;
                                }

                                if (assignmentAverage != 0.0 || gotAZero || totalWeight !=0.0) {
                                    SendNotifications sendNotifications = new SendNotifications(Globalcontext);
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Globalcontext);
                                    Boolean enabledNotifications = sharedPreferences.getBoolean(NOTIFICATION3, true);
                                    if (enabledNotifications) {
                                        Notification notification = sendNotifications.sendOnChannel(CHANNEL_3_ID,
                                                MarksView.class, 2, "New Assignment posted in: " + courseName,
                                                "You Got a " + DoubleRounder.round(assignmentAverage / totalWeight, 1)+ "% in " + assignmentName);
                                        sendNotifications.getManager().notify(3, notification);
                                        System.out.println("SENT NOTIFICATION");

                                    }
                                }
                            }
                            else if (course == 3) {
                                marks = ta.GetMarks(3);
                                LinkedHashMap<String, Double> weights = ta.GetCourseWeights();
                                Double weightKnowledge = 1.0;
                                Double weightCommunication = 1.0;
                                Double weightThinking = 1.0;
                                Double weightApplication = 1.0;
                                String assignmentName = "";
                                double assignmentAverage = 0.0;
                                double totalWeight = 0.0;
                                int usedCategories = 0;
                                Boolean gotAZero = false;
                                for (LinkedHashMap.Entry<String, Double> weight : weights.entrySet()) {
                                    if(weight.getKey().equals("Knowledge")){
                                        weightKnowledge = weight.getValue();
                                    }
                                    if(weight.getKey().equals("Communication")){
                                        weightCommunication = weight.getValue();
                                    }
                                    if(weight.getKey().equals("Thinking")){
                                        weightThinking = weight.getValue();
                                    }
                                    if(weight.getKey().equals("Application")){
                                        weightApplication = weight.getValue();
                                    }

                                }
                                int assignmentNumber = 0;
                                for (LinkedHashMap.Entry<String, List<Map<String, List<String>>>> assignment : marks.entrySet()) {
                                    if (assignmentNumber == marks.size() - 1) {
                                        assignmentName = assignment.getKey();
                                        for (Map<String, List<String>> cat : assignment.getValue()) {
                                            for (Map.Entry<String, List<String>> category : cat.entrySet()) {
                                                if (category.getKey().equals("thinking") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightThinking;
                                                                usedCategories++;
                                                                totalWeight += weightThinking;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightThinking;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                                if (category.getKey().equals("communication") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightCommunication;
                                                                usedCategories++;
                                                                totalWeight += weightCommunication;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightCommunication;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                                if (category.getKey().equals("application") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightApplication;
                                                                usedCategories++;
                                                                totalWeight += weightApplication;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightApplication;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }
                                                if (category.getKey().equals("knowledge") && category.getValue().get(0) != null) {
                                                    if (!category.getValue().get(0).isEmpty()) {
                                                        try {
                                                            if(!category.getValue().get(0).split("/")[0].isEmpty()) {
                                                                assignmentAverage += Double.parseDouble(category.getValue().get(0).split("=")[1].split("%")[0]) * weightKnowledge;
                                                                usedCategories++;
                                                                totalWeight += weightKnowledge;
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException e) {
                                                            assignmentAverage += Double.parseDouble("0");
                                                            usedCategories++;
                                                            totalWeight +=weightKnowledge;
                                                            gotAZero = true;
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    assignmentNumber++;
                                }

                                if (assignmentAverage != 0.0 || gotAZero || totalWeight !=0.0) {
                                    SendNotifications sendNotifications = new SendNotifications(Globalcontext);
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Globalcontext);
                                    Boolean enabledNotifications = sharedPreferences.getBoolean(NOTIFICATION4, true);
                                    if (enabledNotifications) {
                                        Notification notification = sendNotifications.sendOnChannel(CHANNEL_4_ID,
                                                MarksView.class, 3, "New Assignment posted in: " + courseName,
                                                "You Got a " + DoubleRounder.round(assignmentAverage / totalWeight, 1) + "% in " + assignmentName);
                                        sendNotifications.getManager().notify(4, notification);
                                        System.out.println("SENT NOTIFICATION");

                                    }
                                }
                        }*/
                            }
                        course++;
                    }

                }


            } catch (ParseException e) {
                e.printStackTrace();
            }




            return newResponse;

        }

        protected void onProgressUpdate(Integer... progress) {
        }


        protected void onPostExecute(LinkedHashMap<String, List<String>> newresponse) {



        }
    }

    private String CalculateAverage(JSONObject marks, String assingmentNumber){
        try {
            JSONObject weights = marks.getJSONObject("categories");
            Double weightK = weights.getDouble("K")*10;
            Double weightT = weights.getDouble("T")*10;
            Double weightC = weights.getDouble("C")*10;
            Double weightA = weights.getDouble("A")*10;
            Double Kmark = 0.0;
            Double Tmark = 0.0;
            Double Cmark = 0.0;
            Double Amark = 0.0;
            Double Omark = 0.0;
            DecimalFormat round = new DecimalFormat(".#");
            JSONObject assignment = marks.getJSONObject(assingmentNumber);

            if(assignment.has("")){
                if (!assignment.getJSONObject("").getString("outOf").equals("0") || !assignment.getJSONObject("").getString("outOf").equals("0.0")) {
                    Omark = Double.parseDouble(assignment.getJSONObject("").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("").getString("outOf"));
                    return round.format(Omark*100);
                }
            }

            if(assignment.has("K")) {
                if (!assignment.getJSONObject("K").getString("outOf").equals("0") || !assignment.getJSONObject("K").getString("outOf").equals("0.0")) {
                    Kmark = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                }
            }else{
                weightK = 0.0;
            }
            if(assignment.has("T")) {
                if (!assignment.getJSONObject("T").getString("outOf").equals("0") || !assignment.getJSONObject("T").getString("outOf").equals("0.0")) {
                    Tmark = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                }
            }else{
                weightT = 0.0;
            }
            if(assignment.has("C")) {
                if (!assignment.getJSONObject("C").getString("outOf").equals("0") || !assignment.getJSONObject("C").getString("outOf").equals("0.0")) {
                    Cmark = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                }
            }else{
                weightC = 0.0;
            }
            if(assignment.has("A")) {
                if (!assignment.getJSONObject("A").getString("outOf").equals("0") || !assignment.getJSONObject("A").getString("outOf").equals("0.0")) {
                    Amark = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                }
            }else{
                weightA = 0.0;
            }

            Kmark*=weightK;
            Tmark*=weightT;
            Cmark*=weightC;
            Amark*=weightA;
            String Average = round.format((Kmark+Tmark+Cmark+Amark)/(weightK+weightT+weightC+weightA)*100);
            if(Average.equals(".0")){
                Average = "0%";
            }
            return Average;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
