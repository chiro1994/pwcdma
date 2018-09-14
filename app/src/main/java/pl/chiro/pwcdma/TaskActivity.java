package pl.chiro.pwcdma;

/**
 * Created by Chiro on 14.09.2018.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static pl.chiro.pwcdma.MainActivity.parser;

public class TaskActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private RecyclerView listRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter firebaseAdapter;
    private String idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        idList = intent.getStringExtra("idList");
        setTitle(intent.getStringExtra("title"));

        listRecyclerView = (RecyclerView) findViewById(R.id.listRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        listRecyclerView.setLayoutManager(linearLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("lists").child(idList).child("tasks");
        FirebaseRecyclerOptions<ToDoList> options = new FirebaseRecyclerOptions
                .Builder<ToDoList>().setQuery(query, parser).build();
        firebaseAdapter = new FirebaseRecyclerAdapter<ToDoList, TaskActivity.ListViewHolder>(options) {

            @NonNull
            @Override
            public TaskActivity.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new TaskActivity.ListViewHolder(inflater.inflate(R.layout.card_task, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull TaskActivity.ListViewHolder holder,
                                            int position, @NonNull final ToDoList model) {
                holder.listCheckBox.setText(model.getListName());
                holder.listCheckBox.setChecked(model.isDone());
                holder.listCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        model.setDone(isChecked);
                        databaseReference.child("lists").child(idList).child("tasks")
                                .child(model.getId()).setValue(model);
                    }
                });
            }
        };

        listRecyclerView.setAdapter(firebaseAdapter);
        FloatingActionButton floatingButton = (FloatingActionButton) findViewById(R.id.listFloatingButton);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mView = LayoutInflater.from(TaskActivity.this).inflate(R.layout.new_list_dialog, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TaskActivity.this);
                alertDialog.setView(mView);

                final EditText textDialog = (EditText) mView.findViewById(R.id.nameListDialog);
                TextView textView = (TextView) mView.findViewById(R.id.dialogTitle);
                textView.setText(R.string.new_task);
                textDialog.setHint(R.string.task_name);
                alertDialog.setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String nameList = textDialog.getText().toString().trim();
                                if (nameList.length() > 0) {
                                    ToDoList toDoList = new ToDoList(nameList, false);
                                    databaseReference.child("lists").child(idList).child("tasks").push().setValue(toDoList);
                                } else {
                                    Toast.makeText(TaskActivity.this, "Zadanie musi posiadać nazwę!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })

                        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

                alertDialog.create().show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAdapter.startListening();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        CheckBox listCheckBox;

        public ListViewHolder(View v) {
            super(v);
            listCheckBox = (CheckBox) itemView.findViewById(R.id.checkTask);
        }
    }
}