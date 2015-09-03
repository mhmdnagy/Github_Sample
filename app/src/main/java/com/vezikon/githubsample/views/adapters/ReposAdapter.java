package com.vezikon.githubsample.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vezikon.githubsample.R;
import com.vezikon.githubsample.models.Repo;


import java.util.ArrayList;

/**
 * Created by vezikon on 9/2/15.
 */
public class ReposAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Repo> reposArrayList;

    public ReposAdapter(Context context, ArrayList<Repo> reposArrayList) {
        this.context = context;
        this.reposArrayList = reposArrayList;
    }

    @Override
    public int getCount() {
        return reposArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return reposArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_repo, null);

            holder = new ViewHolder(view);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Repo repo = (Repo) getItem(i);

        holder.name.setText(repo.getName());
        holder.owner_name.setText(repo.getOwner_name());
        holder.description.setText(repo.getDescription());

        if (repo.isFork()) {
            holder.container.setBackgroundResource(R.color.green_transparent);
        } else {
            holder.container.setBackgroundResource(R.color.white);
        }

        return view;
    }

    static class ViewHolder {

        TextView name;
        TextView owner_name;
        TextView description;
        LinearLayout container;

        ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.repo_name);
            owner_name = (TextView) view.findViewById(R.id.repo_owner);
            description = (TextView) view.findViewById(R.id.repo_desc);
            container = (LinearLayout) view.findViewById(R.id.list_item_container);
        }
    }

}
