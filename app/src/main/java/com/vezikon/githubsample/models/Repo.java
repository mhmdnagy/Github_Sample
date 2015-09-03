package com.vezikon.githubsample.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vezikon on 9/2/15.
 */
public class Repo implements Parcelable {

    private String name;
    private String owner_name;
    private String description;
    private String repo_url;
    private String owner_url;
    private boolean fork;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepo_url() {
        return repo_url;
    }

    public void setRepo_url(String repo_url) {
        this.repo_url = repo_url;
    }

    public String getOwner_url() {
        return owner_url;
    }

    public void setOwner_url(String owner_url) {
        this.owner_url = owner_url;
    }


    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.owner_name);
        dest.writeString(this.description);
        dest.writeString(this.repo_url);
        dest.writeString(this.owner_url);
        dest.writeByte(fork ? (byte) 1 : (byte) 0);
    }

    public Repo() {
    }

    protected Repo(Parcel in) {
        this.name = in.readString();
        this.owner_name = in.readString();
        this.description = in.readString();
        this.repo_url = in.readString();
        this.owner_url = in.readString();
        this.fork = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Repo> CREATOR = new Parcelable.Creator<Repo>() {
        public Repo createFromParcel(Parcel source) {
            return new Repo(source);
        }

        public Repo[] newArray(int size) {
            return new Repo[size];
        }
    };
}
