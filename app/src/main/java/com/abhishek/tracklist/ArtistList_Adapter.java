package com.abhishek.tracklist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ArtistList_Adapter extends ArrayAdapter<Artist> {

    private Activity context;
    private List<Artist> artistList;

    // Constructor
    public ArtistList_Adapter(Activity context, List<Artist> artistList) {
        super(context, R.layout.list_layout , artistList);
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItems = inflater.inflate(R.layout.list_layout , null , true);

        TextView tvName = listViewItems.findViewById(R.id.tvName);
        TextView tvGenre = listViewItems.findViewById(R.id.tvGenre);

        Artist artist = artistList.get(position);
        tvName.setText(artist.getArtistName());
        tvGenre.setText(artist.getArtistGenre());

        return listViewItems;
    }
}
