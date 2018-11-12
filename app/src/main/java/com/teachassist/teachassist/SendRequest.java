package com.teachassist.teachassist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;

import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class SendRequest {


//remember to add ? at end of url

/*
    public static void main(String[] args) throws IOException {

        sendGET();
        System.out.println("GET DONE");
        sendPOST();
        System.out.println("POST DONE");
    }
*/
    public void sendGET(String GET_URL, HashMap<String, String> Headers, HashMap<String, String> Parameters) throws IOException {
        try {
            String USER_AGENT = "Mozilla/5.0";

            URL obj = new URL(GET_URL);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            //add headers
            Headers.put("Host", "ta.yrdsb.ca");
            Headers.put("Connection", "keep-alive");
            Headers.put("Upgrade-Insecure-Requests", "1");
            Headers.put("Content-Type", "application/x-www-form-urlencoded");
            Headers.put("Cache-Control", "max-age=0");
            Headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
            Headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            Headers.put("Referer", "https://ta.yrdsb.ca/live/index.php");
            Headers.put("Accept-Encoding", "gzip, deflate, br");
            Headers.put("Accept-Language", "en-US,en;q=0.9,vi-VN;q=0.8,vi;q=0.7");

            for (Map.Entry<String, String> entry : Headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            //add parameters
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, String> entry : Parameters.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
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
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPOST(String POST_URL, HashMap<String, String> Headers, HashMap<String, String> Parameters) throws IOException {

        try {
            URL obj = new URL(POST_URL);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");

            //add headers
            Headers.put("Host", "ta.yrdsb.ca");
            Headers.put("Connection", "keep-alive");
            Headers.put("Upgrade-Insecure-Requests", "1");
            Headers.put("Content-Type", "application/x-www-form-urlencoded");
            Headers.put("Cache-Control", "max-age=0");
            Headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
            Headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            Headers.put("Referer", "https://ta.yrdsb.ca/live/index.php");
            Headers.put("Accept-Encoding", "gzip, deflate, br");
            Headers.put("Accept-Language", "en-US,en;q=0.9,vi-VN;q=0.8,vi;q=0.7");

            for (Map.Entry<String, String> entry : Headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            //add parameters
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, String> entry : Parameters.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
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
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}