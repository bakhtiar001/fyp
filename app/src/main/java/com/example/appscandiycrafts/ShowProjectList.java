package com.example.appscandiycrafts;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ShowProjectList extends AppCompatActivity {

    private String predictedLabel;
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProjectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_project_list);

        recyclerView = findViewById(R.id.recyclerView4Show);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the intent that started this activity
        Intent intent = getIntent();
        predictedLabel = intent.getStringExtra("predictedLabel");

        //TODO: need to create more website
        Query query = db.collection("projects").whereEqualTo("mainItem", predictedLabel);

        FirestoreRecyclerOptions<Project> options =
                new FirestoreRecyclerOptions.Builder<Project>()
                        .setQuery(query, Project.class)
                        .build();

        adapter = new ProjectAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}