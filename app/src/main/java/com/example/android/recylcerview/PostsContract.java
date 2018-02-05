package com.example.android.recylcerview;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by adamzarn on 2/2/18.
 */

public class PostsContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.recyclerview";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_POSTS = "posts";

    public static final class PostsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POSTS)
                .build();

        public static final String TABLE_NAME = "posts";

        public static final String COLUMN_USER_ID = "userId";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BODY = "body";

    }

}
