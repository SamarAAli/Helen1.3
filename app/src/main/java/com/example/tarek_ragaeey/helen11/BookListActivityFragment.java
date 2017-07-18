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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class BookListActivityFragment extends Fragment implements
        TextToSpeech.OnInitListener   {
    ////////////////////////////////////////////
    String BookTitle="";
    private TextToSpeech textToSpeech;
    HashMap<String, String> TTSmap = new HashMap<String, String>();

    //////////////////////////////////////////
    private BookListAdapter BooksAdapter;
    private FragmentListener flistener;
    private BookSearch searcher;
    private List<JSONObject> BookList ;
    private JSONObject BookFullData;
    private AlertDialog alertDialog;
    public BookListActivityFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_item_list, container, false);
        BooksAdapter = new BookListAdapter(getActivity(),new ArrayList<JSONObject>());
        ListView listView = (ListView) rootView.findViewById(R.id.Book_List_view);
        listView.setAdapter(BooksAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BookSearch searcher = new BookSearch(getActivity());
                try {
                    JSONObject bookObj = BooksAdapter.getItem(position);
                    String BOOK_TITLE = getBookTitle(bookObj);
                    BookFullData = searcher.getFullBookInfo(BOOK_TITLE);
                    flistener.setDetailsData(BookFullData);
                }catch (Exception e)
                {
                    Log.e("Error:",e.toString());
                }
            }
        });
        //////////////////////////////////////////////////////////////////////Tarek



        ImageButton mAsk=(ImageButton) rootView.findViewById(R.id.ask_helen_bookList);
        mAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpectSpeechInput();
            }
        });
        textToSpeech = new TextToSpeech(getActivity(), (TextToSpeech.OnInitListener) this);
        TTSmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
//////////////////////////////////////////////////////////////////////////
        return rootView;
    }
    public void setFragmentListner(FragmentListener fragmentListener) {
        flistener = fragmentListener;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        updateListView();
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
                //getActivity().finish();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void updateListView(){
        Context context = getActivity();
        if(isOnline(context)) {
            String BooksDataString = getArguments().getString("JSONObject");
            try {
                BookList = getBooksDataFromJson(BooksDataString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (BookList != null) {
                BooksAdapter.clear();
                BooksAdapter.addAll(BookList);
            }
        }else
            showDialogMsg("Please check your internet connection!");
    }

    private String getBookTitle(JSONObject book) throws JSONException
    {
        String result = book.getString("title");
        return result;
    }
    private List<JSONObject> getBooksDataFromJson(String BooksDataString)throws JSONException {
        final String _INFO = "booksinfo";
        JSONObject booksJson = new JSONObject(BooksDataString);
        JSONArray booksArray = booksJson.getJSONArray(_INFO);
        List<JSONObject> resultStrs = new ArrayList<JSONObject>() ;
        for(int i = 0; i < booksArray.length(); i++)
        {
            JSONObject bookObj = booksArray.getJSONObject(i);
            resultStrs.add(bookObj);
        }
        return resultStrs;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////Tarek
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
                            textToSpeech.speak(reviews.get(0), TextToSpeech.QUEUE_FLUSH, TTSmap);

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
                getString(R.string.speech_input_Review));
        try{
            startActivityForResult(intent, 120);
        } catch (ActivityNotFoundException e){
            Toast.makeText(getActivity(),R.string.stt_not_supported_message, Toast.LENGTH_LONG).show();
        }
    }
    public void ExpectSpeechInput() {

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
