package io.github.sage.sage;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import android.widget.EditText;
import android.widget.ListView;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    int i = 0;
    private ListView messagesList;
    private MessageAdapter messageAdapter;
    JSONObject query;
    private URL url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        url = null;
        try {
            url = new URL("http://mysage.xyz:8000/consumer/send_message/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        //title button that brings you to the info page
        ImageButton sendButton = (ImageButton)findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.editText);
                String input = editText.getText().toString();
                if (!input.equals("")){
                    messageAdapter.addMessage(input, MessageAdapter.DIRECTION_OUTGOING);
                    query = new JSONObject();
                    try {
                        query.put("message", input);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {

                        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.setDoOutput(true);
                        connection.setDoInput(true);

                        OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream());
                        wr.write(query.toString());
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
                            messageAdapter.addMessage(sb.toString(), MessageAdapter.DIRECTION_INCOMING);

                        }else{

                            System.out.println(connection.getResponseMessage());
                            System.out.println(connection.getErrorStream());
                            messageAdapter.addMessage("Error", MessageAdapter.DIRECTION_INCOMING);
                        }


                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    editText.setText("");

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
