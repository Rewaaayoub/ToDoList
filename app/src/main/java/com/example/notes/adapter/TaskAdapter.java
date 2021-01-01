 package com.example.notes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.R;
import com.example.notes.activities.TaskDetailsActivity;
import com.example.notes.activities.TasksActivity;
import com.example.notes.model.List;
import com.example.notes.model.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

 public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context ;
    private ArrayList<Task> tasks ;
    private String email, originTitle;
    private List list ;
    FirebaseFirestore db;

    public TaskAdapter(Context context, ArrayList<Task> tasks , String email , List list) {
        this.context = context;
        this.tasks = tasks;
        this.email = email;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_task_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        originTitle = tasks.get(position).getTitle();
        holder.title.setText(tasks.get(position).getTitle());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskDetailsActivity.class);
                intent.putExtra("task" , tasks.get(position));
                intent.putExtra("list" , list);
                intent.putExtra("email" , email);
                context.startActivity(intent);
            }
        });

        if (tasks.get(position).isCompleted()) {
            holder.isCompleted.setBackground(context.getResources().getDrawable(R.drawable.ic_competed));
            holder.isCompleted.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_true) , null , null , null);
        }

        holder.isCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.isCompleted.getBackground() == context.getResources().getDrawable(R.drawable.ic_uncompete)){
                    holder.isCompleted.setBackground(context.getResources().getDrawable(R.drawable.ic_competed));
                    holder.isCompleted.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_true) , null , null , null);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("title" , tasks.get(position).getTitle());
                    hashMap.put("description" , tasks.get(position).getDescription());
                    hashMap.put("isCompleted" ,true);


                    db.collection("Users").document(email).collection("Lists").document(list.getId()).collection("tasks")
                            .document(tasks.get(position).getId())
                            .update(hashMap);

                }



                HashMap<String , Object> hashMapList =  new HashMap<>();
                hashMapList.put("name", list.getName());
                int number = list.getTaskNumber();
                hashMapList.put("taskNumber", number+1);

                db.collection("Users").document(email).collection("Lists").document(list.getId()).
                        update(hashMapList);

            }



        });






    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View view ;
        TextView title ;

        Button isCompleted ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            title=view.findViewById(R.id.tv_task_name);
            isCompleted=view.findViewById(R.id.btn_is_completed);

        }
    }






}
