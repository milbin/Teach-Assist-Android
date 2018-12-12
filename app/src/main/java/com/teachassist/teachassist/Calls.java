package com.teachassist.teachassist;

import android.view.View;

import org.decimal4j.util.DoubleRounder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Calls {
    public static void main(String args[]) {

        String test = "/10=83%";
        String errorCatcher = test.split("/")[0];
        System.out.println(errorCatcher);
        System.out.println(errorCatcher.isEmpty());


        TA ta = new TA();
        String username = "335525168";
        String password = "4a6349kc";
        LinkedHashMap<String, List<String>> response = ta.GetTAData(username, password);
        System.out.println(response);

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

