package com.teachassist.teachassist;


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




        OkHttpClient client = new OkHttpClient().newBuilder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        final ArrayList<Cookie> oneCookie = new ArrayList<>(1);
                        oneCookie.add(createNonPersistentCookie1());
                        oneCookie.add(createNonPersistentCookie2());
                        return oneCookie;
                    }
                })
                .build();

        public static Cookie createNonPersistentCookie1(){
            return new Cookie.Builder()
                    .domain("ta.yrdsb.ca")
                    .path("/live/index.php?")
                    .name("session_token")
                    .value("79LI2XT9LwZS2") // if it dosnt work change this to current one
                    .httpOnly()
                    .secure()
                    .build();}
        public static Cookie createNonPersistentCookie2(){
            return new Cookie.Builder()
                    .domain("ta.yrdsb.ca")
                    .path("/live/index.php?")
                    .name("student_id")
                    .value("127709")
                    .httpOnly()
                    .secure()
                    .build();
        }

    void test() throws IOException {
    String url = "https://ta.yrdsb.ca/live/index.php?";

    RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "335525291")
                .addFormDataPart("password", "6rx8836f")
                .addFormDataPart("subject_id", "0")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        Response response = client.newCall(request).execute();
        System.out.println( response.body().string());
        System.out.println( response.headers().toString());
        //System.out.println(response.);

    }
}
