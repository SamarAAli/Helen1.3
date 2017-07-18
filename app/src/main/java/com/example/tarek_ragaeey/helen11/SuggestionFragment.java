package com.example.tarek_ragaeey.helen11;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SuggestionFragment extends Fragment implements
        TextToSpeech.OnInitListener   {

    private BookListAdapter BooksAdapter;
    private List<JSONObject> BookList ;
    private JSONObject BookFullData;
    private AlertDialog alertDialog;
    private String destination="";
    private BookSearch searcher;
    private ListView listView;
    private TextToSpeech textToSpeech;
    private View rootView;
    HashMap<String, String> TTSmap = new HashMap<String, String>();

    public SuggestionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_suggest, container, false);
        listView = (ListView) rootView.findViewById(R.id.Suggest_List_view);
        updateListView();
      /*  BooksAdapter = new BookListAdapter(getActivity(), new ArrayList<JSONObject>());
        BookList = new ArrayList<JSONObject>();
        BooksAdapter.addAll(BookList);*/
        textToSpeech = new TextToSpeech(getActivity(), (TextToSpeech.OnInitListener) this);
        TTSmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

      /*  if (BooksAdapter != null)
            listView.setAdapter(BooksAdapter);*/
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //updateListView();
    }

    @Override
    public void onStop() {
        super.onStop();
        textToSpeech.speak(destination, TextToSpeech.QUEUE_FLUSH, TTSmap);
    }
    @Override
    public void onStart()
    {
        super.onStart();
       // updateListView();
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
    public void updateListView() {
        Context context = getActivity();
        BooksAdapter = new BookListAdapter(getActivity(), new ArrayList<JSONObject>());
        if (isOnline(context)) {
            searcher = new BookSearch(getActivity());
            JSONObject booksInfo = null;
            try {
                booksInfo = searcher.getBookSuggestions();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (booksInfo != null) {
                try {
                    BookList = getBooksDataFromJson(booksInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                BookList = new ArrayList<JSONObject>();
            }
            // if (BookList != null) {
            BooksAdapter.clear();
            BooksAdapter.addAll(BookList);
            if (BookList.size() > 0) {
                listView.setAdapter(BooksAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        searcher = new BookSearch(getActivity());
                        try {
                            JSONObject bookObj = BooksAdapter.getItem(position);
                            String BOOK_TITLE = getBookTitle(bookObj);
                            BookFullData = searcher.getFullBookInfo(BOOK_TITLE);
                            destination = "You are viewing a page that contains book information";
                            Intent intent = new Intent(getActivity(), BookDetailsActivity.class).putExtra("JSONObject", BookFullData.toString());
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e("Error:", e.toString());
                        }
                    }
                });
            }
        /*}else
        {
            BookList =  new ArrayList<>();
            BooksAdapter.addAll(BookList);
            listView.setAdapter(BooksAdapter);
        }*/
        }
    }
    private String getBookTitle(JSONObject book) throws JSONException
    {
        String result = book.getString("title");
        return result;
    }
    private List<JSONObject> getBooksDataFromJson(JSONObject BooksJsonObj)throws JSONException {
        final String _INFO = "booksinfo";
        JSONArray booksArray = BooksJsonObj.getJSONArray(_INFO);
        List<JSONObject> resultStrs = new ArrayList<JSONObject>() ;
        for(int i = 0; i < booksArray.length(); i++)
        {
            JSONObject bookObj = booksArray.getJSONObject(i);
            resultStrs.add(bookObj);
        }
        return resultStrs;
    }
    @Override
    public void onInit(int i) {

    }
}
