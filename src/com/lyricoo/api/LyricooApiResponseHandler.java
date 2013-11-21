package com.lyricoo.api;

public class LyricooApiResponseHandler {

	/**
	 * Called when the request has been returned and successfully parsed
	 * 
	 * @param responseJson
	 *            Either a JSONObject or JSONArray based on the response. The
	 *            client must cast it accordingly.
	 */
	public void onSuccess(Object responseJson) {

	}

	/**
	 * Called when either the request fails or the response is unable to be
	 * parsed to json
	 * 
	 * @param statusCode
	 * @param responseBody
	 * @param error
	 */
	public void onFailure(int statusCode, String responseBody, Throwable error) {

	}

	public void onStart() {

	}

	public void onFinish() {

	}

	public void onProgress(int bytesWritten, int totalSize) {

	}

	public void onRetry() {

	}

}
