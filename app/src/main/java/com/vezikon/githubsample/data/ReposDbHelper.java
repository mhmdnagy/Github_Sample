package com.vezikon.githubsample.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vezikon.githubsample.models.Repo;

import static com.vezikon.githubsample.data.RepoContract.*;

/**
 * Created by vezikon on 9/2/15.
 */
public class ReposDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "repos.db";

    public ReposDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_REPO_TABLE = "CREATE TABLE " + RepoEntry.TABLE_NAME
                + " ("
                + RepoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RepoEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + RepoEntry.COLUMN_OWNER_NAME + " TEXT NOT NULL, "
                + RepoEntry.COLUMN_DESCRIPTION + " TEXT, "
                + RepoEntry.COLUMN_OWNER_URL + " TEXT, "
                + RepoEntry.COLUMN_REPO_URL + " TEXT, "
                + RepoEntry.COLUMN_FORK + " INTEGER "
                + " );";

        db.execSQL(SQL_CREATE_REPO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + RepoEntry.TABLE_NAME);

        onCreate(db);
    }
}
