package com.example.tarek_ragaeey.helen11;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ServerFragment extends Fragment  {
    private FragmentListener flisttener;
    private AlertDialog alertDialog;
    private BookSearch searcher;
    public ServerFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_server, container, false);
        ImageView mSearch=(ImageView) root.findViewById(R.id.search_view);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //  ExceptSpeechInput();
            search();
            }
        });
        Button mAsk=(Button)root.findViewById(R.id.ask_helen_search);
        mAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpectSpeechInput();
            }
        });

        return root;
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
    public void search()
    {
        EditText search_Query=(EditText) getActivity().findViewById(R.id.input_text);
        RadioButton isSearch = (RadioButton) getActivity().findViewById(R.id.search_radio_btn);
        RadioButton isTitle = (RadioButton) getActivity().findViewById(R.id.title_radio_btn);
        if(search_Query != null)
        {
            Context context = getActivity();
            String query = search_Query.getText().toString();
            if(isOnline(context)) {
                searcher = new BookSearch(getActivity());
                JSONObject bookInfo = null;
                try {
                    if (isSearch.isChecked())
                    {
                        if(isTitle.isChecked())
                        {
                            bookInfo = searcher.getBookByTitle(query);
                            bookInfo.put("query_param",query);
                        }
                        else
                        {
                            bookInfo = searcher.getBookByAuthor(query);
                            bookInfo.put("query_param",query);
                        }
                    }
                    else
                    {
                        if(isTitle.isChecked())
                        {
                            bookInfo = searcher.getSimilarByTitle(query);
                            bookInfo.put("query_param",query);
                        }
                        else
                        {
                            bookInfo = searcher.getSimilarByAuthor(query);
                            bookInfo.put("query_param",query);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error:",e.toString());
                }
                Intent intent = new Intent(getActivity(), BookListActivity.class).putExtra("JSONObject", bookInfo.toString());
                startActivity(intent);
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
             try {
                 Result= task.execute(spokenText.get(0)).get();
                 Intent i=new Intent(getActivity(),TransitActivity.class);
                 i.putExtra("query_class",Result.get(0));
                 i.putExtra("entity",Result.get(1));
                 i.putExtra("type",Result.get(2));
                 startActivity(i);


             } catch (InterruptedException e) {
                 e.printStackTrace();
             } catch (ExecutionException e) {
                 e.printStackTrace();
             }

        }
        super.onActivityResult(requestCode, resultCode, data);
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

    public class ServerTask extends AsyncTask<String, Void, String> {
        String result = ""; //result of the query
        String queryClass=""; // returned class of the query ex: Readbook , searchbook
        String Entity=""; // returned entity of the query ex: lord of the rings
        String Type=""; // returned entity of the query ex: book or author
        URL url;

        @Override
        protected String doInBackground(String... urls) {


            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
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
                queryClass=root.getString("query_class");
                JSONArray Entity_Type=root.getJSONArray("entities");
                JSONObject EntityAndType=Entity_Type.getJSONObject(0);

                Entity=EntityAndType.getString("entity");
                Type=EntityAndType.getString("type");


                /*JSONObject root = new JSONObject(result);
                JSONArray sportsArray = root.getJSONArray("sport");
// now get the first element:
                JSONObject firstSport = sportsArray.getJSONObject(0);
// and so on
                String name = firstSport.getString("name"); // basketball
                int id = firstSport.getInt("id"); // 40
                JSONArray leaguesArray = firstSport.getJSONArray("leagues");
                */
            }catch (Exception e) {

                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
