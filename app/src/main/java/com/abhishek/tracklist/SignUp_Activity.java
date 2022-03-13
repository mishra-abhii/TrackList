package com.abhishek.tracklist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUp_Activity extends AppCompatActivity {

    TextView etUsername , etEmail , etPassword , tvAlreadyhaveaccount;
    Button btnSignup;

    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        Objects.requireNonNull(getSupportActionBar()).hide(); /* To remove action bar form this particular activity as by default it is set for all activity*/
        
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvAlreadyhaveaccount = findViewById(R.id.tvAlreadyhaveaccount);

        btnSignup = findViewById(R.id.btnSignup);


        auth = FirebaseAuth.getInstance();    /* this will be used to take email and password and sign up */
        database = FirebaseDatabase.getInstance();   /* this will helps us to save values given in the editTexts  */

//      code for progress dialog
        progressDialog = new ProgressDialog(SignUp_Activity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");

        tvAlreadyhaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp_Activity.this , SignIn_Activity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if(TextUtils.isEmpty(etUsername.getText().toString()) || TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
                    progressDialog.dismiss();
                    Toast.makeText(SignUp_Activity.this, "Please add your credentials", Toast.LENGTH_SHORT).show();
                }
                else {
                    auth.createUserWithEmailAndPassword
                            (etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) {
                                /* use of Users class we have made */
                                Users user = new Users(etUsername.getText().toString(), etEmail.getText().toString(), auth.getUid());
                                
                                // Creating a node named 'Users' in which data of diff user will be stored who create account
                                database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).setValue(user); 
                                Toast.makeText(SignUp_Activity.this, "Account created Successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignUp_Activity.this , MainActivity.class);
                                startActivity(i);
                                finish();
                                
                            } else {
                                Toast.makeText(SignUp_Activity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        
    }
}
