package com.example.appscandiycrafts;

import java.util.List;

public interface GNewsListener {
    void onArticlesReceived(List<Article> articles);
    void onArticlesError(String error);
}
