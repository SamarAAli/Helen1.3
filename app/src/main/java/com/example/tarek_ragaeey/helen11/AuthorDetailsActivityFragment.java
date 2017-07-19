package com.example.tarek_ragaeey.helen11;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class AuthorDetailsActivityFragment extends Fragment  implements
        TextToSpeech.OnInitListener{
    private View rootview;
    public  AuthorDetailsActivityFragment() {}
    private String BookTitle="";
    private String ABOUT="";
    private TextToSpeech textToSpeech;
    HashMap<String, String> TTSmap = new HashMap<String, String>();
    private BookSearch searcher;
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
        ImageButton mAsk=(ImageButton) rootview.findViewById(R.id.ask_helen_author);
        mAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpectSpeechInput();
            }
        });
        textToSpeech = new TextToSpeech(getActivity(), (TextToSpeech.OnInitListener) this);
        TTSmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

        return rootview;
    }

    private void parseBookDataFromObj(JSONObject authorObj) throws JSONException{
        String AUTHOR = authorObj.getString("author_name");
         ABOUT = authorObj.getString("about");
        String HOMETOWN = authorObj.getString("hometown");
        String WORKCOUNT = authorObj.getString("work_counts");

        TextView authorTitle = (TextView) rootview.findViewById(R.id.author_title);
        authorTitle.setText(AUTHOR);

        TextView about = (TextView) rootview.findViewById(R.id.author_about_obj);
        about.setMovementMethod(new ScrollingMovementMethod());
        about.setText(ABOUT);

        TextView homeTown = (TextView) rootview.findViewById(R.id.author_homeTown);
        homeTown.setText("Author's howmtown: "+HOMETOWN);

        TextView workCount = (TextView) rootview.findViewById(R.id.author_workCount);
        workCount.setText("Author's work count: "+WORKCOUNT);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
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
                 if(Result.get(0).equals("Summary"))
                {
                    textToSpeech.speak(ABOUT, TextToSpeech.QUEUE_FLUSH, TTSmap);
                }

                else if(Result.get(0).equals("")||Result.get(1).equals("")||Result.get(2).equals(""))
                {
                    textToSpeech.speak("I don't understand enter your command again", TextToSpeech.QUEUE_FLUSH, TTSmap);
                    while(textToSpeech.isSpeaking())
                    {

                    }
                    return;
                }

                if(Result.get(0).equals("WriteRating"))
                {
                    if(!Result.get(1).equals(""))

                    {
                        BookTitle = Result.get(1);
                        ExpectRate();
                    }
                    else
                    {
                        textToSpeech.speak("I don't understand enter your command again", TextToSpeech.QUEUE_FLUSH, TTSmap);
                        while(textToSpeech.isSpeaking())
                        {

                        }

                    }
                }
                else if(Result.get(0).equals("WriteReview"))
                {
                    if(!Result.get(1).equals(""))

                    {
                        BookTitle = Result.get(1);
                        ExpectReview();
                    }
                    else
                    {
                        textToSpeech.speak("I don't understand enter your command again", TextToSpeech.QUEUE_FLUSH, TTSmap);
                        while(textToSpeech.isSpeaking())
                        {

                        }

                    }
                }
                else if(Result.get(0).equals("GetReview")) {
                    if (!Result.get(1).equals(""))

                    {
                        BookTitle = Result.get(1);
                        searcher = new BookSearch(getActivity());
                        JSONObject bookInfo = null;
                        try {
                            bookInfo = searcher.getComments(BookTitle);
                            ArrayList<String> reviews = getReviewFromJson(bookInfo.toString());
                            for(int i=0;i<reviews.size();i++) {
                                textToSpeech.speak(reviews.get(i), TextToSpeech.QUEUE_FLUSH, TTSmap);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        textToSpeech.speak("I don't understand enter your command again", TextToSpeech.QUEUE_FLUSH, TTSmap);
                        while (textToSpeech.isSpeaking()) {

                        }

                    }
                }
                else if(Result.get(0).equals("GetRating"))
                {if(!Result.get(1).equals(""))

                {
                    BookTitle = Result.get(1);
                    searcher = new BookSearch(getActivity());
                    JSONObject bookInfo = null;
                    try {
                        bookInfo=searcher.getRatings(BookTitle);
                        String Rating=getRatingFromJson(bookInfo.toString());
                        textToSpeech.speak(BookTitle+" Rating is "+ Rating, TextToSpeech.QUEUE_FLUSH, TTSmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    textToSpeech.speak("I don't understand enter your command again", TextToSpeech.QUEUE_FLUSH, TTSmap);
                    while(textToSpeech.isSpeaking())
                    {

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
        {
            ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            CreateUserInteractions Interaction=new CreateUserInteractions(getActivity());
            float Rating= (float) 1.1;
            try {
                Rating=Float.parseFloat(spokenText.get(0));
                if(!((Rating>=1)&&(Rating<=5)))
                {
                    textToSpeech.speak("say rate from 1 to 5 only", TextToSpeech.QUEUE_FLUSH, TTSmap);
                    while(textToSpeech.isSpeaking())
                    {

                    }
                    ExpectRate();
                    return;
                }
            } catch (NumberFormatException e) {
                textToSpeech.speak("say rate from 1 to 5 only", TextToSpeech.QUEUE_FLUSH, TTSmap);
                while(textToSpeech.isSpeaking())
                {

                }
                ExpectRate();
                return;


            }


            try {
                Interaction.createRating(Rating,BookTitle);
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
                Interaction.createReview(spokenText.get(0),BookTitle);
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

        }        // Starts an Activity that will convert speech to text
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

    @Override
    public void onInit(int i) {

    }
}
