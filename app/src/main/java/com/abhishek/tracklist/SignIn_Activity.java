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

public class SignIn_Activity extends AppCompatActivity {

    TextView etEmail_signin , etPassword_signin , tvCreateaccount;
    Button btnSignin;

    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Objects.requireNonNull(getSupportActionBar()).hide();
        etEmail_signin = findViewById(R.id.etEmail_signin);
        etPassword_signin = findViewById(R.id.etPassword_signin);
        tvCreateaccount = findViewById(R.id.tvCreateaccount);

        btnSignin = findViewById(R.id.btnSignin);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //      code for progress dialog
        progressDialog = new ProgressDialog(SignIn_Activity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Logging into the account");

        tvCreateaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn_Activity.this , SignUp_Activity.class);
                startActivity(intent);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if(TextUtils.isEmpty(etEmail_signin.getText().toString()) || TextUtils.isEmpty(etPassword_signin.getText().toString())){
                    progressDialog.dismiss();
                    Toast.makeText(SignIn_Activity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    auth.signInWithEmailAndPassword
                            (etEmail_signin.getText().toString(), etPassword_signin.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) {
                                Toast.makeText(SignIn_Activity.this, "Login successful..", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignIn_Activity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(SignIn_Activity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

//========================================================================================================

        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(SignIn_Activity.this , MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Both above and below are method to check if user is currently login or not

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = auth.getCurrentUser();
//        if (user != null) {
//            Intent i = new Intent(SignIn_Activity.this , MainActivity.class);
//            startActivity(i);
//            this.finish();
//        }
//    }

}
