package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticlesAdapter;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.Filters;
import com.codepath.nytimessearch.utils.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.utils.ItemClickSupport;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    final int SETTINGS_REQUEST_CODE = 1;
    SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyyMMdd");

    EditText etQuery;
    RecyclerView rvResults;
    Button btnSearch;

    ArrayList<Article> articles;
    ArticlesAdapter adapter;

    EndlessRecyclerViewScrollListener scrollListener;

    // Filters
    String query;
    Filters filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
    }

    public void setupViews() {
        articles = new ArrayList<>();
        filters = Filters.createNewFilters();

        etQuery = (EditText) findViewById(R.id.etQuery);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        rvResults = (RecyclerView) findViewById(R.id.rvResults);

        // Set up Recycler View
        adapter = new ArticlesAdapter(this, articles);
        rvResults.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(layoutManager);

        ItemClickSupport.addTo(rvResults).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                        Article article = articles.get(position);
                        i.putExtra("article", Parcels.wrap(article));
                        startActivity(i);
                    }
                }
        );

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadDataFromApi(page);
            }
        };

        rvResults.addOnScrollListener(scrollListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            // Pass current settings.
            i.putExtra("filters", Parcels.wrap(filters));
            startActivityForResult(i, SETTINGS_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SETTINGS_REQUEST_CODE) {
            filters = (Filters) Parcels.unwrap(data.getParcelableExtra("filters"));
            // Assume the filters have changes so reset the query params
            resetApiQueryParameters(query);
            loadDataFromApi(0);
        }
    }

    public void onArticleSearch(View view) {
        String q = etQuery.getText().toString();
        etQuery.clearFocus();
        if (q != query) {
            resetApiQueryParameters(q);
            loadDataFromApi(0);
        }
    }

    private void resetApiQueryParameters(String q) {
        query = q;

        int numberOfItems = adapter.getItemCount();
        if (numberOfItems > 0) {
            articles.clear();
            adapter.notifyItemRangeRemoved(0, numberOfItems);
            scrollListener.resetState();
        }
    }

    private void loadDataFromApi(int page) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "476d52719f654648b8622ef5830a4568");
        params.put("page", page);

        if (!TextUtils.isEmpty(query)) {
            params.put("q", query);
        }

        if (!TextUtils.isEmpty(filters.getSortOrder())) {
            params.put("sort", filters.getSortOrder().toLowerCase());
        }

        if (filters.getCategories().size() > 0) {
            String fq = "news_desk:(";

            for (int i = 0; i < filters.getCategories().size(); i++) {
                fq += String.format("\"%s\" ", filters.getCategories().get(i));
            }

            fq += ")";
            params.put("fq", fq);
        }
        if (filters.getBeginDate() != null) {
            params.put("begin_date", apiDateFormat.format(filters.getBeginDate().getTime()));
        }


        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");

                    if (articleJSONResults.length() == 0) {
                        // Todo handle no results;
                        Toast.makeText(getApplicationContext(),
                                "No results for query",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // for the recycler view
                    ArrayList<Article> arr = Article.fromJsonArray(articleJSONResults);
                    int currentIndex = adapter.getItemCount();
                    articles.addAll(arr);
                    adapter.notifyItemRangeInserted(currentIndex, arr.size());
                    Log.d("DEBUG", articles.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
