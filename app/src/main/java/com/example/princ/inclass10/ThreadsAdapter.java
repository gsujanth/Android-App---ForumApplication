package com.example.princ.inclass10;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

 class ThreadsAdapter extends ArrayAdapter<ThreadsListResponse.MessageThread> {

    private ThreadsListResponse.MessageThread messageThread;
    private Context ctx;
    private DataUpdateAfterDelete dataUpdateAfterDelete;
    private  final  String TAG="demoThreadAdapter";
    ArrayList<ThreadsListResponse.MessageThread> messageThreadObjects;

    ThreadsAdapter(@NonNull Context context, int resource,@NonNull List<ThreadsListResponse.MessageThread> objects,ThreadsActivity threadsActivity) {
        super(context, resource,objects);
        this.ctx=context;
        this.dataUpdateAfterDelete=threadsActivity;
        this.messageThreadObjects= (ArrayList<ThreadsListResponse.MessageThread>) objects;
    }

     @NonNull
     @Override
     public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         messageThread = getItem(position);
         ViewHolder viewHolder;
         if(convertView==null) {
             convertView = LayoutInflater.from(getContext()).inflate(R.layout.threads_listview, parent, false);
             viewHolder = new ViewHolder();
             viewHolder.threadTitleTV = convertView.findViewById(R.id.threadTitleTV);
             viewHolder.deleteThreadButton=convertView.findViewById(R.id.deleteThreadButton);
             convertView.setTag(viewHolder);
         }else{
             viewHolder = (ViewHolder) convertView.getTag();
         }
         viewHolder.threadTitleTV.setText(messageThread.title);
         if(!messageThread.user_id.equals(getUserId())){
             viewHolder.deleteThreadButton.setVisibility(View.INVISIBLE);
         }else{
             viewHolder.deleteThreadButton.setVisibility(View.VISIBLE);
         }
         viewHolder.deleteThreadButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.d(TAG, "deleteThreadButtonOnClick: "+messageThreadObjects.get(position).id);
                 dataUpdateAfterDelete.deleteThread(getToken(),messageThreadObjects.get(position).id);
             }
         });
        return convertView;
     }

     private static class ViewHolder{
         TextView threadTitleTV;
         ImageButton deleteThreadButton;
     }

     private String getUserId(){
         SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getString(R.string.LOGIN_PREF_FILE), Context.MODE_PRIVATE);
         return sharedPreferences.getString("user_id", "");
     }

     private String getToken() {
         SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getString(R.string.LOGIN_PREF_FILE), Context.MODE_PRIVATE);
         return sharedPreferences.getString("token", "");
     }

     public interface DataUpdateAfterDelete{
         void deleteThread(String token,String thread_id);
     }

 }
