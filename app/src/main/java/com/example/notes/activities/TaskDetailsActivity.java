package com.example.notes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.notes.R;
import com.example.notes.model.List;
import com.example.notes.model.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class TaskDetailsActivity extends AppCompatActivity {

    TextView title  , listName, edit , save , back, delete;
    EditText description ;
    String email ;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
         db = FirebaseFirestore.getInstance();
        title = findViewById(R.id.tv_title);
        edit = findViewById(R.id.tv_edit);
        description = findViewById(R.id.tv_description);
        listName = findViewById(R.id.tv_list_name);
        save = findViewById(R.id.tv_save);
        back = findViewById(R.id.tv_back);
        delete = findViewById(R.id.tv_delete);

        Task task = (Task) getIntent().getSerializableExtra("task");
        List list = (List) getIntent().getSerializableExtra("list");
        email = getIntent().getStringExtra("email");

        title.setText(task.getTitle());
        description.setText(task.getDescription());
        listName.setText(list.getName());
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description.setEnabled(true);
                save.setVisibility(View.VISIBLE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("title" , title.getText().toString());
                hashMap.put("description" , description.getText().toString());
                hashMap.put("isCompleted" ,task.isCompleted());


                db.collection("Users").document(email).collection("Lists").document(list.getId()).collection("tasks")
                        .document(task.getId())
                        .update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        save.setVisibility(View.GONE);
                        description.setEnabled(false);
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Users").document(email).collection("Lists").document(list.getId()).collection("tasks")
                        .document(task.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent =  new Intent(TaskDetailsActivity.this , TasksActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

    }
}