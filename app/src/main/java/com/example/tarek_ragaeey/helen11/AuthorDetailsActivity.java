package com.example.tarek_ragaeey.helen11;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class AuthorDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_details_activity);
        Intent intent = getIntent();
        Bundle extras = new Bundle();
        if (intent != null && intent.hasExtra("JSONObject")) {
            extras = intent.getExtras();
        }
        if (savedInstanceState == null) {
            AuthorDetailsActivityFragment detailsFragment =  new AuthorDetailsActivityFragment();
            detailsFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.author_details_fragment, detailsFragment)
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
