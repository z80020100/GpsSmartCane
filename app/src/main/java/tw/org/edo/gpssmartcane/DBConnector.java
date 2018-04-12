package tw.org.edo.gpssmartcane;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Jerry on 2018/1/29.
 */

public class DBConnector {
    final private static String TAG = "DBConnector";

    public static String executeQuery(ArrayList<NameValuePair> params, String uri) {
        //Log.i(TAG, "executeQuery start");
        String result;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);

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
}
