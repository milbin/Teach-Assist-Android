package com.teachassist.teachassist;

import android.util.JsonReader;
import android.view.View;

import com.google.gson.JsonObject;

import org.decimal4j.util.DoubleRounder;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Calls {
    public static void main(String args[]) {

        TA ta = new TA();
        System.out.println(ta.GetTAData2("335525291", "6rx8836f"));

/*
        String url = "https://ta.yrdsb.ca/live/students/viewReport.php?";
        String path = "/live/students/viewReport.php?";
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
        LinkedHashMap<String, String> cookies = new LinkedHashMap<>();
        parameters.put("subject_id", subjects.get(subject_number));
        parameters.put("student_id", student_id);
        cookies.put("session_token", session_token);
        cookies.put("student_id", student_id);

        Map<String, String> colors = new HashMap<>();
        colors.put("knowledge", "ffffaa");
        colors.put("thinking", "c0fea4");
        colors.put("communication", "afafff");
        colors.put("application", "ffd490");
        colors.put("other", "#dedede");
*/
        //get response


    }
}

        /*
        tests test = new tests();
        try {
            test.test();
        }

        catch(IOException e) {
        e.printStackTrace();
        //String[] returnString = {"ERROR! Check in SendRequest"};

    }*/

