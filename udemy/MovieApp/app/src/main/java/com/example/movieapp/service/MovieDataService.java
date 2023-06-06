package com.example.movieapp.service;

import com.example.movieapp.model.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface MovieDataService {

    @GET("movie/popular")
    Call<Result> getPopularMovies(@Header("Authorization") String authorization);
}
