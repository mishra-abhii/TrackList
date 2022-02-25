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

public class ArtistTrack_Adapter extends ArrayAdapter<Track> {

    private Activity context;
    private List<Track> trackList;

    public ArtistTrack_Adapter(Activity context , List<Track> trackList) {
        super(context, R.layout.track_layout , trackList);
        this.context = context;
        this.trackList = trackList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View trackItemList = inflater.inflate(R.layout.track_layout , null , true);

        TextView trackName = trackItemList.findViewById(R.id.tvTrackName);
        TextView trackRating = trackItemList.findViewById(R.id.tvRating);

        Track track = trackList.get(position);
        trackName.setText(track.getTrackName());
        trackRating.setText(String.valueOf(track.getTrackRating()));

        return trackItemList;
    }
}
