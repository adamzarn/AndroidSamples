package com.example.android.recylcerview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adamzarn on 2/2/18.
 */

public class PostsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "posts.db";
    private static final int DATABASE_VERSION = 1;

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public PostsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_POSTS_TABLE =

                "CREATE TABLE " + PostsContract.PostsEntry.TABLE_NAME + " (" +

                        PostsContract.PostsEntry._ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        PostsContract.PostsEntry.COLUMN_USER_ID         + " REAL NOT NULL, "                     +
                        PostsContract.PostsEntry.COLUMN_ID              + " REAL NOT NULL, "                     +
                        PostsContract.PostsEntry.COLUMN_TITLE           + " REAL NOT NULL, "                     +
                        PostsContract.PostsEntry.COLUMN_BODY            + " REAL NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_POSTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PostsContract.PostsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
