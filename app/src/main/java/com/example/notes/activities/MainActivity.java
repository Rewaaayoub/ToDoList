package com.example.notes.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.model.List;
import com.example.notes.adapter.ListAdapter;
import com.example.notes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private RecyclerView rv_lists;
    private EditText search ;
    private LinearLayoutManager linearLayoutManager;
     private ListAdapter adapter ;
    private ArrayList<List> lists = new ArrayList<>();
    private FirebaseFirestore db;
    private String email;
    private SharedPreferences sharedPreferences;
    private ArrayList<List> searchedList = new ArrayList<>();
    TextView logout;

    Dialog dialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("user info", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        email = sharedPreferences.getString("email", null);
        Button btnAddList = findViewById(R.id.btn_add_list);
        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog(MainActivity.this);
            }
        });
        findViews();
        getLists();


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchedList.clear();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (i2 > 0 ){
                    for (List list : lists){
                        if (list.getName().contains(charSequence.toString())){
                            searchedList.add(list);
                            adapter = new ListAdapter(MainActivity.this , searchedList , email);
                            rv_lists.setAdapter(adapter);
                        }
                    }
                }else if (i2 == 0){
                    adapter = new ListAdapter(MainActivity.this , lists , email);
                    rv_lists.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void findViews() {
        rv_lists = findViewById(R.id.rv_lists);
        search = findViewById(R.id.et_search);
        logout = findViewById(R.id.tv_logout);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_lists.setLayoutManager(linearLayoutManager);
    }


    private void getLists() {
        db.collection("Users").document(email).collection("Lists")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    lists.clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String name = (String) documentSnapshot.getData().get("name");
                        Number taskNumber = (Number) documentSnapshot.getData().get("taskNumber");
                        String id = (String) documentSnapshot.getId();
                        List list = new List(name, taskNumber.intValue());
                        list.setId(id);
                        lists.add(list);
                        adapter = new ListAdapter(MainActivity.this, lists,email);
                        rv_lists.setAdapter(adapter);
                    }
                }
            }
        });
    }


    public void showCustomDialog(final Context context) {
        dialog = new Dialog(context);
       dialog.setContentView(R.layout.create_list_dialog);
        EditText listName = dialog.findViewById(R.id.et_list_name);
        Button btnAddList = dialog.findViewById(R.id.btn_add);
        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listName.getText().toString().isEmpty()) {
                    HashMap<String, Object> list = new HashMap<>();
                    list.put("name", listName.getText().toString());
                    int number = 0;
                    list.put("taskNumber", number);
                    db.collection("Users").document(email).collection("Lists").document().set(list).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "from complete", Toast.LENGTH_SHORT).show();
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Successfully Add note", Toast.LENGTH_SHORT).show();
                                 getLists();

                            }

                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });



        dialog.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (dialog!=null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}