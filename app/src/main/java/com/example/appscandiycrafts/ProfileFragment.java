package com.example.appscandiycrafts;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ImageView profileImage;
    private ProjectAdapter projectAdapter;
    private RecyclerView recyclerView;
    private TextView favouriteTextView;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewFavourite);
        favouriteTextView = rootView.findViewById(R.id.favouriteTextView);



        Button logoutButton = rootView.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout action here
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "You are log out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView sexTextView = view.findViewById(R.id.sexTextView);
        TextView birthdayTextView = view.findViewById(R.id.birthdayTextView);
        profileImage = view.findViewById(R.id.profile_image);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            //Toast.makeText(getActivity(), userId+" "+currentUser, Toast.LENGTH_SHORT).show();
            DocumentReference docRef = db.collection("users").document(userId);

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the user's information and update profile
                        String name = document.getString("name");
                        String sex = document.getString("sex");
                        String birthday = document.getString("birthday");
                        String profileImageUrl = document.getString("profileImageUrl");
                        nameTextView.setText(name);
                        sexTextView.setText(sex);
                        birthdayTextView.setText(birthday);
                        if (profileImageUrl != null) {
                            // Set the profile image
                            Glide.with(this).load(profileImageUrl).into(profileImage);
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });


            //setupRecyclerView();

            setupRecyclerView(options -> {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        projectAdapter = new ProjectAdapter(options);
                        recyclerView.setAdapter(projectAdapter);

                        // Start listening after the adapter is initialized
                        projectAdapter.startListening();
            });



        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch gallery intent
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of the selected image
            Uri imageUri = data.getData();

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userId = currentUser.getUid();

            // Upload the image to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            //StorageReference profileImagesRef = storageRef.child("profileImages/" + UUID.randomUUID().toString());
            StorageReference profileImagesRef = storageRef.child("profileImages/" + userId);
            profileImagesRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        Toast.makeText(getActivity(), "Profile image uploaded", Toast.LENGTH_SHORT).show();

                        // Get the uploaded image URL
                        profileImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update the user's profileImageUrl in Firestore
                            //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                //String userId = currentUser.getUid();
                                DocumentReference docRef = db.collection("users").document(userId);
                                docRef.update("profileImageUrl", uri.toString())
                                        .addOnSuccessListener(aVoid -> {
                                            // Profile image URL updated successfully
                                            loadProfileImage();
                                            Log.d(TAG, "Profile image URL updated successfully");
                                        })
                                        .addOnFailureListener(e -> {
                                            // Failed to update profile image URL
                                            Log.d(TAG, "Failed to update profile image URL", e);
                                        });
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Image upload failed
                        Toast.makeText(getActivity(), "Profile image upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadProfileImage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference profileImagesRef = storageRef.child("profileImages/" + userId);

            profileImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Image loaded successfully, set it to the ImageView
                Glide.with(this)
                        .load(uri)
                        .into(profileImage);
            }).addOnFailureListener(e -> {
                // Failed to load the image
                Toast.makeText(getActivity(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupRecyclerView(OnQuerySuccessCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            CollectionReference favoriteRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("favourite");

            favoriteRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> favoriteProjectIds = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String favoriteProjectId = documentSnapshot.getString("FavouriteProject");
                            if (favoriteProjectId != null) {
                                favoriteProjectIds.add(favoriteProjectId);
                            }
                        }

                        FirestoreRecyclerOptions<Project> options;

                        if (favoriteProjectIds.isEmpty()) {
                            // Handle case when the user has no favorite projects
                            // You can create an empty query or set options to null, depending on your requirements
                            //Query query = FirebaseFirestore.getInstance().collection("projects").limit(0);
                            recyclerView.setVisibility(View.GONE);
                            favouriteTextView.setVisibility(View.VISIBLE);
                            options = null;
                        } else {
                            Query query = FirebaseFirestore.getInstance()
                                    .collection("projects")
                                    .whereIn(FieldPath.documentId(), favoriteProjectIds);

                            options = new FirestoreRecyclerOptions.Builder<Project>()
                                    .setQuery(query, Project.class)
                                    .build();

                            recyclerView.setVisibility(View.VISIBLE);
                            favouriteTextView.setVisibility(View.GONE);
                        }

                        if (options != null) {
                            // Initialize the ProjectAdapter
                            ProjectAdapter adapter = new ProjectAdapter(options);
                            // Set the adapter to the RecyclerView
                            recyclerView.setAdapter(adapter);
                            callback.onQuerySuccess(options);
                        }



                        // Invoke the callback with the options
                        //callback.onQuerySuccess(options);

                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to retrieve favorite projects
                        Log.d(TAG, "Error getting favorite projects: ", e);
                    });
        }
    }




    @Override
    public void onStart() {
        super.onStart();
        //projectAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (projectAdapter != null) {
            projectAdapter.stopListening();
        }
    }


}