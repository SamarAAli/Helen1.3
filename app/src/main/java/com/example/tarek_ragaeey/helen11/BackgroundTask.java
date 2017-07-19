package com.example.tarek_ragaeey.helen11;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Mitch on 2016-06-04.
 */
public class BackgroundTask extends AsyncTask<String,Void,String> {

    //SharedPreferences preferences;
    //SharedPreferences.Editor editor;
    private Activity activityContext;
    //private AlertDialog alertDialog;
    Context context;


    BackgroundTask(Activity ctx){
        activityContext= ctx;
    }



    @Override
    protected String doInBackground(String... params) {
        String task = params[0];
        String reviewJsonstring="";
        String Test = "";
        HttpClient httpclient = new DefaultHttpClient();




        if(task.equals("register")){
            HttpPost httppost = new HttpPost(activityContext.getResources().getString(R.string.signUp));
            String regName = params[1];
            String regPassword = params[2];
            String regPassword2 = params[3];
            try {
                //add data

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("username",params[1]));
                nameValuePairs.add(new BasicNameValuePair("password",params[2]));
                nameValuePairs.add(new BasicNameValuePair("password2",params[3]));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);

                reviewJsonstring = EntityUtils.toString(response.getEntity());
                JSONObject responseObj = new JSONObject(reviewJsonstring);
                Test=responseObj.getString("token");
            } catch (ClientProtocolException e) {
                Log.e("Error:",e.getMessage(),e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Error:",e.getMessage(),e);
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Test;


        }
        if(task.equals("login")){
            HttpPost httppost = new HttpPost(activityContext.getResources().getString(R.string.login));
            String regName = params[1];
            String regPassword = params[2];
            try {
                //add data

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("username",params[1]));
                nameValuePairs.add(new BasicNameValuePair("password",params[2]));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);
                if (response.getStatusLine().getStatusCode()== HttpsURLConnection.HTTP_OK){
                    reviewJsonstring = EntityUtils.toString(response.getEntity());
                    JSONObject responseObj = new JSONObject(reviewJsonstring);
                    Test=responseObj.getString("token");
                }
               else
                {
                    Test= "error";
                }

            } catch (ClientProtocolException e) {
                Log.e("Error:",e.getMessage(),e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Error:",e.getMessage(),e);
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Test;

        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //This method willbe called when doInBackground completes... and it will return the completion string which
    //will display this toast.
    @Override
    protected void onPostExecute(String s) {

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public void display(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
