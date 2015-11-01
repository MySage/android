package io.github.sage.sage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

    private ListView messagesList;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);

        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

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
                    String locationProvider = LocationManager.NETWORK_PROVIDER;

                    Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                    editText.setText("");
                    i.putExtra("input", input);
                    i.putExtra("latitude", lastKnownLocation.getLatitude());
                    i.putExtra("longitude", lastKnownLocation.getLongitude());
                    startService(i);
                }

            }
        });

    }
    private BroadcastReceiver requestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getExtras().getString("response");
            System.out.println(response);
            if (response.substring(0, 4).equals("http")){
                messageAdapter.addMessage(response, MessageAdapter.IMAGE_URL);

            } else{
                messageAdapter.addMessage(response, MessageAdapter.DIRECTION_INCOMING);
            }


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
