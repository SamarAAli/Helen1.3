package com.example.tarek_ragaeey.helen11;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Tarek-Ragaeey on 7/2/2017.
 */

public class UnderstandUserTask extends AsyncTask <String, Void, ArrayList<String>>{
    ArrayList<String> Result=new ArrayList<>();
    String result = ""; //result of the query
    String queryClass=""; // returned class of the query ex: Readbook , searchbook
    String Entity=""; // returned entity of the query ex: lord of the rings
    String Type=""; // returned entity of the query ex: book or author
    String token="";
    private Activity activityContext;
    @Override
    protected ArrayList<String> doInBackground(String... strings) {

        String basicAuth="JWT "+"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmlnX2lhdCI6MTUwMDM4NTQ1OCwidXNlcm5hbWUiOiJyYW1hZGFuIiwiZXhwIjoxNTAwNDcxODU4LCJlbWFpbCI6InJhbWFkYW5haG1lZHJhbWFkYW45M0B5YWhvby5jb20iLCJ1c2VyX2lkIjoxfQ.aqDSbs5s3IFFlQ50dN3C-Up9zXfRNp55zzipXlnTVGw";
                 URL url;
    /*    String JWTauth="JWT "+token;
    */    HttpURLConnection urlConnection = null;

        try {
            String query=strings[0].replaceAll(" ","%20");
            url = new URL("https://helen-final-gp.herokuapp.com/question/?question="+query);

            urlConnection = (HttpURLConnection) url.openConnection();
      /*      urlConnection.setRequestProperty("Authorization",);
      */
            urlConnection.setRequestProperty ("Authorization", basicAuth);
            InputStream in = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();

            while (data != -1) {

                char current = (char) data;

                result += current;

                data = reader.read();

            }

            JSONArray arr=new JSONArray(result);
            JSONObject root = arr.getJSONObject(0);
            queryClass=root.getString("intent");
            JSONObject EntityAndType=new JSONObject();

             JSONArray Entity_Type = root.getJSONArray("entities");
            if(Entity_Type!=null&&Entity_Type.length()>0)
            {
                EntityAndType = Entity_Type.getJSONObject(0);
                Entity=EntityAndType.getString("entity");
                Type=EntityAndType.getString("type");
            }


            Result.add(queryClass);
            Result.add(Entity);
            Result.add(Type);

            Log.d("query_class",queryClass);
            Log.d("Entity",Entity);
            Log.d("Type",Type);
        }catch (Exception e) {

            e.printStackTrace();

        }


        return Result;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
    }

}
