package com.example.appscandiycrafts;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class Project {
    private String tittle;
    private String image;
    private String mainItem;
    private String documentId;
    private DocumentSnapshot documentSnapshot;
    private String instruction;

    private List<String> searchKeyword;

    public Project() {}

    public Project(String projectTittle, String image, String mainItem) {
        this.tittle = projectTittle;
        this.image = image;
        this.mainItem = mainItem;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMainItem() {
        return mainItem;
    }

    public void setMainItem(String mainItem) {
        this.mainItem = mainItem;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public DocumentSnapshot getDocumentSnapshot() {
        return documentSnapshot;
    }

    public void setDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
    }

    public List<String> getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(List<String> searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}

