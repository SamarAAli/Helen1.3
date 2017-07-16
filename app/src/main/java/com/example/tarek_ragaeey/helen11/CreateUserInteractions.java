package com.example.tarek_ragaeey.helen11;


import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class CreateUserInteractions {
    private Activity activityContext;
    private AlertDialog alertDialog;
    CreateUserInteractions(Activity context)
    {
        super();
        activityContext = context;
    }
    public JSONObject createReview (String Review,String book_title) throws Exception
    {
        StringBuilder link = new StringBuilder(activityContext.getResources().getString(R.string.create_review)+book_title);
        link.append("&review=");
        link.append(URLEncoder.encode(Review, "UTF-8"));
        return new CreateInteractionTask().execute(link.toString()).get();
    }
    public void createRating (float Rating,String book_title) throws Exception
    {
        StringBuilder link = new StringBuilder(activityContext.getResources().getString(R.string.create_rating)+book_title);
        link.append("&rate=");
        link.append(URLEncoder.encode(Float.toString(Rating), "UTF-8"));
        new CreateInteractionTask().execute(link.toString());
    }

    private void showDialogMsg(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activityContext);
        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private class CreateInteractionTask extends AsyncTask<String, Void, JSONObject>
    {
        private JSONObject getReviewDataFromJson(String reviewJsonStr) throws JSONException {
            JSONObject ReviewJsonObj = new JSONObject(reviewJsonStr);
            return  ReviewJsonObj;
        }
        @Override
        protected JSONObject doInBackground(String... params)
        {
            String basicAuth="JWT eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmlnX2lhdCI6MTUwMDE4ODM1NCwidXNlcm5hbWUiOiJyYW1hZGFuIiwiZXhwIjoxNTAwMjc0NzU0LCJlbWFpbCI6InJhbWFkYW5haG1lZHJhbWFkYW45M0B5YWhvby5jb20iLCJ1c2VyX2lkIjoxfQ.aNgCoK8ZBnHL29NPFCPIHd3eHxP79Mq375709XmcvzY";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewJsonstring = null;
            try {
                Uri builtUri = Uri.parse(params[0]);
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestProperty ("Authorization", basicAuth);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewJsonstring = buffer.toString();
            }catch (IOException e) {
                Log.e("Error:", "Could not connect ", e);
                showDialogMsg("Please check your internet connection!");
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Error:", "Error closing stream", e);
                    }
                }
            }
            try{
                return getReviewDataFromJson(reviewJsonstring);
            } catch (JSONException e)
            {
                Log.e("Error:",e.getMessage(),e);
                e.printStackTrace();
            }
            return null;
        }
    }
}
