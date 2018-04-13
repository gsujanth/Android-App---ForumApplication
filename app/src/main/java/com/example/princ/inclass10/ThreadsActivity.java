package com.example.princ.inclass10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThreadsActivity extends AppCompatActivity implements ThreadsAdapter.DataUpdateAfterDelete {

    OkHttpClient client = new OkHttpClient();
    private final String TAG="demoThreads";
    ArrayList<ThreadsListResponse.MessageThread> threadsList;
    ThreadsAdapter threadsAdapter;

    TextView userNameTV,threadListTV;
    EditText newThreadET;
    ImageButton logOutButton,addThreadButton;
    ListView threadItemsLV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threads);
        setTitle("Message Threads");

        userNameTV=findViewById(R.id.userNameTV);
        threadListTV=findViewById(R.id.threadListTV);
        newThreadET=findViewById(R.id.newThreadET);
        logOutButton=findViewById(R.id.logOutButton);
        addThreadButton=findViewById(R.id.addThreadButton);
        threadItemsLV=findViewById(R.id.threadItemsLV);

        userNameTV.setTextColor(Color.parseColor("#000000"));
        threadListTV.setTextColor(Color.parseColor("#000000"));

       // threadItemsLV.setDivider(new ColorDrawable(Color.parseColor("#0000FF")));
       // threadItemsLV.setDividerHeight(2);

        Log.d(TAG, "ThreadsActivityOnCreate: "+getToken());
        getThreadList(getToken());

        userNameTV.setText(getUserName());

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREF_FILE), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.clear().apply();
                Intent intent = new Intent(ThreadsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=newThreadET.getText().toString();
                if(!title.isEmpty()) {
                    addNewThread(getToken(), title);
                }else{
                    Toast.makeText(ThreadsActivity.this, "Enter thread title", Toast.LENGTH_SHORT).show();
                }
            }
        });

        threadItemsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ThreadsActivity.this, "list view item "+position+" clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ThreadsActivity.this, ChatActivity.class);
                intent.putExtra("messageThreadDetails",threadsList.get(position));
                startActivity(intent);
                finish();
            }
        });
    }

    public void getThreadList(String token) {
        Request request = new Request.Builder()
                .header("Authorization", "BEARER " + token)
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "getThreadListOnFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.d(TAG, "getThreadListOnResponse: " + str);

                Gson gson=new Gson();
                final ThreadsListResponse threadsListResponse = gson.fromJson(str, ThreadsListResponse.class);
                threadsList=threadsListResponse.threads;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        threadsAdapter = new ThreadsAdapter(ThreadsActivity.this, R.layout.threads_listview, threadsList,ThreadsActivity.this);
                        threadItemsLV.setAdapter(threadsAdapter);
                    }
                });
            }
        });
    }

    public void addNewThread(String token,String title){

        RequestBody formBody = new FormBody.Builder()
                .add("title", title)
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "BEARER " + token)
                .post(formBody)
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread/add").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "addNewThreadOnFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.d(TAG, "addNewThreadOnResponse: " + str);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ThreadsActivity.this, "New thread created", Toast.LENGTH_SHORT).show();
                        newThreadET.setText("");
                        //threadsAdapter.notifyDataSetChanged();
                        getThreadList(getToken());
                    }
                });
            }
        });

    }

    public String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREF_FILE), Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    public String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREF_FILE), Context.MODE_PRIVATE);
        String fName= sharedPreferences.getString("user_fname", "");
        String lName= sharedPreferences.getString("user_lname", "");
        return fName+" "+lName;
    }

    @Override
    public void deleteThread(String token,String thread_id){
        Request request = new Request.Builder()
                .header("Authorization", "BEARER " + token)
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread/delete/"+thread_id).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "deleteThreadOnFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.d(TAG, "deleteThreadOnResponse: "+str);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ThreadsActivity.this, "thread deleted", Toast.LENGTH_SHORT).show();
                        getThreadList(getToken());
                    }
                });
            }
        });
    }


}
