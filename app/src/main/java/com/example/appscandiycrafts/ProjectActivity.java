package com.example.appscandiycrafts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ProjectActivity extends AppCompatActivity {
    private WebView webView;
    private FloatingActionButton fab;
    private String documentName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        fab = findViewById(R.id.fab);

        // get the instruction URL from the intent
        Intent intent = getIntent();
        String instruction = intent.getStringExtra("instructionUrl");
        documentName = intent.getStringExtra("documentId");

        if(documentName!=null){
            Toast.makeText(ProjectActivity.this,"The doc id is "+ documentName, Toast.LENGTH_SHORT).show();
        }else Toast.makeText(ProjectActivity.this, "It's null damnit", Toast.LENGTH_SHORT).show();

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(instruction);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast message
                //Toast.makeText(ProjectActivity.this, "Favourite", Toast.LENGTH_SHORT).show();

                // Get the current user's uid or document reference
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference userRef = db.collection("users").document(uid);

                // Check if the project is already added to favorites
                userRef.collection("favourite")
                        .whereEqualTo("FavouriteProject", documentName)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    // The project is not yet added to favorites, proceed with adding it
                                    addProjectToFavorites(userRef);
                                } else {
                                    // The project is already added to favorites
                                    Toast.makeText(ProjectActivity.this, "The project is already added to favorites", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to check if the project is already added to favorites
                                Toast.makeText(ProjectActivity.this, "Failed to check if the project is already added to favorites", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



    }

    private void addProjectToFavorites(DocumentReference userRef) {
        // Create a new document under the "favourite" subcollection
        DocumentReference favouriteRef = userRef.collection("favourite").document();

        // Set the instructionUrl as a field in the new document
        favouriteRef.set(new HashMap<String, Object>() {{
                    put("FavouriteProject", documentName);
                }})
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document added to "favourite" subcollection successfully
                        Toast.makeText(ProjectActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add document to "favourite" subcollection
                        Toast.makeText(ProjectActivity.this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}