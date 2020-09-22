package com.lstm.corona.client;

import java.util.List;

import com.lstm.corona.model.Country;
import com.lstm.corona.model.Day;
import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;

public interface ApiClient {
	
	ApiClient apiClient = Feign.builder().client(new OkHttpClient()).encoder(new GsonEncoder()).decoder(new GsonDecoder()).target(ApiClient.class, "https://api.covid19api.com");

	@RequestLine("GET /dayone/country/{country}")
	@Headers("Content-Type: application/json")
	List<Day> getCountryAllDay(@Param("country") String country);

	@RequestLine("GET /countries")
	@Headers("Content-Type: application/json")
	List<Country> getCountry();

}
