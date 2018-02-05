package com.example.android.recylcerview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.id;

/**
 * Created by adamzarn on 2/2/18.
 */

public class DatabaseClient {

    public interface PostsReceivedListener {
        void didReceivePosts(ArrayList<Post> posts);
    }

    public static void getPosts(PostsReceivedListener activityContext) {
        GetPostsAsyncTask task = new GetPostsAsyncTask(activityContext);
        task.execute();
    }

    private static class GetPostsAsyncTask extends AsyncTask<URL, Void, Cursor> {

        private PostsReceivedListener postsReceivedListener;

        public GetPostsAsyncTask(PostsReceivedListener activityContext) {
            this.postsReceivedListener = activityContext;
        }

        @Override
        protected Cursor doInBackground(URL... params) {
            try {
                return ((Context) postsReceivedListener).getContentResolver().query(PostsContract.PostsEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        PostsContract.PostsEntry._ID);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor data) {
            super.onPostExecute(data);
            ArrayList<Post> posts = new ArrayList<Post>();
            while (data.moveToNext()) {
                int userId = data.getInt(data.getColumnIndex("userId"));
                int id = data.getInt(data.getColumnIndex("id"));
                String title = data.getString(data.getColumnIndex("title"));
                String body = data.getString(data.getColumnIndex("body"));
                Post post = new Post(userId, id, title, body);
                posts.add(post);
            }
            postsReceivedListener.didReceivePosts(posts);
        }
    }

    public static Boolean savePosts(ArrayList<Post> posts, Context context) {
        int rowsDeleted = context.getContentResolver().delete(PostsContract.PostsEntry.CONTENT_URI, null, null);
        if (rowsDeleted > 0) {
            System.out.println("Rows Deleted: " + rowsDeleted);
            ContentValues[] contentValuesArray = new ContentValues[posts.size()];
            int i = 0;
            for (Post post : posts) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PostsContract.PostsEntry.COLUMN_ID, id);
                contentValues.put(PostsContract.PostsEntry.COLUMN_USER_ID, post.getUserId());
                contentValues.put(PostsContract.PostsEntry.COLUMN_ID, post.getId());
                contentValues.put(PostsContract.PostsEntry.COLUMN_TITLE, post.getTitle());
                contentValues.put(PostsContract.PostsEntry.COLUMN_BODY, post.getBody());
                contentValuesArray[i] = contentValues;
                i++;
            }

            int rowsInserted = context.getContentResolver().bulkInsert(PostsContract.PostsEntry.CONTENT_URI, contentValuesArray);
            System.out.println("Rows inserted: " + rowsInserted);
            return rowsInserted > 0;
        }
        return false;

    }

}
