package com.example.tarek_ragaeey.helen11;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import org.json.JSONObject;


public class BookListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list_activity);
        Intent intent = getIntent();
        Bundle extras = new Bundle();
        if (intent != null && intent.hasExtra("JSONObject") && intent.hasExtra("Action")) {
            extras = intent.getExtras();
        }
        if (savedInstanceState == null) {
            BookListActivityFragment BookListFragment =  new BookListActivityFragment();
            BookListFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_list_fragment, BookListFragment)
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
