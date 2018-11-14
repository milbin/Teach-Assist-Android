package com.teachassist.teachassist;

import java.io.IOException;

public class Calls {
    public static void main(String args[]){


        TA ta = new TA();
        String username = "335525291";
        String password = "6rx8836f";
        String response = ta.Get_session_token_and_student_ID(username, password);
        //System.out.print(response);
        /*
        tests test = new tests();
        try {
            test.test();
        }

        catch(IOException e) {
        e.printStackTrace();
        //String[] returnString = {"ERROR! Check in SendRequest"};

    }*/

    }
}
