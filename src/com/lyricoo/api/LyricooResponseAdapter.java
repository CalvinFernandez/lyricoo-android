package com.lyricoo.api;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Use LyricooResponseAdapter to convert reliably from AsyncHttpResponse to
 * LyricooApiResponseHandler.
 * 
 * Usage LyricooResponseAdapter.adapt(LyricooApiResponseHandler);
 * 
 * @author
 * 
 */
public class LyricooResponseAdapter {

	/**
	 * Adapter that converts http responses to json responses
	 * 
	 * @param JsonHttpResponseHandler
	 *            handles json responses
	 * @return AsyncHttpResponseHandler
	 */
	public static AsyncHttpResponseHandler adapt(
			final LyricooApiResponseHandler responseHandler) {

		return new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				try {
					Object json = LyricooResponseAdapter.toJson(responseBody);
					
					// json should be either a JSONObject or JSONArray
					if ((json instanceof JSONObject)
							|| (json instanceof JSONArray)) {
						responseHandler.onSuccess(json);
					} else {
						// Something's unexpected about the response
						responseHandler.onFailure(statusCode,
								decodeByteArray(responseBody), null);
					}

				} catch (Throwable e) {
					responseHandler.onFailure(statusCode,
							decodeByteArray(responseBody), e);
				}
			}

			@Override
			public void onFailure(int statusCode,
					org.apache.http.Header[] headers, byte[] responseBody,
					java.lang.Throwable error) {

				responseHandler.onFailure(statusCode,
						decodeByteArray(responseBody), error);
			}

			@Override
			public void onStart() {
				responseHandler.onStart();
			}

			@Override
			public void onFinish() {
				responseHandler.onFinish();
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				responseHandler.onProgress(bytesWritten, totalSize);
			}

			@Override
			public void onRetry() {
				responseHandler.onRetry();
			}

		};
	}

	/**
	 * Converts byte array to object form
	 * 
	 * @param in
	 *            The byte[]
	 * @return Object The decoded json *
	 * @throws Throwable
	 *             Either a NullPointerException or JSONException,
	 */
	private static Object toJson(byte[] in) throws Throwable {
		String decoded = decodeByteArray(in);
		Object json = new JSONTokener(decoded).nextValue();
		return json;
	}

	/**
	 * Decode a byte array into a String using UTF-8
	 * 
	 * @param arr
	 * @return The decoded string, or null on failure
	 */
	private static String decodeByteArray(byte[] arr) {
		try {
			return new String(arr, "UTF-8");
		} catch (Throwable e) {
			return null;
		}
	}
}
