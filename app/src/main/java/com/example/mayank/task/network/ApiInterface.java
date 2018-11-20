package com.example.mayank.task.network;

import com.example.mayank.task.models.WorldPopulationModel;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("tutorial/jsonparsetutorial.txt")
    Observable<WorldPopulationModel> getCountryList();

}
