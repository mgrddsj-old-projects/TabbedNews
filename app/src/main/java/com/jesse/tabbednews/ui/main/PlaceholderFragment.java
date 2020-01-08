package com.jesse.tabbednews.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jesse.tabbednews.Article;
import com.jesse.tabbednews.NewsAdapter;
import com.jesse.tabbednews.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment
{

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private View root;

    private Context context;
    private RequestQueue requestQueue;
    static final private String REQUEST_NEWS = "News";
    public ArrayList<Article> articleArrayList;
    private RecyclerView recyclerView;
    private NewsAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> list;
    private SwipeRefreshLayout swipeContainer;
    private String searchQueryRealtime;


    public static PlaceholderFragment newInstance(int index)
    {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null)
        {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        root = inflater.inflate(R.layout.fragment_main, container, false);
        context = getContext();

        articleArrayList = new ArrayList<>();

        searchQueryRealtime = "";

        setupPullToRefresh();
        fetchNews(pageViewModel.getNewsURL());
//        final TextView textView = root.findViewById(R.id.section_label);
//        pageViewModel.getText().observe(this, new Observer<String>()
//        {
//            @Override
//            public void onChanged(@Nullable String s)
//            {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void fetchNews(String url)
    {
        requestQueue = Volley.newRequestQueue(context);
//        String url = "https://newsapi.org/v2/top-headlines?country=nz&apiKey=7f32fd5b23e947abafa4b92c55b42898";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Toast.makeText(context, "Got latest news! ", Toast.LENGTH_SHORT).show();
                            JSONArray responseArray = response.getJSONArray("articles");
                            articleArrayList.clear();
                            for (int i=0; i<responseArray.length(); i++)
                            {
                                JSONObject jsonObject = responseArray.getJSONObject(i);
                                String title = jsonObject.getString("title");
                                String author = jsonObject.getString("author");
                                String date = jsonObject.getString("publishedAt").substring(0, 10);
                                String description = jsonObject.getString("description");
                                String photoURL = jsonObject.getString("urlToImage");
                                String articleURL = jsonObject.getString("url");
                                articleArrayList.add(new Article(title, author, date, description, photoURL, articleURL));
                            }
                            showRecyclerView();
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(context, "JSON Exception! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(context, "Volley Exception! ", Toast.LENGTH_SHORT).show();
                        swipeContainer.setRefreshing(false);
                    }
                });
        jsonObjectRequest.setTag(REQUEST_NEWS); // TODO Cancel request on exit.
        requestQueue.add(jsonObjectRequest);
    }

    public void showRecyclerView()
    {
        recyclerView = root.findViewById(R.id.recycler);
        mAdapter = new NewsAdapter((List<Article>)articleArrayList);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);
        list = articleArrayList;

        swipeContainer.setRefreshing(false);
    }

    private void setupPullToRefresh()
    {
        swipeContainer = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (searchQueryRealtime.length() != 0)
                {
//                    searchNews(searchQueryRealtime);
                }
                else
                {
                    fetchNews(pageViewModel.getNewsURL());
                }
            }
        });
        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
        swipeContainer.setColorSchemeColors(getResources().getColor(android.R.color.black));
    }
}