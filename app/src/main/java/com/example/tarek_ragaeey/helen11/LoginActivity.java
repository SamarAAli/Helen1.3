package com.example.tarek_ragaeey.helen11;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    //  TextView name,email;
//    SharedPreferences preferences;
    private static final int REQUEST_SIGNUP = 0;
    private static final String TAG = "LoginActivity";
    String stringUsername,stringPassword;
    EditText etUsername,etPassword;
    Button _loginButton;
    TextView _signupLink;
    String Token = "";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.input_name);
        etPassword = (EditText) findViewById(R.id.input_password);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        _loginButton = (Button) findViewById(R.id.btn_login);

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login();

            }

        });

    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        stringUsername = etUsername.getText().toString();
        stringPassword = etPassword.getText().toString();
        String task = "login";
        BackgroundTask backgroundTask = new BackgroundTask(LoginActivity.this);

        etUsername.setText("");
        etPassword.setText("");

        //execute the task
        //passes the paras to the backgroundTask (param[0],param[1],param[2])
        try {
            Token = backgroundTask.execute(task, stringUsername, stringPassword).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (Token.equals("error"))
        {
            Toast.makeText(this,"Invalid Username or Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            SharedPreferences sharedPreferences = getSharedPreferences("com.example.tarek_ragaeey.helen11", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.clear();
            edit.putString("token", Token);
            edit.commit();
            // Start the Signup activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);

        //finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        stringUsername = etUsername.getText().toString();
        stringPassword = etPassword.getText().toString();

        if (stringUsername.isEmpty() || stringUsername.length() < 3) {
            etUsername.setError("at least 3 characters");
            valid = false;
        } else {
            etUsername.setError(null);
        }

        if (stringPassword.isEmpty() || stringPassword.length() < 4 || stringPassword.length() > 10) {
            etPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if((requestCode == 100) && (data != null) ) {

            // Store the data sent back in an ArrayList
            ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


        } super.onActivityResult(requestCode, resultCode, data);
    }

}