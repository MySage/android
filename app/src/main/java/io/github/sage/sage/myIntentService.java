package io.github.sage.sage;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by dannyeng on 15-11-01.
 */
public class myIntentService extends IntentService {
    public myIntentService() {
        super("myIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        requestRunnable run = new requestRunnable();
        String response = run.getResponse(intent.getExtras().getString("input"));
        Intent i = new Intent("done");
        i.putExtra("response", response);
        System.out.println(response);
        myIntentService.this.sendBroadcast(i);

    }
}
