package com.example.todolisttut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mrecyclerView;
    private FloatingActionButton mfab;

    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;
    //to inform the users know what is going on at each particular moment

    //this field is used for update
    private String key ="";
    private String task;
    private String description;

    RadioButton urgent,noturgent;


    int i = 0; //assume its the id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initialize these variables
        mToolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Todo List Home");

        mrecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(HomeActivity.this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        mReference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);



        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    i = (int)snapshot.getChildrenCount();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //

            }
        });

                mfab = findViewById(R.id.fab);
        //we want create another (we will open the input_file.xml)
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });



    }

    private void addTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_file,null);
        myDialog.setView(myView);

        //we now to create the alert dialog
        final AlertDialog dialog = myDialog.create();
        //by touching outside does not make it disappear
        //dialog.show();
        dialog.setCancelable(false);

        final EditText task = myView.findViewById(R.id.etTask);
        final EditText description = myView.findViewById(R.id.etDescription);
        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTask = task.getText().toString().trim();
                String mDescription = description.getText().toString().trim();
                String id = mReference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if (TextUtils.isEmpty(mTask)){
                    task.setError("Task required!");
                }

                if (TextUtils.isEmpty(mDescription)){
                    description.setError("Description required");
                }else{
                    loader.setMessage("Adding your task");
                    loader.setCanceledOnTouchOutside(true);
                    loader.show();

                    final Model model = new Model(mTask,mDescription,id,date);
                    mReference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(HomeActivity.this,"Task has been inserted successfully",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String error = task.getException().toString();
                                Toast.makeText(HomeActivity.this,"Sorry, task has been inserted unsuccessfully" + error,Toast.LENGTH_SHORT).show();

                            }


                        }
                    });

                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(mReference,Model.class)
                .build();

        FirebaseRecyclerAdapter<Model,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDescription(model.getDescription());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //we want to fetch that particular data to that particular task
                        //and place it inside this so users can update it
                        key = getRef(position).getKey();
                        task = model.getTask();
                        description=model.getDescription();

                        updateTask();

                    }
                });


            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from (parent.getContext()).inflate(R.layout.retrieval_layout,parent,false);
                return new MyViewHolder(view);
            }
        };

        //set the adapter to the recycler view
        mrecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //mView equals item view
            mView = itemView;
            //be able to use the text views we created in the retrieval layout
        }

        public void setTask(String task){
            TextView taskTextView = mView.findViewById(R.id.taskTv);
            taskTextView.setText(task);
        }

        public void setDescription(String desc){
            TextView descTextView = mView.findViewById(R.id.descriptionTv);
            descTextView.setText(desc);
        }

        public void setDate (String date){
            TextView dateTextView = mView.findViewById(R.id.dateTv);
            dateTextView.setText(date);
        }
    }

    private void updateTask() {
        //now we want to create the alert dialog
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_data,null);
        myDialog.setView(view);

        final AlertDialog dialog = myDialog.create();

        final EditText mtask = view.findViewById(R.id.updateTask);
        final EditText mdesc = view.findViewById(R.id.updateDesc);
        //set the text to the text that was already in dialog
        mtask.setText(task);
        mtask.setSelection(task.length());

        //set the description too
        mdesc.setText(description);
        mdesc.setSelection(description.length());

        Button mbtnDel = view.findViewById(R.id.btnDel);
        Button mbtnUpdate = view.findViewById(R.id.btnUpdate);

        mbtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we need to perform codes for update button
                task = mtask.getText().toString().trim();
                description = mdesc.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());
                //insert that data to the database

                //an object of Model class
                Model model = new Model(task,description,key,date);

                mReference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this,"Task has been updated successfully",Toast.LENGTH_SHORT).show();
                        }else{
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity.this,"Task has been updated unsuccessfully" + error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        mbtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //function of deleting the button
                mReference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this,"Task has been deleted successfully",Toast.LENGTH_SHORT).show();
                        }else{
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity.this,"Task has been deleted unsuccessfully" + error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout :
                mAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}