package io.github.sage.sage;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dannyeng on 15-11-01.
 */
public class requestRunnable implements Runnable {

    String responseString;
    private URL url;
    JSONObject query;
    JSONObject response;



    public String getResponse (String input, double latitude, double longitude) {
        url = null;
        try {
            url = new URL("http://mysage.xyz:8000/consumer/send_message/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        query = new JSONObject();
        try {
            query.put("message", input);
            query.put("latitude", latitude);
            query.put("longitude", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        run();
        return responseString;

    }

    @Override
    public void run() {
        //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream());
            wr.write(query.toString());
            System.out.println(query.toString());
            wr.flush();
            StringBuilder sb = new StringBuilder();
            int HttpResult = connection.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                try {
                    response = new JSONObject(sb.toString());
                    responseString = response.get("message").toString();
                    if (responseString.equals("")){
                        responseString = "I didn't quite catch that. Say that again?";
                    }
                } catch (JSONException e) {
                    responseString = sb.toString();
                }
            }else{

                System.out.println(connection.getResponseMessage());
                System.out.println(connection.getErrorStream());
                responseString = "I didn't quite catch that. Say that again?";
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            responseString = "I didn't quite catch that. Say that again?";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            responseString = "I didn't quite catch that. Say that again?";
        }
    }
}
