package com.teachassist.teachassist;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Calls {
    public static void main(String args[]) {


        TA ta = new TA();
        String username = "335525291";
        String password = "6rx8836f";
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

