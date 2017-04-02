package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticlesAdapter;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.Filters;
import com.codepath.nytimessearch.network.NYTimesApiInterface;
import com.codepath.nytimessearch.network.models.NYTimesApiResponse;
import com.codepath.nytimessearch.network.models.NYTimesSearchResponse;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    final int SETTINGS_REQUEST_CODE = 1;
    final boolean USE_RETROFIT = true;
    public static final String BASE_URL = "https://api.nytimes.com/";
    final static String API_KEY = "476d52719f654648b8622ef5830a4568";
    SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyyMMdd");
    NYTimesApiInterface apiService;

    RecyclerView rvResults;

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
        setUpApi();
        setupViews();
    }

    public void setUpApi() {
        if (USE_RETROFIT) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(NYTimesApiInterface.class);
        }
    }

    public void setupViews() {
        articles = new ArrayList<>();
        filters = Filters.createNewFilters();

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query;

                int searchEditId = android.support.v7.appcompat.R.id.search_src_text;

                EditText etQuery = (EditText) searchView.findViewById(searchEditId);

                String q = etQuery.getText().toString();

                if (q != query) {
                    resetApiQueryParameters(q);
                    loadDataFromApi(0);
                }

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchItem.expandActionView();
        searchView.requestFocus();

        return super.onCreateOptionsMenu(menu);
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

    private void resetApiQueryParameters(String q) {
        query = q;

        int numberOfItems = adapter.getItemCount();
        if (numberOfItems > 0) {
            articles.clear();
            adapter.notifyItemRangeRemoved(0, numberOfItems);
            scrollListener.resetState();
        }
    }

    private void loadDataFromApiWithRetrofit(int page) {
        String sort = getSortFilterParam();
        String beginDate = getBeginDateParam();
        String fq = getFacetQueryParam();
        String q = getQueryParam();

        Call<NYTimesApiResponse> call = apiService.searchArticles(API_KEY,
                q,
                page,
                sort,
                beginDate,
                fq);

        call.enqueue(new Callback<NYTimesApiResponse>() {
            @Override
            public void onResponse(Call<NYTimesApiResponse> call, Response<NYTimesApiResponse> response) {
                NYTimesApiResponse body = response.body();
                handleNYTimesApiResponse(body);
            }

            @Override
            public void onFailure(Call<NYTimesApiResponse> call, Throwable t) {
                NYTimesSearchResponse body = null;
            }
        });
    }

    private void handleNYTimesApiResponse(NYTimesApiResponse apiResponse) {
        NYTimesSearchResponse response = apiResponse.getResponse();
        setArticlesFromApi(response.getDocs());
    }

    private void setArticlesFromApi(ArrayList<Article> results) {
        if (results.size() == 0) {
            // TODO handle no results
            Snackbar.make(rvResults, R.string.snackbar_no_results, Snackbar.LENGTH_LONG).show();
            return;
        }
        // for the recycler view
        int currentIndex = adapter.getItemCount();
        articles.addAll(results);
        adapter.notifyItemRangeInserted(currentIndex, results.size());
        Log.d("DEBUG", articles.toString());
    }

    private String getQueryParam() {
        if (!TextUtils.isEmpty(query)) {
            return query;
        }
        return null;
    }

    private String getSortFilterParam() {
        if (!TextUtils.isEmpty(filters.getSortOrder())) {
            return filters.getSortOrder().toLowerCase();
        }
        return null;
    }

    private String getBeginDateParam() {
        if (filters.getBeginDate() != null) {
            return apiDateFormat.format(filters.getBeginDate().getTime());
        }
        return null;
    }

    private String getFacetQueryParam() {
        if (filters.getCategories().size() > 0) {
            String fq = "news_desk:(";

            for (int i = 0; i < filters.getCategories().size(); i++) {
                fq += String.format("\"%s\" ", filters.getCategories().get(i));
            }

            fq += ")";
            return fq;
        }
        return null;
    }

    private void loadDataFromApi(int page) {
        if (USE_RETROFIT) {
            loadDataFromApiWithRetrofit(page);
        } else {
            loadDataFromApiWithAsync(page);
        }
    }

    private void loadDataFromApiWithAsync(int page) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + "svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", page);
        params.put("q", getQueryParam());
        params.put("sort", getSortFilterParam());
        params.put("fq", getFacetQueryParam());
        params.put("begin_date", getBeginDateParam());

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");

                    if (articleJSONResults.length() == 0) {
                        Snackbar.make(rvResults, R.string.snackbar_no_results, Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    // for the recycler view
                    ArrayList<Article> arr = Article.fromJsonArray(articleJSONResults);
                    setArticlesFromApi(arr);
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
