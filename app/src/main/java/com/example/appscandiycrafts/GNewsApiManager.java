package com.example.appscandiycrafts;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GNewsApiManager {
    private static final String BASE_URL = "https://gnews.io/api/v4/search";
    private static final String API_KEY = "296353c1470c0cb1ba27d0e2445095c3";

    private Context context;
    private GNewsListener listener;

    public GNewsApiManager(Context context) {
        this.context = context;
    }

    public void setListener(GNewsListener listener) {
        this.listener = listener;
    }

    public void getEnvironmentNews() {
        String query = "pollution+climate+change";
        String url = BASE_URL + "?q=" + query + "&token=" + API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<Article> articles = parseArticlesFromResponse(response);
                        listener.onArticlesReceived(articles);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onArticlesError(error.getMessage());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private List<Article> parseArticlesFromResponse(JSONObject response) {
        List<Article> articles = new ArrayList<>();

        try {
            JSONArray articlesArray = response.getJSONArray("articles");
            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject articleObj = articlesArray.getJSONObject(i);
                Article article = parseArticle(articleObj);
                articles.add(article);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return articles;
    }

    private Article parseArticle(JSONObject articleObj) {
        Article article = new Article();

        try {
            article.setTitle(articleObj.getString("title"));
            article.setDescription(articleObj.getString("description"));
            article.setUrl(articleObj.getString("url"));
            article.setImageUrl(articleObj.getString("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return article;
    }
}
