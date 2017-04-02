package com.codepath.nytimessearch.network;

import com.codepath.nytimessearch.network.models.NYTimesApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by christine_nguyen on 4/1/17.
 */

public interface NYTimesApiInterface {
    @GET("svc/search/v2/articlesearch.json")
    Call<NYTimesApiResponse> searchArticles(@Query("api-key") String apiKey,
                                            @Query("q") String query,
                                            @Query("page") int page,
                                            @Query("sort") String sort,
                                            @Query("begin_date") String beginDate,
                                            @Query("fq") String fq);
}
