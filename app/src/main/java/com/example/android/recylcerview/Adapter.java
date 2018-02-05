package com.example.android.recylcerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adamzarn on 5/28/17.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {

    public ArrayList<Post> posts;

    @Override
    public int getItemCount() {
        if (posts == null) {
            return 0;
        }
        return posts.size();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView itemTitleTextView;
        public final TextView itemBodyTextView;

        public AdapterViewHolder(View view) {
            super(view);
            itemTitleTextView = (TextView) view.findViewById(R.id.recycler_view_item_title);
            itemBodyTextView = (TextView) view.findViewById(R.id.recycler_view_item_body);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            myClickHandler.onClick(posts.get(adapterPosition).getTitle());
        }
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recycler_view_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder adapterViewHolder, int position) {
        Post currentPost = posts.get(position);
        String title = position + ". " + currentPost.getTitle();
        adapterViewHolder.itemTitleTextView.setText(title);
        adapterViewHolder.itemBodyTextView.setText(currentPost.getBody());
    }

    private final AdapterOnClickHandler myClickHandler;

    public interface AdapterOnClickHandler {
        void onClick(String city);
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public Adapter(AdapterOnClickHandler clickHandler) {
        myClickHandler = clickHandler;
    }
}
