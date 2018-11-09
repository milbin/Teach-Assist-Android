package com.teachassist.teachassist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.List;

public class SendRequest {




/*
    public static void main(String[] args) throws IOException {

        sendGET();
        System.out.println("GET DONE");
        sendPOST();
        System.out.println("POST DONE");
    }
*/
    private static void sendGET(String GET_URL, List<String> HeaderKeys, List<String> HeaderValues) throws IOException {
        String USER_AGENT = "Mozilla/5.0";

        //String GET_URL = "http://localhost:9090/SpringMVCExample";


        URL obj = new URL(GET_URL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        for(String Key:HeaderKeys){
            int index = HeaderKeys.indexOf(Key);
            String Value = HeaderValues.get(index);
            con.setRequestProperty(Key, Value);

        }

        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpsURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }

    }

    private static void sendPOST(String POST_URL, List<String> HeaderKeys, List<String> HeaderValues) throws IOException {
        //String POST_URL = "http://localhost:9090/SpringMVCExample/home";
        String USER_AGENT = "Mozilla/5.0";

        URL obj = new URL(POST_URL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        for(String Key:HeaderKeys){
            int index = HeaderKeys.indexOf(Key);
            String Value = HeaderValues.get(index);
            con.setRequestProperty(Key, Value);

        }

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpsURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST request not worked");
        }
    }

}