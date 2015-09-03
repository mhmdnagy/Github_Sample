package com.vezikon.githubsample.parsers;

import android.util.JsonReader;

import com.vezikon.githubsample.models.Repo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by vezikon on 9/2/15.
 */
public class RepoParser {

    private Repo repo;

    public ArrayList parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        try {
            return readMessagesArray(reader);

        } finally {
            reader.close();
        }
    }


    public ArrayList readMessagesArray(JsonReader reader) throws IOException {
        ArrayList repos = new ArrayList();


        reader.beginArray();

        while (reader.hasNext()) {
            repos.add(readMessage(reader));
        }
        reader.endArray();
        return repos;
    }

    public Repo readMessage(JsonReader reader) throws IOException {

        repo = new Repo();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("name")) {
                repo.setName(reader.nextString());
            } else if (name.equals("description")) {
                repo.setDescription(reader.nextString());
            } else if (name.equals("owner")) {
                ReadOwnerMessage(reader);
            } else if (name.equals("html_url")) {
                repo.setRepo_url(reader.nextString());
            } else if (name.equals("fork")) {
                repo.setFork(reader.nextBoolean());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return repo;
    }

    public void ReadOwnerMessage(JsonReader reader) throws IOException {

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("login")) {
                repo.setOwner_name(reader.nextString());
            } else if (name.equals("html_url")) {
                repo.setOwner_url(reader.nextString());
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
    }

}
