package io.github.sage.sage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private ListView messagesList;
    private MessageAdapter messageAdapter;
    JSONObject response;
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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("done");
        registerReceiver(requestReceiver, intentFilter);


        //title button that brings you to the info page
        ImageButton sendButton = (ImageButton)findViewById(R.id.sendButton);
        final Intent i = new Intent(this, myIntentService.class);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.editText);
                String input = editText.getText().toString();
                if (!input.equals("")){
                    messageAdapter.addMessage(input, MessageAdapter.DIRECTION_OUTGOING);
                    editText.setText("");
                    i.putExtra("input", input);
                    startService(i);
                    //String output = runnable.getResponse(input);


                    //messageAdapter.addMessage(output, MessageAdapter.DIRECTION_INCOMING);

                }

            }
        });

    }
    private BroadcastReceiver requestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getExtras().getString("response");
            messageAdapter.addMessage(response, MessageAdapter.DIRECTION_INCOMING);

        }
    };

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
