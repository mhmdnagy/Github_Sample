package com.vezikon.githubsample.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.vezikon.githubsample.data.RepoContract.*;

/**
 * Created by vezikon on 9/2/15.
 */
public class ReposProvider extends ContentProvider {

    public final static int REPOS = 100;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    private ReposDbHelper moviesDbHelper;

    static {

        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_REPOS, REPOS);
    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new ReposDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        switch (matcher.match(uri)) {
            case REPOS:
                cursor = moviesDbHelper.getReadableDatabase().query(RepoEntry.TABLE_NAME
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , sortOrder);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (matcher.match(uri)) {
            case REPOS:
                return RepoEntry.CONTENT_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = matcher.match(uri);
        Uri returnUri;

        switch (match) {
            case REPOS: {
                long _id = db.insert(RepoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RepoEntry.buildReposUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new android.database.SQLException("Failed to insert row into " + uri);

        }

        //notify data change in the content provider
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = matcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case REPOS:
                rowsDeleted = db.delete(
                        RepoEntry.TABLE_NAME, selection, selectionArgs);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            Log.d("content resolver", "notified");
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = matcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case REPOS:
                rowsUpdated = db.update(RepoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
