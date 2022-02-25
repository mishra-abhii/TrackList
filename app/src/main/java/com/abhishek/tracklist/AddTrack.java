package com.abhishek.tracklist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrack extends AppCompatActivity {

    TextView tvArtistName;
    EditText etTrackName;
    Button btnAddTrack;
    RatingBar ratingBar;

    List<Track> trackList;
    ListView listViewTracks;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);
        setTitle("TrackList");

        tvArtistName = findViewById(R.id.tvArtistName);
        etTrackName = findViewById(R.id.etTrackName);
        btnAddTrack = findViewById(R.id.btnAddTrack);
        ratingBar = findViewById(R.id.ratingBar);

        trackList = new ArrayList<>();
        listViewTracks = findViewById(R.id.listViewTracks);

        // code to get data passed through intent putExtra
        Intent intent = getIntent();
        String id = intent.getStringExtra(MainActivity.ARTIST_ID);
        String name = intent.getStringExtra(MainActivity.ARTIST_NAME);
        tvArtistName.setText(name);

        // Giving same id to child of Tracks(node) as of the respective child's id of the Artist(node)
        databaseReference = FirebaseDatabase.getInstance().getReference("Tracks").child(id);

        btnAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String trackName = etTrackName.getText().toString().trim();
                float trackRating = ratingBar.getRating();

                if(TextUtils.isEmpty(trackName)){
                    Toast.makeText(AddTrack.this, "Please enter the Track", Toast.LENGTH_SHORT).show();
                }
                else{
                    String id1 = databaseReference.push().getKey(); // this id or key is for diff entries of child of node(tracks) added for the same artist
                    Track track = new Track(id1 , trackName , trackRating);
                    databaseReference.child(id1).setValue(track);

                    etTrackName.getText().clear();
                    Toast.makeText(AddTrack.this, "Track added..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listViewTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Track track = trackList.get(i);
                showDeleteDialog(track.getTrackId(), track.getTrackName());
            }
        });

    }

    // method to create dialog box
    private void showDeleteDialog(String trackId, String trackName){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.delete_track_dialogbox , null);

        dialogBuilder.setView(dialogView);

        final TextView tv_track_name = dialogView.findViewById(R.id.tv_track_name);
        final Button btnDeleteTrack = dialogView.findViewById(R.id.btnDeleteTrack);

        tv_track_name.setText(trackName);

        dialogBuilder.setTitle("Delete Track");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // code to delete Track list data from firebase database
        btnDeleteTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String id = intent.getStringExtra(MainActivity.ARTIST_ID);
                DatabaseReference drTrackDelete = FirebaseDatabase.getInstance().getReference("Tracks").child(id).child(trackId);

                drTrackDelete.removeValue();
                alertDialog.dismiss();

                Toast.makeText(AddTrack.this, "Deleted successfully..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                trackList.clear();

                for(DataSnapshot trackSnapshot : snapshot.getChildren()){
                    Track track = trackSnapshot.getValue(Track.class);
                    trackList.add(track);
                }

                ArtistTrack_Adapter adapter = new ArtistTrack_Adapter(AddTrack.this , trackList);
                listViewTracks.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}