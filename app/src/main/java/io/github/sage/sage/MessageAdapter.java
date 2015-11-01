package io.github.sage.sage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

/**
 * Created by dannyeng on 15-10-31.
 */
public class MessageAdapter extends BaseAdapter{

    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;
    public static final int IMAGE_URL = 2;
    private List<Pair<String, Integer>> messages;
    private LayoutInflater layoutInflater;

    public MessageAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<Pair<String, Integer>>();
    }

    public void addMessage(String message, int direction) {
        messages.add(new Pair(message, direction));
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int i) {
        return messages.get(i).second;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);
        //show message on left or right, depending on if
        //it's incoming or outgoing
        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_INCOMING) {
                res = R.layout.message_right;
            } else if (direction == DIRECTION_OUTGOING) {
                res = R.layout.message_left;
            }
            else if (direction == IMAGE_URL){
                res = R.layout.image_layout;
            }
            convertView = layoutInflater.inflate(res, viewGroup, false);
        }
        if (messages.get(i).second == IMAGE_URL){


            final View finalConvertView = convertView;
            new AsyncTask<Void, Void, Void>() {
                Bitmap bmp;
                @Override
                protected Void doInBackground(Void... params) {
                    String message = messages.get(i).first;

                    try {
                        InputStream in = new URL(message).openStream();
                        System.out.println(message);
                        bmp = BitmapFactory.decodeStream(in);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (bmp != null){
                        float aspectRatio = bmp.getWidth() /
                                (float) bmp.getHeight();
                        int width = 900;
                        int height = Math.round(width / aspectRatio);

                        bmp = Bitmap.createScaledBitmap(
                                bmp, width, height, false);
                        ImageButton imageMessage = (ImageButton) finalConvertView.findViewById(R.id.imageMessage);
                        imageMessage.setImageBitmap(bmp);
                    }

                }

            }.execute();
        }
        else{
            TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
            txtMessage.setText(messages.get(i).first);
        }

        return convertView;
    }
}

