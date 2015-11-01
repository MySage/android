package io.github.sage.sage;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {

    JSONObject query;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            url = new URL("http://mysage.xyz:8000/user/login/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //title button that brings you to the info page
        Button start = (Button)findViewById(R.id.button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameText = (EditText)findViewById(R.id.nameText);
                EditText passwordText = (EditText)findViewById(R.id.passwordText);
                String user = nameText.getText().toString();
                String pass = passwordText.getText().toString();
                passwordText.setText("");
                if (pass.equals("")){
                    pass = "wordpass";
                }

                query = new JSONObject();
                try {
                    query.put("username", user);
                    query.put("password", pass);
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
                        TextView errorText = (TextView)findViewById(R.id.errorText);
                        errorText.setVisibility(View.INVISIBLE);
                        Intent i2 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i2);
                    }else{
                        TextView errorText = (TextView)findViewById(R.id.errorText);
                        errorText.setVisibility(View.VISIBLE);

                        //System.out.println(connection.getResponseMessage());
                        //System.out.println(connection.getErrorStream());
                    }


                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
