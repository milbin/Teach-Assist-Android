package com.teachassist.teachassist;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

public class AlertReceiver extends BroadcastReceiver {

    LinkedHashMap<String, List<String>> response;
    String username;
    String password;
    Context Globalcontext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Globalcontext = context;
        //get old response
        SharedPreferences prefs = context.getSharedPreferences("RESPONSE", MODE_PRIVATE);
        String str = prefs.getString("RESPONSE", "");
        Gson gson = new Gson();
        Type entityType = new TypeToken<LinkedHashMap<String, List<String>>>() {
        }.getType();
        response = gson.fromJson(str, entityType);
        System.out.println("NOTIFICATION" + response);

        //get username and password
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME, "");
        password = sharedPreferences.getString(PASSWORD, "");
        new GetTaData().execute(username, password);


    }

    private class GetTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params) {
            TA ta = new TA();
            String Username = params[0];
            String Password = params[1];

            //LinkedHashMap<String, List<String>> newResponse = ta.GetTAData(Username, Password);
            ArrayList list1 = new ArrayList<>(Arrays.asList("66.7", "AVI3M1-01", "Visual Arts", "169"));
            ArrayList list2 = new ArrayList<>(Arrays.asList("93.1", "SPH3U1-01", "Physics", "167"));
            ArrayList list3 = new ArrayList<>(Arrays.asList("82.0", "FIF3U1-01", "", "214"));
            ArrayList list4 = new ArrayList<>(Arrays.asList("87.1", "MCR3U1-01", "Functions and Relations", "142"));
            LinkedHashMap<String, List<String>> newResponse = new LinkedHashMap<>();
            newResponse.put("283098", list1);
            newResponse.put("283003", list2);
            newResponse.put("283001", list3);
            newResponse.put("283152", list4);



            return newResponse;

        }

        protected void onProgressUpdate(Integer... progress) {
        }


        protected void onPostExecute(LinkedHashMap<String, List<String>> newresponse) {
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
                System.out.println(currentTime.after(calendarStart.getTime()));
                System.out.println(currentTime.before(calendarEnd.getTime()));
                System.out.println(calendarStart.getTime());
                System.out.println(calendarEnd.getTime());
                if (currentTime.after(calendarStart.getTime()) && currentTime.before(calendarEnd.getTime())) {


                    LinkedList toSend = new LinkedList<String>();
                    for (LinkedHashMap.Entry<String, List<String>> entry : response.entrySet()) {
                        if (entry.getKey().contains("NA")) {
                            toSend.add(entry.getKey());
                        } else {
                            toSend.add(entry.getValue().get(0));
                        }

                    }
                    int course = 0;
                    for (LinkedHashMap.Entry<String, List<String>> entry : newresponse.entrySet()) {
                        if (entry.getKey().contains("NA")) {
                            //toSend.remove(entry.getKey());
                        } else {
                            if (!entry.getValue().get(0).equals(toSend.get(course)) || toSend.get(course).toString().contains("NA")) { // idk why u gotta add toString here
                                SendNotifications sendNotifications = new SendNotifications(Globalcontext);
                                String courseName = entry.getValue().get(1);
                                if(course == 0) {
                                    Notification notification = sendNotifications.sendOnChannel(CHANNEL_1_ID,
                                            1, "Your Marks for: " + courseName + " Have Been Updated",
                                            "You Average is Now: " + toSend.get(course) + " Click here for more information");
                                    sendNotifications.getManager().notify(1, notification);
                                    System.out.println("SENT NOTIFICATION");
                                }
                                else if(course == 1){
                                    Notification notification = sendNotifications.sendOnChannel(CHANNEL_2_ID,
                                            1, "Your Marks for: " + courseName + " Have Been Updated",
                                            "You Average is Now: " + toSend.get(course) + " Click here for more information");
                                    sendNotifications.getManager().notify(1, notification);
                                    System.out.println("SENT NOTIFICATION");
                                }
                                else if(course == 2){
                                    Notification notification = sendNotifications.sendOnChannel(CHANNEL_3_ID,
                                            1, "Your Marks for: " + courseName + " Have Been Updated",
                                            "You Average is Now: " + toSend.get(course) + " Click here for more information");
                                    sendNotifications.getManager().notify(1, notification);
                                    System.out.println("SENT NOTIFICATION");
                                }
                                else if(course == 3){
                                    Notification notification = sendNotifications.sendOnChannel(CHANNEL_4_ID,
                                            1, "Your Marks for: " + courseName + " Have Been Updated",
                                            "You Average is Now: " + toSend.get(course) + " Click here for more information");
                                    sendNotifications.getManager().notify(1, notification);
                                    System.out.println("SENT NOTIFICATION");
                                }
                            }
                        }
                        course++;
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
    }
}
