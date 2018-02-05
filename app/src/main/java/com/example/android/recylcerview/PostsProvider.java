package com.example.android.recylcerview;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by adamzarn on 2/2/18.
 */

public class PostsProvider extends ContentProvider {
    public static final int CODE_POSTS = 100;

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private PostsDbHelper postsDbHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PostsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PostsContract.PATH_POSTS, CODE_POSTS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        postsDbHelper = new PostsDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = postsDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case CODE_POSTS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(PostsContract.PostsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = postsDbHelper.getReadableDatabase();

        Cursor retCursor;

        switch (uriMatcher.match(uri)) {

            case CODE_POSTS: {
                db.beginTransaction();
                try {
                    retCursor = db.query(
                            PostsContract.PostsEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    db.setTransactionSuccessful();
                    break;
                } finally {
                    db.endTransaction();
                }
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (uriMatcher.match(uri)) {

            case CODE_POSTS:
                numRowsDeleted = postsDbHelper.getWritableDatabase().delete(
                        PostsContract.PostsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not implementing getType.");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = postsDbHelper.getWritableDatabase();

        Uri returnUri;

        switch (uriMatcher.match(uri)) {

            case CODE_POSTS:

                long id = db.insert(PostsContract.PostsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PostsContract.PostsEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;

    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("Not implementing update.");
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        postsDbHelper.close();
        super.shutdown();
    }
}
