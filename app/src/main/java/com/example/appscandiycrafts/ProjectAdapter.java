package com.example.appscandiycrafts;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends FirestoreRecyclerAdapter<Project, ProjectAdapter.ProjectViewHolder> {

    private List<Project> originalList;
    private List<Project> filteredList;
    private String searchText = "";
    public ProjectAdapter(@NonNull FirestoreRecyclerOptions<Project> options) {

        super(options);
        originalList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    @Override
    protected void onBindViewHolder(@NonNull ProjectAdapter.ProjectViewHolder holder, int position, @NonNull Project model) {
        // bind project data to view holder
        Picasso.get().load(model.getImage()).into(holder.projectImage);
        holder.labelMainItem.setText(model.getMainItem());

        String title = model.getTittle();
        holder.labelTittle.setText(title);
        holder.instruction = model.getInstruction();
        // Retrieve the document ID
        String documentId = getSnapshots().getSnapshot(position).getId();

        // Set the document ID in the Project object
        model.setDocumentId(documentId);
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_project, parent, false);
        return new ProjectViewHolder(view);
    }


    class ProjectViewHolder extends RecyclerView.ViewHolder{

        ImageView projectImage;
        TextView labelTittle, labelMainItem;
        String instruction;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            projectImage = itemView.findViewById(R.id.imageViewProject);
            labelTittle = itemView.findViewById(R.id.textViewTitle);
            labelMainItem = itemView.findViewById(R.id.textViewItem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // get the project at the clicked position
                        Project project = getItem(position);
                        // get the instruction URL from the project
                        instruction = project.getInstruction();
                        //get the project document id
                        String documentId = project.getDocumentId();
                        // start the ProjectActivity with the instruction URL
                        Intent intent = new Intent(itemView.getContext(), ProjectActivity.class);
                        intent.putExtra("instructionUrl", instruction);
                        intent.putExtra("documentId", documentId);
                        itemView.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

}
