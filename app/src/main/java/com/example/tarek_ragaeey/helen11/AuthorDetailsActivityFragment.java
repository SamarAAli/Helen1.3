package com.example.tarek_ragaeey.helen11;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AuthorDetailsActivityFragment extends Fragment {
    private View rootview;
    public  AuthorDetailsActivityFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.author_details_body, container, false);
        String authorDataString = getArguments().getString("JSONObject");
        try {
            JSONObject AuthorJsonObj = new JSONObject(authorDataString);
            JSONArray AuthorInfoArray = AuthorJsonObj.getJSONArray("booksinfo");
            JSONObject AuthorDataObj = AuthorInfoArray.getJSONObject(0);
            parseBookDataFromObj(AuthorDataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootview;
    }

    private void parseBookDataFromObj(JSONObject authorObj) throws JSONException{
        String AUTHOR = authorObj.getString("author_name");
        String ABOUT = authorObj.getString("about");
        String HOMETOWN = authorObj.getString("hometown");
        String WORKCOUNT = authorObj.getString("work_counts");

        TextView authorTitle = (TextView) rootview.findViewById(R.id.author_title);
        authorTitle.setText(AUTHOR);

        TextView about = (TextView) rootview.findViewById(R.id.author_about_obj);
        about.setText(ABOUT);

        TextView homeTown = (TextView) rootview.findViewById(R.id.author_homeTown);
        homeTown.setText("Author's howmtown: "+HOMETOWN);

        TextView workCount = (TextView) rootview.findViewById(R.id.author_workCount);
        workCount.setText("Author's work count: "+WORKCOUNT);
    }
}
