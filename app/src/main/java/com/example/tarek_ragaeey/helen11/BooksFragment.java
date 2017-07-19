package com.example.tarek_ragaeey.helen11;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;


public class BooksFragment extends Fragment  implements
        TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;
    private String BookTitle="";
    private BookSearch searcher;
    HashMap<String, String> TTSmap = new HashMap<String, String>();
    public BooksFragment() {
        // Required empty public constructor
    }

    Set<String> BookList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Books");
    }

    private ArrayList<String> Books=new ArrayList<>();
    private ArrayList<String> BooksNames=new ArrayList<>();
    private ArrayAdapter<String> mBooksAdapter;
    private ArrayList<String> Bookpath=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_books, container, false);
        ImageButton askHelen=(ImageButton) rootView.findViewById(R.id.ask_helen_home);
        askHelen.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpectSpeechInput();
               //login();
            }
        });
        textToSpeech = new TextToSpeech(getActivity(), (TextToSpeech.OnInitListener) this);
        TTSmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
       /////////////////////////////////////////////////////////////////////////////////

        String path = Environment.getExternalStorageDirectory().toString()+"/Download/Helen";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);

        for (int i = 0; i < files.length; i++)
        {
            String Filename=files[i].getName();
            if(Filename.length()>3)
            {
                if (Filename.substring(Filename.length() - 4).equals(".pdf")) {
                    BooksNames.add(Filename);
                    Bookpath.add(files[i].getAbsolutePath());
                }
            }
        }
        ///////////////////////////////////////////////////////////////////////////



        /*SharedPreferences sharedPreferences=getActivity().getSharedPreferences("com.example.tarek_ragaeey.helen11", Context.MODE_PRIVATE);

        BookList=sharedPreferences.getStringSet("books",null);
        if(BookList!=null)
        {
                for (Iterator<String> it = BookList.iterator(); it.hasNext(); ) {
                    String uri = it.next();
                    Log.e("Book on create",uri);
                    Books.add(uri);
                    Uri myUri = Uri.parse(uri);
                    File myFile = new File(myUri.getPath());
                   String FilePath = myFile.getAbsolutePath();
                    String FileName =FilePath.substring(FilePath.lastIndexOf("/")+1);
                    FileName.replaceAll(".pdf","");
                    BooksNames.add(FileName);
                }
        }
        else
        {
            BookList=new HashSet<String>();
        }*/

        mBooksAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_books_2, // The name of the layout ID.
                        R.id.list_item_books_textview_2, // The ID of the textview to populate.
                        BooksNames);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_books);
        listView.setAdapter(mBooksAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


               /* String uri=Books.get(i);
                Uri myUri = Uri.parse(uri);
                File myFile = new File(myUri.getPath());
                String FilePath = myFile.getAbsolutePath();*/
                Intent intent = new Intent(getActivity(), PDFViewer.class);
                intent.putExtra("path", Bookpath.get(i));
                startActivity(intent);
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                textToSpeech.speak(BooksNames.get(i), TextToSpeech.QUEUE_FLUSH, TTSmap);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

    }
    public void stopVoice()
    {
        if(textToSpeech.isSpeaking())
        {
            textToSpeech.stop();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "Tarek";

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, "application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getActivity(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
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
                                while(textToSpeech.isSpeaking())
                                {

                                }
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

    private String getRatingFromJson(String ratingString) throws JSONException {

        JSONObject rateObj = new JSONObject(ratingString);
        JSONArray reviewsArr = rateObj.getJSONArray("booksinfo");
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
    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    @Override
    public void onInit(int i) {

    }
}
