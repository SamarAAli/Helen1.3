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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;


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
        String link = activityContext.getResources().getString(R.string.create_review);
        JSONObject reviewData = new CreateInteractionTask().execute(link,book_title,"review",Review).get();
        return reviewData;
    }
    public void createRating (float Rating,String book_title) throws Exception
    {
        String link = activityContext.getResources().getString(R.string.create_rating);
        new CreateInteractionTask().execute(link,book_title,"rate",Float.toString(Rating));
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
            String basicAuth="JWT " + activityContext.getResources().getString(R.string.token);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            httppost.addHeader("Authorization", basicAuth);
            try {
                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("book_title",params[1]));
                nameValuePairs.add(new BasicNameValuePair(params[2],params[3]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);
                String reviewJsonstring = EntityUtils.toString(response.getEntity());
                try{
                    return getReviewDataFromJson(reviewJsonstring);
                } catch (JSONException e)
                {
                    Log.e("Error:",e.getMessage(),e);
                    e.printStackTrace();
                }
            } catch (ClientProtocolException e) {
                Log.e("Error:",e.getMessage(),e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Error:",e.getMessage(),e);
                e.printStackTrace();
            }
            return null;
        }
    }
}
