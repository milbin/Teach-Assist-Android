package com.teachassist.teachassist;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TA {


    public void Login(String Username, String Password) {
        // https://ta.yrdsb.ca/yrdsb/?subject_id=0&username=335525168&password=4a6349kc&submit=Login
        String url = "https://ta.yrdsb.ca/yrdsb/?";
        HashMap<String, String> headers = new HashMap<>();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("username", Username);
        parameters.put("password", Password);
        parameters.put("subject_id", "0");
        SendRequest sr = new SendRequest();
        try {
            sr.sendPOST(url, headers, parameters);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }
}
