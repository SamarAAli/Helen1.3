package com.example.tarek_ragaeey.helen11;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONObject;

public class TransitActivity extends AppCompatActivity {
    private AlertDialog alertDialog;
    private BookSearch searcher;
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transit);
        Intent i = this.getIntent();
        String queryClass=i.getStringExtra("query_class");
        String Entity=i.getStringExtra("entity");
        String Type=i.getStringExtra("type");

        Intent targetClass;
        switch (queryClass) {
            case "Search":
                search(true,Entity,Type);

            case "Recommend":
                search(false,Entity,Type);

            case "ReadBook":
                break;

            case "Summary":
                break;

            case "WriteReview ":
                break;

            case "GetReview ":
                break;

            case "WriteRating ":
                break;

            case "GetRating":
                break;

            case"AuthorName":
                break;


            default:
                break;
        }
    }
    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }
    private void showDialogMsg(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                alertDialog.dismiss();
                //getActivity().finish();

                Intent i=new Intent(context,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });
    }
    public void search(Boolean Search,String Entity,String Type )
    {
      Boolean isSearch=Search;
        Boolean isTitle;
                if(Type=="BOOK")
                    isTitle=true;
                else
                    isTitle=false;




            String query = Entity;
            if(isOnline(context)) {
                searcher = new BookSearch(this);
                JSONObject bookInfo = null;
                try {
                    if (isSearch)
                    {
                        if(isTitle)
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
                        if(isTitle)
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
                Intent intent = new Intent(this, BookListActivity.class).putExtra("JSONObject", bookInfo.toString());
                startActivity(intent);
            }else
                showDialogMsg("Please check your internet connection!");

    }


}
