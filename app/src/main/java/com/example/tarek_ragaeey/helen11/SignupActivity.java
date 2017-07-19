package com.example.tarek_ragaeey.helen11;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.Bind;

public class SignupActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {
    private static final String TAG = "SignupActivity";


    EditText etName, etPassword, etConfirmPassword;
    String name, password, confirmPassword;
    Boolean username=false;
    Boolean     userpassword=false;
    Boolean userconfirmpassword=false;
    Button _signupButton;
    String token="";
    private TextToSpeech textToSpeech;
    HashMap<String, String> TTSmap = new HashMap<String, String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = (EditText) findViewById(R.id.input_name);
        etPassword = (EditText) findViewById(R.id.input_password);
        etConfirmPassword = (EditText) findViewById(R.id.input_ConfirmPassword);
        _signupButton = (Button) findViewById(R.id.btn_signup);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();

            }
        });
        ImageButton mAsk=(ImageButton) findViewById(R.id.ask_helen_search);
        mAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpectSpeechInput();
            }
        });
        textToSpeech = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);
        TTSmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        name = etName.getText().toString();
        password = etPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

        String task = "register";
        BackgroundTask backgroundTask = new BackgroundTask(SignupActivity.this);
        try {
            token= backgroundTask.execute(task,name, password, confirmPassword).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (token.equals("error"))
        {
            Toast.makeText(this,"Invalid Username or Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            SharedPreferences sharedPreferences = getSharedPreferences("com.example.tarek_ragaeey.helen11", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.clear();
            edit.putString("token", token);
            edit.commit();
            // Start the Signup activity
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
        }


    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        //finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        name = etName.getText().toString();
        password = etPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            etName.setError("at least 3 characters");
            valid = false;
        } else {
            etName.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || confirmPassword.length() > 10 || !(confirmPassword.equals(password))) {
            etConfirmPassword.setError("Password Do not match");
            valid = false;
        } else {
            etConfirmPassword.setError(null);
        }

        return valid;
    }
    public void ExpectSpeechInput() {
            if(etName.getText().toString().isEmpty())
            {
                username=true;
                userconfirmpassword=false;
                userpassword=false;
                textToSpeech.speak("Enter you Name", TextToSpeech.QUEUE_FLUSH,TTSmap);
                while(textToSpeech.isSpeaking())
                {

                }
            }
            else if(etPassword.getText().toString().isEmpty())
            {
                username=false;
                userconfirmpassword=false;
                userpassword=true;
                textToSpeech.speak("Confirm your password", TextToSpeech.QUEUE_FLUSH,TTSmap);
                while(textToSpeech.isSpeaking())
                {

                }
            }
            else if(etConfirmPassword.getText().toString().isEmpty())
            {
                username=false;
                userconfirmpassword=true;
                userpassword=false;
                textToSpeech.speak("Enter you password", TextToSpeech.QUEUE_FLUSH,TTSmap);
                while(textToSpeech.isSpeaking())
                {

                }
            }
            else
            {
                name = etName.getText().toString();
                password = etPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();

                String task = "register";
                BackgroundTask backgroundTask = new BackgroundTask(SignupActivity.this);
                try {
                    token= backgroundTask.execute(task,name, password, confirmPassword).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (token.equals("error"))
                {
                    Toast.makeText(this,"Invalid Username or Password",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SharedPreferences sharedPreferences = getSharedPreferences("com.example.tarek_ragaeey.helen11", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.clear();
                    edit.putString("token", token);
                    edit.commit();
                    // Start the Signup activity
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                }



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
                getString(R.string.speech_input_phrase));
        try{
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e){
            Toast.makeText(this,R.string.stt_not_supported_message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if((requestCode == 100) && (data != null) ) {

            // Store the data sent back in an ArrayList
            ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(username)
            {
             etName.setText(spokenText.get(0));
            }
            else if(userpassword)
            {
                etPassword.setText(spokenText.get(0));
            }
            else if(userconfirmpassword)
            {
                etConfirmPassword.setText(spokenText.get(0));
            }

        } super.onActivityResult(requestCode, resultCode, data);
        }


            @Override
    public void onInit(int i) {

    }
}