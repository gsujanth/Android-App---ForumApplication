package com.example.princ.inclass10;

/*Assignment# - InClass10
  Names : Sujanth Babu Guntupalli
          Mounika Yendluri
*/

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    private final String TAG = "demoLogin";

    EditText emailET, pwdET;
    Button loginButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Chat Room");

        emailET = findViewById(R.id.emailET);
        pwdET = findViewById(R.id.pwdET);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);


        Log.d(TAG, "LoginActivityOnCreate: " + getToken());

        if (!getToken().isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, ThreadsActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = pwdET.getText().toString();
                if(email.isEmpty()){
                    emailET.setError("Email field empty");
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //Toast.makeText(LoginActivity.this, "Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                    emailET.setError("Invalid Email pattern");
                }else if(password.isEmpty()){
                    pwdET.setError("Password field empty");
                }else {
                    performLogin(email, password);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void performLogin(String email, String password) {

        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/login")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "performLoginOnFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "performLoginOnResponse: " + String.valueOf(Thread.currentThread().getId()));
                String str = response.body().string();
                Log.d(TAG, "performLoginOnResponse: " + str);
                Gson gson = new Gson();
                LoginResponse loginResponse = gson.fromJson(str, LoginResponse.class);
                Log.d(TAG, "performLoginOnResponse: " + loginResponse.token);
                saveToken(loginResponse.token, loginResponse.user_fname, loginResponse.user_lname, loginResponse.user_id);
                if (loginResponse.token != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: " + getToken());
                            Intent intent = new Intent(LoginActivity.this, ThreadsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Login not successful. Enter Valid Email/Password", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    public void saveToken(String token, String user_fname, String user_lname, String user_id) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREF_FILE), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putString("user_fname", user_fname);
        editor.putString("user_lname", user_lname);
        editor.putString("user_id", user_id);
        editor.apply();
    }

    public String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREF_FILE), Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }
}
