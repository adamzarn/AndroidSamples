package com.example.android.recylcerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.recylcerview.Adapter.AdapterOnClickHandler;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterOnClickHandler, NetworkingClient.PostsReceivedListener, DatabaseClient.PostsReceivedListener {

    private RecyclerView myRecyclerView;
    private Adapter myAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        myRecyclerView.setLayoutManager(layoutManager);
        myRecyclerView.setHasFixedSize(true);
        myAdapter = new Adapter(this);
        myRecyclerView.setAdapter(myAdapter);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    if (NetworkingClient.hasInternetAccess(MainActivity.this)) {
                        NetworkingClient.getPosts(MainActivity.this);
                    } else {
                        DatabaseClient.getPosts(MainActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    @Override
    public void onClick(String city) {
        Context context = this;
        Toast.makeText(context, city + " clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didReceivePosts(ArrayList<Post> posts) {
        myAdapter.setPosts(posts);
    }
}
