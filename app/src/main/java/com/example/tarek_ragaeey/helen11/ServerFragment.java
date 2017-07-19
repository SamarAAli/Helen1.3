package com.example.tarek_ragaeey.helen11;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ServerFragment extends Fragment implements
        TextToSpeech.OnInitListener{
    private AlertDialog alertDialog;
    private BookSearch searcher;
    private boolean isAuthor = false,isTitle = true,isAuthorName = false,isSearch = true,isCancelled = false;
    private View customView;
    private String BookTitle="",destination="";
    private TextToSpeech textToSpeech;
    HashMap<String, String> TTSmap = new HashMap<String, String>();

    public ServerFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_server, container, false);
        ImageView mSearch=(ImageView) root.findViewById(R.id.search_view);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                customView = inflater.inflate(R.layout.custom_dialog_box, null);
                dialogBuilder.setView(customView);
                dialogBuilder.setCancelable(false);
                final AlertDialog ChoicesDialog = dialogBuilder.create();
                ChoicesDialog.show();
                Button confirmButton = (Button) customView.findViewById(R.id.confirm_button);
                Button cancelButton = (Button) customView.findViewById(R.id.cancel_button);
                final Switch searchFor = (Switch) customView.findViewById(R.id.search_for_switch);
                final Switch searchType = (Switch) customView.findViewById(R.id.search_type_switch);
                final Switch searchBy = (Switch) customView.findViewById(R.id.search_by_switch);
                searchFor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

                        if(isChecked){
                            isAuthor = true;
                            searchFor.setText("Searching for Author");
                        }else{
                            isAuthor = false;
                            searchFor.setText("Searching for Book");
                        }

                    }
                });
                searchType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

                        if(isChecked){
                            isSearch = false;
                            searchType.setText("Find Similar Books");
                        }else{
                            isSearch = true;
                            searchType.setText("Find Book");
                        }

                    }
                });
                searchBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

                        if(isChecked){
                            isAuthorName = true;
                            isTitle = false;
                            searchBy.setText("Author Name");
                        }else{
                            isAuthorName = false;
                            isTitle = true;
                            searchBy.setText("Book Title");
                        }

                    }
                });
                confirmButton.setOnClickListener(new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isCancelled = false;
                        ChoicesDialog.dismiss();
                    }});
                cancelButton.setOnClickListener(new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isCancelled = true;
                        ChoicesDialog.dismiss();
                    }});
                ChoicesDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface arg0) {
                        if(!isCancelled)
                            search();
                    }
                });
            }
        });
        ImageButton mAsk=(ImageButton) root.findViewById(R.id.ask_helen_search);
        mAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpectSpeechInput();
            }
        });
        textToSpeech = new TextToSpeech(getActivity(), (TextToSpeech.OnInitListener) this);
        TTSmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        textToSpeech.speak(destination, TextToSpeech.QUEUE_FLUSH, TTSmap);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }
    private void showDialogMsg(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
    public void search()
    {
        EditText search_Query=(EditText) getActivity().findViewById(R.id.input_text);
        if(search_Query != null)
        {
            Context context = getActivity();
            String query = search_Query.getText().toString();
            if(isOnline(context)) {
                searcher = new BookSearch(getActivity());
                JSONObject bookInfo = null;
                JSONObject authorInfo = null;
                try {
                    if(isAuthor)
                    {
                        if(isAuthorName)
                        {
                            authorInfo = searcher.getAuthorByName(query);
                        }
                        else
                        {
                            authorInfo = searcher.getAuthorInfoByTitle(query);
                        }
                    }
                    else
                    {
                        if (isSearch)
                        {
                            if(isTitle)
                            {
                                bookInfo = searcher.getBookByTitle(query);
                            }
                            else
                            {
                                bookInfo = searcher.getBookByAuthor(query);
                            }
                        }
                        else
                        {
                            if(isTitle)
                            {
                                bookInfo = searcher.getSimilarByTitle(query);
                            }
                            else
                            {
                                bookInfo = searcher.getSimilarByAuthor(query);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error:",e.toString());
                }
                if(isAuthor)
                {
                    isAuthor = false;
                    isTitle = true;
                    isAuthorName = false;
                    isSearch = true;
                    if(authorInfo == null)
                        Toast.makeText(getActivity(),"Something went wrong, please check that the text you have entered is valid", Toast.LENGTH_LONG).show();
                    else
                    {
                        destination = "You are viewing a page that contains author information";
                        Intent intent = new Intent(getActivity(), AuthorDetailsActivity.class).putExtra("JSONObject", authorInfo.toString());
                        startActivity(intent);
                    }
                }
                else
                {
                    isAuthor = false;
                    isTitle = true;
                    isAuthorName = false;
                    isSearch = true;
                    if(bookInfo == null)
                        Toast.makeText(getActivity(),"Something went wrong, please check that the text you have entered is valid", Toast.LENGTH_LONG).show();
                    else
                    {
                        destination = "You are viewing a list of searched books";
                        Intent intent = new Intent(getActivity(), BookListActivity.class).putExtra("JSONObject", bookInfo.toString()).putExtra("Action","search");
                        startActivity(intent);
                    }
                }
            }else
                showDialogMsg("Please check your internet connection!");
        }
        else
            showDialogMsg("Please enter a book title or an author name!");
    }



///////////////////////////////////////////////////////////////////////

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
                if(!isOnline(getActivity()))
                {
                    textToSpeech.speak("Check you Internet connection", TextToSpeech.QUEUE_FLUSH, TTSmap);
                    while(textToSpeech.isSpeaking())
                    {

                    }
                    return;
                }
                Result= task.execute(spokenText.get(0)).get();
                if(Result.get(0).equals("")||Result.get(1).equals("")||Result.get(2).equals(""))
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
    public void stopVoice()
    {
        if(textToSpeech.isSpeaking())
        {
            textToSpeech.stop();
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
      /*public float  getRatingFromJson(String json)
        {

        }*/

    @Override
    public void onInit(int i) {


    }

}
