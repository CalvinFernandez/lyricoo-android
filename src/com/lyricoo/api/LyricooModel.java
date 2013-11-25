package com.lyricoo.api;

import com.loopj.android.http.RequestParams;

/**
 * LyricooModel abstracts communication between a model 
 * and the server api in a RESTful way.
 * LyricooModel should be the base class for any model 
 * that needs to communicate in a RESTful way between
 * the phone and the server.
 * 
 * To use LyricooModel, create a new class 
 * that extends LyricooModel. You will need to define
 * a baseUrl for the model which defines the 
 * root url for communication between the model and the 
 * server. For example: 
 * 
 * public class User extends LyricooModel {
 *   
 * 	  public User() {
 *       super("users/4");
 *       .
 *       .
 *       .
 *    }
 * }
 * 
 * You will then be able to access the server using 
 * any instance of the User class, using user.(get | post | put | delete)
 * 
 */
public class LyricooModel {
	
	/*
	 * This is the base url for the route linking
	 * the model to the server. Example: "users/1"
	 * should be defined as the base url for a user
	 * whose id is 1. 
	 */
	private String baseUrl;
	
	public LyricooModel(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * You can call Lyricoomodel without a baseUrl but 
	 * you'll need to define it later if you want to
	 * access anything other that "/".
	 */
	public LyricooModel() {
		this("");
	}
	
	/**
	 * Updates the base url. Should only be set once
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/*
	 * Route: baseUrl
	 * Params: None 
	 */
	public void get(LyricooApiResponseHandler responseHandler) {
		LyricooApi.get(baseUrl, new RequestParams(), LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Route: baseUrl
	 * params: Custom.
	 */
	public void get(RequestParams params, LyricooApiResponseHandler responseHandler) {
		LyricooApi.get(baseUrl, params, LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Route: baseUrl + "/" + url
	 * Params: Custom 
	 */
	public void get(String url, RequestParams params, LyricooApiResponseHandler responseHandler) {
		LyricooApi.get(getAbsoluteUrl(url), params, LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Route: baseUrl + "/" + url
	 * params: None
	 */
	public void get(String url, LyricooApiResponseHandler responseHandler) {
		LyricooApi.get(getAbsoluteUrl(url), new RequestParams(), LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Route: baseUrl
	 * params: Custom.
	 */
	public void post(RequestParams params, LyricooApiResponseHandler responseHandler) {
		LyricooApi.post(baseUrl, params, LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Route: baseUrl + "/" + url
	 * params: Custom.
	 */
	public void post(String url, RequestParams params, LyricooApiResponseHandler responseHandler) {
		LyricooApi.post(getAbsoluteUrl(url), params, LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Route: baseUrl
	 * params: Custom.
	 */
	public void put(RequestParams params, LyricooApiResponseHandler responseHandler) {
		LyricooApi.put(baseUrl, params, LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Route: baseUrl + "/" + url
	 * params: Custom.
	 */
	public void put(String url, RequestParams params, LyricooApiResponseHandler responseHandler) {
		LyricooApi.put(getAbsoluteUrl(url), params, LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Route: baseUrl
	 * params: None
	 */
	public void delete(LyricooApiResponseHandler responseHandler) {
		LyricooApi.delete(baseUrl, LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Route: baseUrl + "/" + url
	 * params: None
	 */
	public void delete(String url, LyricooApiResponseHandler responseHandler) {
		LyricooApi.delete(getAbsoluteUrl(url), LyricooResponseAdapter.adapt(responseHandler));
	}
	
	/*
	 * Builds a url
	 * Returns baseUrl + "/" + relativeUrl
	 */
	private String getAbsoluteUrl(String relativeUrl) {
		return baseUrl + "/" + relativeUrl;
	}
	
}
