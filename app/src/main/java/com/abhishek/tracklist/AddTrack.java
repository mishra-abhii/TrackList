package com.abhishek.tracklist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddTrack extends AppCompatActivity {

    private static final CharSequence COPY_LIST = "copiedlist";
    TextView tvArtistName , tvCopy;
    EditText etTrackName;
    TextView btnAddTrack;
    RatingBar ratingBar;
    String name;

    List<Track> trackList;
    ListView listViewTracks;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);
        setTitle("TrackList");

        tvArtistName = findViewById(R.id.tvArtistName);
        tvCopy = findViewById(R.id.tvCopy);
        etTrackName = findViewById(R.id.etTrackName);
        btnAddTrack = findViewById(R.id.btnAddTrack);
        ratingBar = findViewById(R.id.ratingBar);

        trackList = new ArrayList<>();
        listViewTracks = findViewById(R.id.listViewTracks);

        // code to get data passed through intent putExtra
        // to give same id to child of Tracks(node) as of the respective child's id of the Artist(node)
        Intent intent = getIntent();
        String id = intent.getStringExtra(MainActivity.ARTIST_ID);
        name = intent.getStringExtra(MainActivity.ARTIST_NAME);
        tvArtistName.setText(name);


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).child("Tracks").child(id);

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

        tvCopy.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                copyList();
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

    // code to pass data from the list to string builder object
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void copyList(){
        StringBuilder ListItems = new StringBuilder();
        ListItems.append("Artist name : ").append(name).append("\n").append("\n");
        for (int i = 0; i < trackList.size(); i++) {
            ListItems.append("Track Name : ").append(trackList.get(i).getTrackName()).append("\n");
            ListItems.append("Track Rating : ").append(trackList.get(i).getTrackRating()).append("\n\n");
        }

//      Below code is used to copy the list to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(COPY_LIST, ListItems);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Track list copied", Toast.LENGTH_SHORT).show();


    }

    // method to create dialog box
    private void showDeleteDialog(String trackId, String trackName){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.delete_track_dialogbox , null);

        dialogBuilder.setView(dialogView);

        final TextView tv_track_name = dialogView.findViewById(R.id.tv_track_name);
        final TextView btnDeleteTrack = dialogView.findViewById(R.id.btnDeleteTrack);

        tv_track_name.setText(trackName);

        dialogBuilder.setTitle("Delete Track");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // code to delete Track list data from firebase database
        btnDeleteTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String id1 = intent.getStringExtra(MainActivity.ARTIST_ID);
                DatabaseReference drTrackDelete = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users")
                        .child(Objects.requireNonNull(auth.getUid()))
                        .child("Tracks").child(id1).child(trackId);

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