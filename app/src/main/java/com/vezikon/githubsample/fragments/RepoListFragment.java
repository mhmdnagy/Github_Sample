package com.vezikon.githubsample.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vezikon.githubsample.R;
import com.vezikon.githubsample.models.Repo;
import com.vezikon.githubsample.parsers.RepoParser;
import com.vezikon.githubsample.util.InfiniteScrollListener;
import com.vezikon.githubsample.util.Utils;
import com.vezikon.githubsample.views.adapters.ReposAdapter;

import java.io.IOException;
import java.util.ArrayList;

import static com.vezikon.githubsample.data.RepoContract.*;

/**
 * A fragment representing a list of Items.
 * <p>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p>
 */
public class RepoListFragment extends Fragment implements AbsListView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemLongClickListener {

    private static final String REPO_LIST = "repo.list";
    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ReposAdapter mAdapter;

    private ArrayList<Repo> reposArrayList;

    //projection
    public final static String[] REPO_COLUMNS = {
            RepoEntry.COLUMN_NAME,
            RepoEntry.COLUMN_DESCRIPTION,
            RepoEntry.COLUMN_OWNER_NAME,
            RepoEntry.COLUMN_REPO_URL,
            RepoEntry.COLUMN_OWNER_URL,
            RepoEntry.COLUMN_FORK
    };

    //Cursor indexes
    public static final int COL_NAME = 0;
    public static final int COL_DESC = 1;
    public static final int COL_OWNER_NAME = 2;
    public static final int COL_REPO_URL = 3;
    public static final int COL_OWNER_URL = 4;
    public static final int COL_FORK = 5;


    //UI
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    //API_KEY (Use your own access token)
    public static final String API_KEY = "";


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RepoListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            reposArrayList = savedInstanceState.getParcelableArrayList(REPO_LIST);
        } else {
            reposArrayList = new ArrayList<>();
        }

        mAdapter = new ReposAdapter(getActivity(), reposArrayList);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(REPO_LIST, reposArrayList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mListView = (ListView) view.findViewById(android.R.id.list);
        // Set the adapter
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener and OnItemLongClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        //load more scroll listener
        mListView.setOnScrollListener(new InfiniteScrollListener(2) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                //if this size is between (n -1) * 10 and n * 10
                //this means that we were in the last page and there are no other pages to load
                //it will only download new data when the size equals n * 10
                int size = reposArrayList.size() / 10;
                size++;

                if (size == Math.round(size)) {
                    //load the new page
                    loadMoreDelayer(size);
                }

            }
        });
        return view;
    }


    /**
     * Use this method to load more data
     *
     * we need to delay the request quiet a bit to avoid glitchy scrolling
     * @param page is page number
     */
    private void loadMoreDelayer(final int page) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new GithubApiFetcher().execute(String.valueOf(page));
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get cached data first
        getActivity().getSupportLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //do something here if you want
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        DialogFragment dialogFragment = UrlChooserDialog.newInstance(reposArrayList.get(i));
        dialogFragment.show(ft, "UrlChooserDialog");

        return false;
    }


    @Override
    public void onRefresh() {

        showProgress(true);

        //deleting cached data
        getActivity().getContentResolver().delete(RepoEntry.CONTENT_URI, null, null);

        //deleting current list
        reposArrayList.clear();
        mAdapter.notifyDataSetChanged();

        if (Utils.isNetworkAvailable(getActivity())) {
            new GithubApiFetcher().execute("1");
        } else {
            Toast.makeText(getActivity(), R.string.error_msg_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity()
                , RepoEntry.CONTENT_URI
                , REPO_COLUMNS
                , null
                , null
                , null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //if the cursor is empty... get data from the internet
        if (data.getCount() > 1) {

            //moving cursor to first
            data.moveToFirst();

            do {
                Repo repo = new Repo();
                repo.setName(data.getString(COL_NAME));
                repo.setOwner_name(data.getString(COL_OWNER_NAME));
                repo.setOwner_url(data.getString(COL_OWNER_URL));
                repo.setDescription(data.getString(COL_DESC));
                repo.setRepo_url(data.getString(COL_REPO_URL));
                repo.setFork(data.getInt(COL_FORK) == 0);

                reposArrayList.add(repo);

            } while (data.moveToNext());

            mAdapter.notifyDataSetChanged();

        } else {

            //show progress form
            showProgress(true);
            //get some data
            new GithubApiFetcher().execute("1");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    /*
     * GitHub API fetcher
     */
    private class GithubApiFetcher extends AsyncTask<String, Void, ArrayList<Repo>> {

        @Override
        protected ArrayList<Repo> doInBackground(String... strings) {
            try {
                return loadData(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Repo> repos) {
            super.onPostExecute(repos);

            if (repos != null) {
                reposArrayList.addAll(repos);
                mAdapter.notifyDataSetChanged();

                //cache the data
                cacheData(reposArrayList);

                //stop progress
                showProgress(false);


                //stop SwipeToRefreshLayout if it's working
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    /**
     * Use this method to add data to the content provider
     *
     * @param reposArrayList list of {@link Repo}
     */
    private void cacheData(ArrayList<Repo> reposArrayList) {
        ContentValues contentValues;
        for (Repo repo : reposArrayList) {
            contentValues = new ContentValues();
            contentValues.put(RepoEntry.COLUMN_NAME, repo.getName());
            contentValues.put(RepoEntry.COLUMN_OWNER_NAME, repo.getOwner_name());
            contentValues.put(RepoEntry.COLUMN_DESCRIPTION, repo.getDescription());
            contentValues.put(RepoEntry.COLUMN_OWNER_URL, repo.getOwner_url());
            contentValues.put(RepoEntry.COLUMN_REPO_URL, repo.getRepo_url());
            contentValues.put(RepoEntry.COLUMN_FORK, repo.isFork() ? 0 : 1);

            //insert the content value to the content provider
            getActivity().getContentResolver().insert(RepoEntry.CONTENT_URI, contentValues);
        }
    }

    /**
     * Use this method to fetch internet data from specific URL
     *
     * @param page the request page number
     * @return ArrayList of {@link Repo}
     * @throws IOException
     */
    private ArrayList<Repo> loadData(String page) throws IOException {

        //URL builder
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.github.com")
                .appendPath("users")
                .appendPath("square")
                .appendPath("repos")
                .appendQueryParameter("access_token", API_KEY)
                .appendQueryParameter("page", page)
                .appendQueryParameter("per_page", "10");


        RepoParser parser = new RepoParser();
        return parser.parse(Utils.downloadUrl(builder.build().toString()));
    }


    /**
     * Shows the progress UI and hides the List view form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            swipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            swipeRefreshLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    swipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            swipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
