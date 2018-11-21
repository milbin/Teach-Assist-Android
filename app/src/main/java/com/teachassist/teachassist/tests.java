package com.teachassist.teachassist;


import org.decimal4j.util.DoubleRounder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class tests {



    public static void main(){
        double Average = 0;
        double[] grades = new double[4];
        int i = 0;

       grades[i] = 1.1;



        for (double value:grades)
            Average += value;
        Average = DoubleRounder.round(Average/grades.length, 1);


    }


}
