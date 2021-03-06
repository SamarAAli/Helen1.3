package com.example.tarek_ragaeey.helen11;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener   {
    private TextToSpeech textToSpeech;
    HashMap<String, String> TTSmap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        askPermissions();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
          //  actionBar.setDisplayShowHomeEnabled(false);
        }
        /*setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BooksFragment())
                    .commit();
        }*/
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        textToSpeech = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);
        TTSmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(position==0)
                {
                    textToSpeech.speak("Showing books in a scroll view and you can use the button down the page to issue a command ", TextToSpeech.QUEUE_FLUSH, TTSmap);
                }
                else if(position==1)
                {
                    textToSpeech.speak("Search Tab", TextToSpeech.QUEUE_FLUSH, TTSmap);
                }
            }
        });

    }

    @TargetApi(23)
    private void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        ActivityCompat.requestPermissions(MainActivity.this,permissions, requestCode);
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.get_pdf:
                    showFileChooser();

                default:
                return super.onOptionsItemSelected(item);
      }
    }*/
    ////////////////////////////////////////////////////
        private static final int FILE_SELECT_CODE = 0;
        private void showFileChooser() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, "application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==FILE_SELECT_CODE) {

            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                Uri uri = data.getData();

                File myFile = new File(uri.getPath());
                String FilePath= myFile.getAbsolutePath();
                String Filename=FilePath.substring(FilePath.lastIndexOf("/")+1);
                // Get the path

                Intent i = new Intent(this, PDFViewer.class);
                i.putExtra("uri", uri.toString());
                i.putExtra("path",FilePath );
                i.putExtra("name",Filename );
                i.putExtra("page","");
                startActivity(i);
            }
        }
                else if((requestCode == 100) && (data != null) ){

                    // Store the data sent back in an ArrayList
                    ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    EditText wordsEntered = (EditText) findViewById(R.id.input_text);

                    // Put the spoken text in the EditText
                    wordsEntered.setText(spokenText.get(0));

                }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void ExceptSpeechInput(View view) {

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

            Toast.makeText(this,R.string.stt_not_supported_message, Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onInit(int i) {

    }


    //////////////////////////////////
}
