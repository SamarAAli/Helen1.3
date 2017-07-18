package com.example.tarek_ragaeey.helen11;

import android.content.Intent;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Locale;

public class Splash extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private boolean timerdone = false;
    HashMap<String, String> TTSmap = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textToSpeech = new TextToSpeech(Splash.this,new  TextToSpeech.OnInitListener (){
            @Override
        public void onInit(int status) {
            // TODO Auto-generated method stub
            if(status == TextToSpeech.SUCCESS){
                int result=textToSpeech.setLanguage(Locale.US);
                if(result==TextToSpeech.LANG_MISSING_DATA ||
                        result==TextToSpeech.LANG_NOT_SUPPORTED){
                    Log.e("error", "This Language is not supported");
                }
            }
            else
                Log.e("error", "Initilization Failed!");
        }
    });
        setContentView(R.layout.splash);
        new CountDownTimer(3000,1000){
            @Override
            public void onTick(long millisUntilFinished){}

            @Override
            public void onFinish(){
                //set the new Content of your activity
                textToSpeech.speak("welcome to homepage", TextToSpeech.QUEUE_FLUSH, TTSmap);
                timerdone = true;
                switchactivity();
            }
        }.start();
}

    private void switchactivity()
    {
        Intent i = new Intent(Splash.this, MainActivity.class);
        startActivity(i);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        if(textToSpeech != null){

            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }
    @Override
    public void onStart() {
        super.onStart();
        textToSpeech.speak("welcome to homepage", TextToSpeech.QUEUE_FLUSH, TTSmap);
    }
    @Override
    public void onStop() {
        super.onStop();
        textToSpeech.speak("welcome to homepage", TextToSpeech.QUEUE_FLUSH, TTSmap);
    }
}
