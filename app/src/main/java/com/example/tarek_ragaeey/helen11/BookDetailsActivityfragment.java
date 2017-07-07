package com.example.tarek_ragaeey.helen11;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

 public class BookDetailsActivityfragment extends Fragment {
    private BookDetailsAdapter detailsAdapter;
    private String BOOK_TITLE;
    private String downloadLink,Referer;
    private List<JSONObject> Adapterinput;
    private BookDownload downloader;
    private RatingBar user_rating;
    private View headerview;
    private CreateUserInteractions interactor;
    public  BookDetailsActivityfragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.book_details_body, container, false);
        headerview = inflater.inflate(R.layout.book_details_header,null);
        detailsAdapter = new BookDetailsAdapter(getActivity(),new ArrayList<JSONObject>());
        ListView listView = (ListView) rootview.findViewById(R.id.Book_Content_List_view);
        String bookDataString = getArguments().getString("JSONObject");
        try {
            JSONObject BookDataObj = new JSONObject(bookDataString);
            parseBookDataFromObj(BookDataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.addHeaderView(headerview);
        listView.setAdapter(detailsAdapter);
        return rootview;
    }
    private void parseBookDataFromObj(JSONObject bookObj) throws JSONException{
        BOOK_TITLE = bookObj.getString("title");
        String AUTHOR = bookObj.getString("author");
        String IMAGEURL = bookObj.getString("image");
        String REL_DATE = bookObj.getString("published_date");
        String DESCRIPTION = bookObj.getString("description");
        String GOODREADS_RATING = bookObj.getString("goodreads_rating");
        String HELEN_RATING = bookObj.getString("user_ratings");
        Referer = bookObj.getString("referer");
        downloadLink = bookObj.getString("download_link");
        JSONArray REVIEWS = bookObj.getJSONArray("comments");
        interactor = new CreateUserInteractions(getActivity());

        Adapterinput = new ArrayList<>();
        for(int i = 0; i < REVIEWS.length(); i++)
        {
            JSONObject bookReview = REVIEWS.getJSONObject(i);
            Adapterinput.add(bookReview);
        }
        if(Adapterinput != null)
        {
            detailsAdapter.clear();
            detailsAdapter.addAll(Adapterinput);
        }

        TextView Title = (TextView) headerview.findViewById(R.id.object_title);
        Title.setText(BOOK_TITLE);

        TextView authors = (TextView) headerview.findViewById(R.id.object_author);
        String authorsName = "by "+AUTHOR;
        authors.setText(authorsName);

        ImageView Poster = (ImageView) headerview.findViewById(R.id.object_poster);
        Picasso.with(getContext()).load(IMAGEURL).resize(270,270).onlyScaleDown().into(Poster);

        TextView OverView = (TextView) headerview.findViewById(R.id.object_desc);
        OverView.setText(DESCRIPTION);

        TextView ReleaseDate = (TextView) headerview.findViewById(R.id.object_release_date);
        ReleaseDate.setText("Released in: "+REL_DATE);

        RatingBar goodreads_rating = (RatingBar) headerview.findViewById(R.id.goodreads_rating);
        float rate = Float.parseFloat(GOODREADS_RATING);
        goodreads_rating.setRating(rate);

        RatingBar helen_ratings = (RatingBar) headerview.findViewById(R.id.helen_ratings);
        float u_rate = Float.parseFloat(HELEN_RATING);
        helen_ratings.setRating(u_rate);

        user_rating = (RatingBar) headerview.findViewById(R.id.user_ratings);
        user_rating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = user_rating.getWidth();
                    float starsf = (touchPositionX / width) * 5.0f;
                    try
                    {
                        interactor.createRating(starsf,BOOK_TITLE);
                    }catch (Exception e)
                    {
                        Log.e("Error:",e.toString());
                    }
                    int stars = (int)starsf + 1;
                    user_rating.setRating(stars);
                    v.setPressed(false);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }
                return true;
            }});

        Button downloadButton = (Button) headerview.findViewById(R.id.download_button);
        downloadButton.setVisibility(View.VISIBLE);
        downloadButton.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloader = new BookDownload(getActivity());
                downloader.getDownload(downloadLink,BOOK_TITLE,Referer);
            }
        });
    }
}
