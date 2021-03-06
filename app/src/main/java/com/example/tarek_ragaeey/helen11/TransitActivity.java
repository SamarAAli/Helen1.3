package com.example.tarek_ragaeey.helen11;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

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
                search(true,Entity,Type,"search");
                    break;
            case "Recommend":
                search(false,Entity,Type,"recommend");
                break;
            case "ReadBook":
                readBook(Entity);
                break;

            case"AuthorName":
                ShowAuthorDetails(Entity,Type);
                break;


            default:
                Intent wrong=new Intent(this,MainActivity.class);
                startActivity(wrong);
                break;
        }
    }


    private void readBook(String entity) {

        String path = Environment.getExternalStorageDirectory().toString()+"/Download/Helen";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        String filePath="";
        Boolean found=false;
        for (int i = 0; i < files.length; i++)
        {
            String Filename=files[i].getName();
            if(Filename.length()>3)
            {
                if (Filename.substring(Filename.length() - 4).equals(".pdf")) {
                    if (Filename.toLowerCase().contains(entity.toLowerCase())) {
                        filePath = files[i].getAbsolutePath();
                        Intent read = new Intent(TransitActivity.this, PDFViewer.class);
                        read.putExtra("path", filePath);
                        startActivity(read);
                        Log.d("file_path", filePath);
                        found = true;
                        break;

                    } else if (entity.toLowerCase().contains(Filename.toLowerCase())) {
                        filePath = files[i].getAbsolutePath();
                        Intent read = new Intent(TransitActivity.this, PDFViewer.class);
                        read.putExtra("path", filePath);
                        startActivity(read);
                        Log.d("file_path", filePath);
                        found = true;
                        break;

                    }
                }
            }
        }

        if(!found) {
            Intent intent = new Intent(TransitActivity.this, MainActivity.class);
            startActivity(intent);
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
                startActivity(i);
            }
        });
    }
    private void ShowAuthorDetails(String Entity,String Type) {
        Boolean isTitle;
        if(Type.equals("BOOK"))
        {
            isTitle=true;
        }
        else
        {
            isTitle=false;
        }
        if(isOnline(context)) {
            searcher = new BookSearch(this);
            JSONObject bookInfo = null;
            try {
                if (isTitle)
                {
                    bookInfo=searcher.getAuthorInfoByTitle(Entity);

                }
                else if(!isTitle)
                {
                    bookInfo=searcher.getAuthorByName(Entity);

                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error:",e.toString());
            }
            Intent intent = new Intent(this, AuthorDetailsActivity.class).putExtra("JSONObject", bookInfo.toString());
            startActivity(intent);
        }else
            showDialogMsg("Please check your internet connection!");


    }

    public void search(Boolean Search,String Entity,String Type,String Action )
    {
      Boolean isSearch=Search;
        Boolean isTitle;
                if(Type.equals("BOOK"))
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
                Intent intent = new Intent(this, BookListActivity.class).putExtra("JSONObject", bookInfo.toString()).putExtra("Action",Action);
                startActivity(intent);
            }else
                showDialogMsg("Please check your internet connection!");

    }
    public void RateReview(String Entity,String Action )

    {
        String query = Entity;
        if(isOnline(context)) {

            searcher = new BookSearch(this);
            JSONObject bookInfo = null;
            try {
                bookInfo = searcher.getBookByTitle(query);
                bookInfo.put("query_param",query);
            }catch (Exception e) {
                e.printStackTrace();
                Log.e("Error:",e.toString());
            }
            Intent intent = new Intent(this, BookListActivity.class).putExtra("JSONObject", bookInfo.toString()).putExtra("Action",Action);
            startActivity(intent);
        }else
    showDialogMsg("Please check your internet connection!");
    }






}
