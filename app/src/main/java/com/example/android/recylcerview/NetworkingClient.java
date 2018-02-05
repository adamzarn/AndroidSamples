package com.example.android.recylcerview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

/**
 * Created by adamzarn on 1/31/18.
 */

class NetworkingClient {

    public interface PostsReceivedListener {
        void didReceivePosts(ArrayList<Post> posts);
    }

    private final static String BASE_URL = "https://jsonplaceholder.typicode.com/";
    private final static String POSTS_STRING = "posts";

    public static void getPosts(PostsReceivedListener activityContext) {
        try {
            URL postsUrl = new URL(BASE_URL + POSTS_STRING);
            GetPostsAsyncTask task = new GetPostsAsyncTask(activityContext);
            task.execute(postsUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static class GetPostsAsyncTask extends AsyncTask<URL, Void, String> {

        private PostsReceivedListener postsReceivedListener;

        public GetPostsAsyncTask(PostsReceivedListener activityContext) {
            this.postsReceivedListener = activityContext;
        }

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String results = null;
            try {
                results = getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject[] jsonObjectArray = getJsonObjectArray(getJsonArray(s));

            ArrayList<Post> posts = new ArrayList<Post>();
            for (JSONObject post: jsonObjectArray) {
                try {
                    Post newPost = new Post(
                            post.getInt("userId"),
                            post.getInt("id"),
                            post.getString("title"),
                            post.getString("body"));
                    posts.add(newPost);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            DatabaseClient.savePosts(posts, ((Context) postsReceivedListener));
            postsReceivedListener.didReceivePosts(posts);
        }
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static JSONArray getJsonArray(String response) {
        JSONArray array = null;
        try {
            array = new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    private static JSONObject[] getJsonObjectArray(JSONArray array) {
        JSONObject[] jsonObjectArray = new JSONObject[array.length()];
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                jsonObjectArray[i] = jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObjectArray;
    }

    public static boolean hasInternetAccess(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
