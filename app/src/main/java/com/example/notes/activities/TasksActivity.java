package com.example.notes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.model.List;
import com.example.notes.R;
import com.example.notes.adapter.TaskAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class TasksActivity extends AppCompatActivity {

    private RecyclerView rv_tasks;
    private LinearLayoutManager linearLayoutManager;
    private TaskAdapter adapter ;
    private ArrayList<com.example.notes.model.Task> tasks = new ArrayList<>();
    private FirebaseFirestore db;
    private String email;
    private SharedPreferences sharedPreferences;
private List list ;
    Dialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("user info", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        email = sharedPreferences.getString("email", null);
        Button btnAddList = findViewById(R.id.btn_add_list);
        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog(TasksActivity.this);
            }
        });



        list = (List) getIntent().getSerializableExtra("list");



        findViews();
        getTasks();
    }


    private void findViews() {
        rv_tasks = findViewById(R.id.rv_tasks);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_tasks.setLayoutManager(linearLayoutManager);
        TextView listName = findViewById(R.id.tv_list);
        listName.setText(list.getName());
    }


    private void getTasks() {
        db.collection("Users").document(email).collection("Lists").document(list.getId()).collection("tasks")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    tasks.clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String title = (String) documentSnapshot.getData().get("title");
                        String description = (String) documentSnapshot.getData().get("description");
                        boolean isCompleted = (boolean) documentSnapshot.getData().get("isCompleted");
                        String id = (String) documentSnapshot.getId();
                        com.example.notes.model.Task taskT = new com.example.notes.model.Task(title, description , isCompleted);
                        taskT.setId(id);
                      tasks.add(taskT);
                       adapter = new TaskAdapter(TasksActivity.this, tasks,email,list);
                        rv_tasks.setAdapter(adapter);
                    }
                }
            }
        });
    }


    public void showCustomDialog(final Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.create_task_dialog);
        EditText taskName = dialog.findViewById(R.id.et_task_title);
        EditText taskDescription = dialog.findViewById(R.id.et_task_description);
        Button btnAddList = dialog.findViewById(R.id.btn_add);
        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!taskName.getText().toString().isEmpty() && !taskDescription.getText().toString().isEmpty()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("title" , taskName.getText().toString());
                    hashMap.put("description" , taskDescription.getText().toString());
                    hashMap.put("isCompleted" ,false);

                    db.collection("Users").document(email).collection("Lists").document(list.getId()).collection("tasks").document(taskName.getText().toString()).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(TasksActivity.this, "from complete", Toast.LENGTH_SHORT).show();
                            if (task.isSuccessful()) {
                                Toast.makeText(TasksActivity.this, "Successfully Add note", Toast.LENGTH_SHORT).show();
                                getTasks();

                            }

                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TasksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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