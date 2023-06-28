package com.example.appscandiycrafts;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.example.appscandiycrafts.Project;

public interface OnQuerySuccessCallback {
    void onQuerySuccess(FirestoreRecyclerOptions<Project> options);
}
