package com.example.princ.inclass10;

import java.io.Serializable;
import java.util.ArrayList;

public class ThreadsListResponse {
    String status;
    ArrayList<MessageThread> threads = new ArrayList<>();

    public class MessageThread implements Serializable{
        String user_fname,user_lname,user_id,id,title,created_at;

        @Override
        public String toString() {
            return "MessageThread{" +
                    "user_fname='" + user_fname + '\'' +
                    ", user_lname='" + user_lname + '\'' +
                    ", user_id='" + user_id + '\'' +
                    ", id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", created_at='" + created_at + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ThreadsListResponse{" +
                "status='" + status + '\'' +
                ", threads=" + threads +
                '}';
    }
}
