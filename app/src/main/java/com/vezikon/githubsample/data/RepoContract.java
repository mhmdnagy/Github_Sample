package com.vezikon.githubsample.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by vezikon on 9/2/15.
 */
public class RepoContract {

    public static final String CONTENT_AUTHORITY = "com.vezikon.githubsample";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_REPOS = "repos";


    public static class RepoEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REPOS)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPOS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPOS;


        public static final String TABLE_NAME = "repo";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_OWNER_NAME = "owner_name";
        public static final String COLUMN_DESCRIPTION = "desc";
        public static final String COLUMN_FORK = "fork";
        public static final String COLUMN_REPO_URL = "repo_url";
        public static final String COLUMN_OWNER_URL = "owner_url";

        public static Uri buildReposUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
