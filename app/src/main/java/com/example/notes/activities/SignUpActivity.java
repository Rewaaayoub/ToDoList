package com.example.notes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notes.DatabaseHandler;
import com.example.notes.R;
import com.example.notes.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    private EditText et_email, et_name, et_password;
    private String email, password, name;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        findViews();

    }

    private void setValues() {
        email = et_email.getText().toString();
        password = et_password.getText().toString();
        name = et_name.getText().toString();
    }

    private void findViews() {
        et_email = findViewById(R.id.et_email);
        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);

    }


    public void signUp(View view) {
        setValues();
        if (email.equals("") || name.equals("") || password.equals("") )
            Toast.makeText(this, "All field must be filled", Toast.LENGTH_SHORT).show();

        else if (password.length() < 6)
            Toast.makeText(this, "Password can't less than 6", Toast.LENGTH_SHORT).show();


        else {
            final User user = new User(name, email);
            final ProgressDialog proDialog = ProgressDialog.show(SignUpActivity.this, "", "logging");

            final DatabaseHandler sqliteDB = new DatabaseHandler(this);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                final String userId = currentUser.getUid();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                db.collection("Users")
                                        .document(email)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUpActivity.this, "Successful registered", Toast.LENGTH_SHORT).show();

                                                sqliteDB.addUser(user);
                                                proDialog.cancel();
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                proDialog.cancel();
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


        }


    }

    public void login(View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}