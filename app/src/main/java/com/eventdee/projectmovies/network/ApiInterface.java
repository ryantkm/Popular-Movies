package com.eventdee.projectmovies.network;

import com.eventdee.projectmovies.object.Movie;
import com.eventdee.projectmovies.object.MoviesResponse;
import com.eventdee.projectmovies.object.ReviewsResponse;
import com.eventdee.projectmovies.object.VideosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<MoviesResponse> getUpcomingMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<Movie> getMovieDetails(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<VideosResponse> getVideos(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewsResponse> getReviews(@Path("id") String id, @Query("api_key") String apiKey);
}