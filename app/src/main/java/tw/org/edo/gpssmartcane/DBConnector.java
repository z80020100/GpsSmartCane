package tw.org.edo.gpssmartcane;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static tw.org.edo.gpssmartcane.Constant.COOKIE_ASP_SESSION_ID_NAME_PREFIX;

/**
 * Created by Jerry on 2018/1/29.
 */

public class DBConnector {
    final private static String TAG = "DBConnector";
    private static String sAspSessionIdValue;
    private static String sAspSessionIdFieldName;

    public static synchronized String executeQuery(ArrayList<NameValuePair> params, String uri, String sessionIdFieldName, String sessionIdValue) {
        //Log.i(TAG, "executeQuery start");
        String result;

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        CookieStore cookieStore = null;
        if(sessionIdFieldName != null && sessionIdValue != null){
            String cookie_session_id = sessionIdFieldName + "=" + sessionIdValue;
            Log.i(TAG, "Set session ID to cookie: " + cookie_session_id);
            httpPost.setHeader("Cookie", cookie_session_id);
        }
        else{
            Log.w(TAG, "No set session ID");
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cookieStore = httpClient.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        //Log.i(TAG, "cookies.size() = " + cookies.size());

        for(int i = 0;i < cookies.size(); i++){
            if(cookies.get(i).getName().contains(COOKIE_ASP_SESSION_ID_NAME_PREFIX)){
                Log.i(TAG, cookies.get(i).getName() + ": " + cookies.get(i).getValue());
                if(sessionIdFieldName == null || sessionIdValue == null){
                    Log.i(TAG, "Store ASP session data");
                    sAspSessionIdFieldName = cookies.get(i).getName();
                    sAspSessionIdValue = cookies.get(i).getValue();
                }
                else{
                    Log.i(TAG, "Ignore ASP session data");
                }
            }
            else{
                Log.i(TAG, "Others -> " + cookies.get(i).getName() + ": " + cookies.get(i).getValue());
            }
        }

        HttpEntity httpEntity = httpResponse.getEntity();
        InputStream inputStream = null;

        try {
            inputStream = httpEntity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
        }
        StringBuilder builder = new StringBuilder();
        String line = null;
        try {
            while((line = bufReader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        result = builder.toString();
        //Log.i(TAG, "executeQuery end");
        Log.e(TAG, "return: "  + result);
        return result;
    }

    public static String executeQuery(ArrayList<NameValuePair> params, String uri) {
        return executeQuery(params, uri, null, null);
    }

    public static String getSessionIdValue(){
        return sAspSessionIdValue;
    }

    public static String getsAspSessionIdFieldName(){
        return sAspSessionIdFieldName;
    }
}
