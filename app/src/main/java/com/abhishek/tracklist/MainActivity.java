package com.abhishek.tracklist;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String ARTIST_NAME = "artistname"; // created for the ref purpose to pass data from one activity to other while using intent
    public static final String ARTIST_ID = "artistid";  // created for the ref purpose to pass data from one activity to other while using intent

    EditText etArtistName;
    Button btnAddArtist;
    Spinner spinner;
    ListView listViewArtist;
    List<Artist> artistList;

    private FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("TrackList");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).child("Artist"); // path is used to create node named Artist

        etArtistName = findViewById(R.id.etArtistName);
        btnAddArtist = findViewById(R.id.btnAddArtist);
        spinner = findViewById(R.id.spinner);
        listViewArtist = findViewById(R.id.listViewArtist);
        artistList = new ArrayList<>();

        btnAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddArtist();
            }
        });

        // code for moving to Activity which shows Track list on clicking List items
        listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artistList.get(i);
                Intent intent = new Intent(MainActivity.this , AddTrack.class);

                // will pass data in AddTrack Activity
                intent.putExtra(ARTIST_ID , artist.getArtistId());
                intent.putExtra(ARTIST_NAME , artist.getArtistName());
                startActivity(intent);
            }
        });

        // To open dialog box for updating name
        listViewArtist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Artist artist = artistList.get(i);

                showUpdateDialog(artist.getArtistId() , artist.getArtistName());
                return false;
            }
        });
    }

    // code to fetch data from firebase
    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                This method is used to read the value from database as it will fetch all the values inside
//                the specified reference which is Artist and it will contain all the data inside this DataSnapshot object

                artistList.clear(); // this will prevent this method to again and again fetch all the data stored in the database and only new
                // data stored will be fetched every time excluding the ones which are already fetched
                for(DataSnapshot artistSnapshot : snapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    artistList.add(artist);
                }

                ArtistList_Adapter adapter = new ArtistList_Adapter(MainActivity.this , artistList);
                listViewArtist.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // code for Update Dialog Box and updating value
    private void showUpdateDialog(String artistId , String artistName){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialogbox , null);

        dialogBuilder.setView(dialogView);

//        final TextView tvOldName = dialogView.findViewById(R.id.tvOldName);
        final EditText etNewName = dialogView.findViewById(R.id.etNewName);
        final Spinner spinner2 = dialogView.findViewById(R.id.spinner_update);
        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        dialogBuilder.setTitle("Update Artist Name");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // code to update the data stored in firebase database
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etNewName.getText().toString().trim();
                String genre = spinner2.getSelectedItem().toString();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(MainActivity.this, "Name Required", Toast.LENGTH_SHORT).show();
                }
                else{
                    Artist artist = new Artist(artistId, name , genre);
                    databaseReference.child(artistId).setValue(artist);
                    Toast.makeText(MainActivity.this, "Name updated successfully..", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }

            }
        });

        // code to delete data from firebase database
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child(artistId).removeValue();
                DatabaseReference drTrack = FirebaseDatabase.getInstance()
                        .getReference().child("Users")
                        .child(Objects.requireNonNull(auth.getUid()))
                        .child("Tracks").child(artistId);

                drTrack.removeValue();
                alertDialog.dismiss();

                Toast.makeText(MainActivity.this, "Deleted successfully..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void AddArtist(){
        String name = etArtistName.getText().toString().trim();
        String genre = spinner.getSelectedItem().toString();  // to get the selected item from array genres

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter the name", Toast.LENGTH_SHORT).show();
        }
        else{
            // push method creates a unique key inside the node Artist
            String id = databaseReference.push().getKey();
            Artist artist = new Artist(id, name , genre);
            databaseReference.child(id).setValue(artist);  // child(id) is used so that every artist created will be assigned a unique key stored in id variable

            etArtistName.getText().clear();
            Toast.makeText(this, "Artist added..", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, SignIn_Activity.class);
            finish();
        }
        return true;
    }
}