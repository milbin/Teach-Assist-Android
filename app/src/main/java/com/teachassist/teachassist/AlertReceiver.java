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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.teachassist.teachassist.App.CHANNEL_1_ID;
import static com.teachassist.teachassist.LaunchActivity.CREDENTIALS;
import static com.teachassist.teachassist.LaunchActivity.PASSWORD;
import static com.teachassist.teachassist.LaunchActivity.USERNAME;

public class AlertReceiver extends BroadcastReceiver {

    LinkedHashMap<String, List<String>> response;
    ArrayList toSend = new ArrayList<String>();
    String username;
    String password;
    Context Globalcontext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Globalcontext = context;
        //get old response
        SharedPreferences prefs = context.getSharedPreferences("RESPONSE",MODE_PRIVATE);
        String str = prefs.getString("RESPONSE", "");
        Gson gson = new Gson();
        Type entityType = new TypeToken<LinkedHashMap<String, List<String>>>(){}.getType();
        response = gson.fromJson(str, entityType);
        System.out.println("NOTIFICATION"+response);

        //get username and password
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME, "");
        password = sharedPreferences.getString(PASSWORD, "");
        new GetTaData().execute(username, password);





    }

    private class GetTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>> {


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params){
            TA ta = new TA();
            String Username = params[0];
            String Password = params[1];

            response = ta.GetTAData(Username, Password);
            return response;

        }

        protected void onProgressUpdate(Integer... progress) {}


        protected void onPostExecute(LinkedHashMap<String, List<String>> newresponse) {
            int course = 0;
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(entry.getKey().contains("NA")){
                    toSend.add(entry.getKey());
                }
                else{
                    toSend.add(entry.getValue().get(0));
                }
                course++;
            }
            course = 0;
            for (Map.Entry<String, List<String>> entry : newresponse.entrySet()) {
                if(entry.getKey().contains("NA")){
                    toSend.remove(entry.getKey());
                }
                else{
                    if(!entry.getValue().equals(toSend.get(course)) || toSend.get(course).toString().contains("NA")) { // idk why u gotta add toString here
                        toSend.add(entry.getValue().get(0));
                    }

                }
                course++;
            }
            System.out.println(toSend);
            SendNotifications sendNotifications = new SendNotifications(Globalcontext);
            Notification notification = sendNotifications.sendOnChannel(CHANNEL_1_ID, 1, "title", "body");
            sendNotifications.getManager().notify(1 , notification);
        }



    }
}
