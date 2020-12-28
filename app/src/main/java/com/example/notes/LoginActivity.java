package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText et_email , et_password  ;
    private String email, password;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore db ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        findViews();

    }

    private void setValues() {
        email = et_email.getText().toString();
        password = et_password.getText().toString();
    }


    private void findViews() {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
    }

    final DatabaseHandler sqliteDB = new DatabaseHandler(this);


    public void login(View view){
        setValues();
        if (email.equals("") || password.equals(""))
            Toast.makeText(this, "All field must be filled", Toast.LENGTH_SHORT).show();

        else if (password.length() < 6)
            Toast.makeText(this, "Password must be +6", Toast.LENGTH_SHORT).show();

        else {
            final ProgressDialog proDialog = ProgressDialog.show(this, "", "logging");

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        if (!sqliteDB.getUser(email)){
                            db.collection("Users").document(email)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot > task) {
                                    if (task.isSuccessful()) {

                                        String name = (String) task.getResult().get("name");
                                        sqliteDB.addUser(new User(name , email ));
                                        Toast.makeText(LoginActivity.this, name , Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });

                        }


                        editor = getSharedPreferences("user info", MODE_PRIVATE).edit();
                        editor.putString("email", email);
                        editor.apply();

                        proDialog.cancel();
                        Intent intent= new Intent(LoginActivity.this , MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    proDialog.cancel();
                }
            });

        }
    }


    public void signUp(View view){
        Intent intent = new Intent(LoginActivity.this , SignUpActivity.class);
        startActivity(intent);
    }
}