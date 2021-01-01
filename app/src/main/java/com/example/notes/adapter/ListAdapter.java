package com.example.notes.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.R;
import com.example.notes.activities.TasksActivity;
import com.example.notes.model.List;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context context ;
    private ArrayList<List> lists ;
    private String email, originTitle;
    FirebaseFirestore db;

    public ListAdapter(Context context, ArrayList<List> lists , String email) {
        this.context = context;
        this.lists = lists;
        this.email = email;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        originTitle = lists.get(position).getName();
        holder.name.setText(lists.get(position).getName());
        holder.taskNumber.setText(lists.get(position).getTaskNumber() + " tasks");
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TasksActivity.class);
                intent.putExtra("list" , lists.get(position));
                context.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View view ;
        TextView name , taskNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name=view.findViewById(R.id.tv_list_name);
            taskNumber = view.findViewById(R.id.tv_list_size );
        }
    }






}
