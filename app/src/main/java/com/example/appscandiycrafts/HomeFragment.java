package com.example.appscandiycrafts;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomeFragment extends Fragment implements GNewsListener{

    private FrameLayout homeContainer;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private ImageView cameraButton;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Get the button container and set a click listener on it
        homeContainer = view.findViewById(R.id.home_container);
        homeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click here
            }
        });

        newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsAdapter = new NewsAdapter(new ArrayList<Article>());
        newsRecyclerView.setAdapter(newsAdapter);

        // Get the cameraButton and set a click listener on it
        ImageView cameraButton = view.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast message
                Toast.makeText(getActivity(), "Item Scan", Toast.LENGTH_SHORT).show();
                // Start CaptureImageActivity
                Intent intent = new Intent(getActivity(), ImageClassificationActivity.class);
                startActivity(intent);
            }
        });

        // Fetch news articles
        fetchNews();

        return view;
    }

    private void fetchNews() {
        GNewsApiManager gNewsApiManager = new GNewsApiManager(getActivity());
        gNewsApiManager.setListener(this);
        gNewsApiManager.getEnvironmentNews();
    }


    @Override
    public void onArticlesReceived(List<Article> articles) {
        // Update the RecyclerView with the received articles
        newsAdapter.setArticles(articles);
    }

    @Override
    public void onArticlesError(String error) {
        // Handle error while fetching news
        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
    }
}