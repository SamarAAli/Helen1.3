package com.example.tarek_ragaeey.helen11;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class BookDetailsActivityfragment extends Fragment implements
        TextToSpeech.OnInitListener{
    private BookDetailsAdapter detailsAdapter;
    private String BOOK_TITLE,USER_RATING;
    private String downloadLink,Referer,review;
    private List<JSONObject> Adapterinput;
    private BookDownload downloader;
    private RatingBar user_rating;
    private View headerview;
    private String DESCRIPTION;
    private BookSearch searcher;
    private Button downloadButton;
    String GOODREADS_RATING;
    //////////////////////////////////////////////////////////////////// Tarek
    private TextToSpeech textToSpeech;
    HashMap<String, String> TTSmap = new HashMap<String, String>();
    private String BookTitleRateReview="";
    ////////////////////////////////////////////////////////////////////
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
            JSONObject BooksDataObj = new JSONObject(bookDataString);
            JSONArray  BooksInfoArray = BooksDataObj.getJSONArray("booksinfo");
            JSONObject BookDataObj = BooksInfoArray.getJSONObject(0);
            parseBookDataFromObj(BookDataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.addHeaderView(headerview);
        listView.setAdapter(detailsAdapter);
      ///////////////////////////////////////////////////////////////////////////////////////////// Tarek
        textToSpeech = new TextToSpeech(getActivity(), (TextToSpeech.OnInitListener) this);
        TTSmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

        ImageButton mAsk=(ImageButton) rootview.findViewById(R.id.tarek_edit);
        mAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpectSpeechInput();
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////
        return rootview;
    }



    private void parseBookDataFromObj(JSONObject bookObj) throws JSONException{
        BOOK_TITLE = bookObj.getString("title");
        String AUTHOR = bookObj.getString("author");
        String IMAGEURL = bookObj.getString("image");
        String REL_DATE = bookObj.getString("published_date");
         DESCRIPTION = bookObj.getString("description");
         GOODREADS_RATING = bookObj.getString("goodreads_rating");
        String HELEN_RATING = bookObj.getString("helen_rating");
        USER_RATING = bookObj.getString("user_rating");
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
        Title.setText(BOOK_TITLE+"   by "+AUTHOR);

        /*TextView authors = (TextView) headerview.findViewById(R.id.object_author);
        String authorsName = "by "+AUTHOR;
        authors.setText(authorsName);*/

        ImageView Poster = (ImageView) headerview.findViewById(R.id.object_poster);
        Picasso.with(getContext()).load(IMAGEURL).resize(270,270).onlyScaleDown().into(Poster);

        TextView OverView = (TextView) headerview.findViewById(R.id.object_desc);
        OverView.setText(DESCRIPTION);

        TextView ReleaseDate = (TextView) headerview.findViewById(R.id.object_release_date);
        ReleaseDate.setText("Released in: "+REL_DATE);

        TextView goodreads_rating = (TextView) headerview.findViewById(R.id.goodreads_rating);
        goodreads_rating.setText(GOODREADS_RATING+"/5.0");

        TextView helen_ratings = (TextView) headerview.findViewById(R.id.helen_rating);
        helen_ratings.setText(HELEN_RATING+"/5.0");

        user_rating = (RatingBar) headerview.findViewById(R.id.user_ratings);
        if(USER_RATING != null)
        {
            float u_rate = Float.parseFloat(USER_RATING);
            user_rating.setRating(u_rate);
        }
        user_rating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP ) {
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
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL ) {
                    v.setPressed(false);
                }
                return true;
            }});

        Button addReviewButton = (Button) headerview.findViewById(R.id.add_review_button);
        addReviewButton.setVisibility(View.VISIBLE);
        addReviewButton.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                final EditText txtInput = new EditText(getActivity());
                dialogBuilder.setTitle("New Review ");
                dialogBuilder.setMessage("What's your opinion about "+BOOK_TITLE+"?");
                dialogBuilder.setView(txtInput);
                dialogBuilder.setPositiveButton("Add Review", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        review = txtInput.getText().toString();
                        JSONObject reviewObj;
                        try
                        {
                            reviewObj = interactor.createReview(review,BOOK_TITLE);
                            Adapterinput.add(reviewObj);
                            if(Adapterinput != null)
                            {
                                detailsAdapter.clear();
                                detailsAdapter.addAll(Adapterinput);
                                Toast.makeText(getActivity(), "Your review has been added successfully", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e) {
                            Log.e("Error:", e.toString());
                            Toast.makeText(getActivity(), "Error while adding review", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Adding new review is cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog AddReviewDialog = dialogBuilder.create();
                AddReviewDialog.show();
            }
        });
        downloadButton = (Button) headerview.findViewById(R.id.download_button);
        downloadButton.setVisibility(View.VISIBLE);
        downloadButton.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloader = new BookDownload(getActivity());
                downloader.getDownload(downloadLink,BOOK_TITLE,Referer);
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////Tarek
    public void stopVoice()
    {
        if(textToSpeech.isSpeaking())
        {
            textToSpeech.stop();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if((requestCode == 100) && (data != null) ){

            // Store the data sent back in an ArrayList
            ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            ArrayList<String> Result=new ArrayList<>();
            UnderstandUserTask task=new UnderstandUserTask();
            textToSpeech.speak("Executing command "+spokenText.get(0), TextToSpeech.QUEUE_FLUSH, TTSmap);
            while(textToSpeech.isSpeaking())
            {

            }
            /*    YesorNo();
                if(YesOrNo==false)
                    return;*/

            try {
                Result= task.execute(spokenText.get(0)).get();
                if(Result.get(0).equals("WriteRating"))
                {
                    if(!Result.get(1).equals(""))
                        BookTitleRateReview=Result.get(1);
                        else
                            BookTitleRateReview=BOOK_TITLE;
                    ExpectRate();
                }

                else if(Result.get(0).equals("WriteReview"))
                {
                    if(!Result.get(1).equals(""))
                        BookTitleRateReview=Result.get(1);
                    else
                        BookTitleRateReview=BOOK_TITLE;
                    ExpectReview();
                }

                else if(Result.get(0).equals("Summary"))
                {
                    textToSpeech.speak(DESCRIPTION, TextToSpeech.QUEUE_FLUSH, TTSmap);
                }
                else if(Result.get(0).equals("GetReview")) {
                    if (!Result.get(1).equals(""))

                    {
                        BookTitleRateReview = Result.get(1);
                        searcher = new BookSearch(getActivity());
                        JSONObject bookInfo = null;
                        try {
                            bookInfo = searcher.getComments(BookTitleRateReview);
                            ArrayList<String> reviews = getReviewFromJson(bookInfo.toString());
                            textToSpeech.speak(reviews.get(0), TextToSpeech.QUEUE_FLUSH, TTSmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, TTSmap);
                        while (textToSpeech.isSpeaking()) {

                        }

                    }
                }
                else if(Result.get(0).equals("GetRating"))
                {if(!Result.get(1).equals(""))

                {
                    BookTitleRateReview = Result.get(1);
                    searcher = new BookSearch(getActivity());
                    JSONObject bookInfo = null;
                    try {
                        bookInfo=searcher.getRatings(BookTitleRateReview);
                        String Rating=getRatingFromJson(bookInfo.toString());
                        textToSpeech.speak(BookTitleRateReview+" Rating is "+ Rating, TextToSpeech.QUEUE_FLUSH, TTSmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    textToSpeech.speak(BOOK_TITLE+ " Rating is "+ GOODREADS_RATING, TextToSpeech.QUEUE_FLUSH, TTSmap);
                    while (textToSpeech.isSpeaking()) {

                    }
                }

                }
                else {
                    Intent i = new Intent(getActivity(), TransitActivity.class);
                    i.putExtra("query_class", Result.get(0));

                    i.putExtra("entity", Result.get(1));
                    i.putExtra("type", Result.get(2));
                    startActivity(i);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        else if((requestCode == 110) && (data != null))
        {ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            CreateUserInteractions Interaction=new CreateUserInteractions(getActivity());
            float Rating= (float) 1.1;
            try {
                Rating=Float.parseFloat(spokenText.get(0));
            } catch (NumberFormatException e) {
                textToSpeech.speak("say rate from 1 to 5 only", TextToSpeech.QUEUE_FLUSH, TTSmap);
                while(textToSpeech.isSpeaking())
                {

                }
                ExpectRate();
                return;


            }   try {
                Interaction.createRating(Rating,BookTitleRateReview);
            textToSpeech.speak("Adding your rate is being processed", TextToSpeech.QUEUE_FLUSH,TTSmap);
            while(textToSpeech.isSpeaking())
            {

            }

        } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if((requestCode == 120) && (data != null))
        {
            ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            CreateUserInteractions Interaction=new CreateUserInteractions(getActivity());
            try {
                Interaction.createReview(spokenText.get(0),BookTitleRateReview);
                textToSpeech.speak("Adding your review is being processed", TextToSpeech.QUEUE_FLUSH,TTSmap);
                while(textToSpeech.isSpeaking())
                {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void ExpectRate()
    {

        textToSpeech.speak("Say your rate", TextToSpeech.QUEUE_FLUSH,TTSmap);
        while(textToSpeech.isSpeaking())
        {

        }
        // Starts an Activity that will convert speech to text
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use a language model based on free-form speech recognition
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Recognize speech based on the default speech of device
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // Prompt the user to speak
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_input_Rate));
        try{
            startActivityForResult(intent, 110);
        } catch (ActivityNotFoundException e){
            Toast.makeText(getActivity(),R.string.stt_not_supported_message, Toast.LENGTH_LONG).show();
        }
    }

    public void ExpectSpeechInput() {

            stopVoice();

        // Starts an Activity that will convert speech to text
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use a language model based on free-form speech recognition
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Recognize speech based on the default speech of device
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // Prompt the user to speak
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_input_phrase));
        try{
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e){
            Toast.makeText(getActivity(),R.string.stt_not_supported_message, Toast.LENGTH_LONG).show();
        }
    }
    public void ExpectReview()
    {
        textToSpeech.speak("Say your review", TextToSpeech.QUEUE_FLUSH,TTSmap);
        while(textToSpeech.isSpeaking())
        {

        }


        // Starts an Activity that will convert speech to text
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use a language model based on free-form speech recognition
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Recognize speech based on the default speech of device
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // Prompt the user to speak
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_input_Review));
        try{
            startActivityForResult(intent, 120);
        } catch (ActivityNotFoundException e){
            Toast.makeText(getActivity(),R.string.stt_not_supported_message, Toast.LENGTH_LONG).show();
        }
    }
    private String getRatingFromJson(String ratingString) throws JSONException{

        JSONObject rateObj = new JSONObject(ratingString);
        JSONArray  reviewsArr = rateObj.getJSONArray("booksinfo");
        JSONObject rate = reviewsArr.getJSONObject(0);
        String s=rate.getString("goodreads_rating");

        return s;
    }

    private ArrayList<String> getReviewFromJson(String reviewString) throws JSONException{
        JSONObject reviewsObj = new JSONObject(reviewString);
        JSONArray  reviewsArr = reviewsObj.getJSONArray("booksinfo");
        ArrayList<String> reviews=new ArrayList<>();
        for(int i = 0; i < reviewsArr.length(); i++)
        {
            JSONObject review = reviewsArr.getJSONObject(i);
            reviews.add(review.getString("review"));
        }
        return reviews;
    }


    @Override
    public void onInit(int i) {

    }
    /////////////////////////////////////////////////////////////////////////////////////////////// Tarek
}
