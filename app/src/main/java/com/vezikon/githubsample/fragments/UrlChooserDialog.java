package com.vezikon.githubsample.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.vezikon.githubsample.R;
import com.vezikon.githubsample.models.Repo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UrlChooserDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UrlChooserDialog extends DialogFragment implements View.OnClickListener {


    private static final String REPO = "repo";

    private Repo repo;

    //UI
    Button btnRepoUrl;
    Button btnOwnerUrl;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param repo instance of {@link Repo}.
     * @return A new instance of fragment UrlChooserDialog.
     */
    public static UrlChooserDialog newInstance(Repo repo) {
        UrlChooserDialog fragment = new UrlChooserDialog();
        Bundle args = new Bundle();
        args.putParcelable(REPO, repo);
        fragment.setArguments(args);
        return fragment;
    }

    public UrlChooserDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            repo = getArguments().getParcelable(REPO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_url_chooser, container, false);

        btnRepoUrl = (Button) view.findViewById(R.id.btn_repo_url);
        btnOwnerUrl = (Button) view.findViewById(R.id.btn_owner_url);

        btnRepoUrl.setOnClickListener(this);
        btnOwnerUrl.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_owner_url:
                sendToBrowser(repo.getOwner_url());
                break;
            case R.id.btn_repo_url:
                sendToBrowser(repo.getRepo_url());
                break;
        }
    }

    //send this url to browser
    private void sendToBrowser(String url) {

        if (url == null) {
            Toast.makeText(getActivity(), R.string.error_msg_url_not_provided, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);

        dismiss();
    }
}
