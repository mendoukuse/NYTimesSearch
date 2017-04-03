package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    final int SETTINGS_REQUEST_CODE = 1;
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
    int retryPage;

    Handler handler;

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
        handler = new Handler();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(NYTimesApiInterface.class);
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
        if (id == R.id.action_filters) {
            Intent i = new Intent(this, FiltersActivity.class);
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

    private void loadDataFromApi(int page) {
        String sort = getSortFilterParam();
        String beginDate = getBeginDateParam();
        String fq = getFacetQueryParam();
        String q = getQueryParam();
        retryPage = page;

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
                int responseCode = response.raw().code();

                if (responseCode == 200 && body != null) {
                    handleNYTimesApiResponse(body);
                } else if (responseCode == 429) {
                    // retry api call after timeout
                    handler.postDelayed(runnableRetryCode, 2000);
                } else {
                    handleApiFailure();
                }
            }

            @Override
            public void onFailure(Call<NYTimesApiResponse> call, Throwable t) {
                handleApiFailure();
            }
        });
    }

    private Runnable runnableRetryCode = new Runnable() {
        @Override
        public void run() {
            loadDataFromApi(retryPage);
            Log.d("Handlers", "Called on main thread");
            Log.d("DEBUG", String.format("Retrying api call for query: %s, page: %s", query, retryPage));
        }
    };

    private void handleApiFailure() {
        Snackbar.make(rvResults, R.string.snackbar_no_api_response, Snackbar.LENGTH_LONG).show();
    }

    private void handleNYTimesApiResponse(NYTimesApiResponse apiResponse) {
        if (apiResponse == null || apiResponse.getResponse() == null) {
            handleApiFailure();
            return;
        }
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

}
