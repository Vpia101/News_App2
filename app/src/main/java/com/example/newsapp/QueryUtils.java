package com.example.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link NewsItem} objects.
     */
    public static List<NewsItem> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Return the list of {@link NewsItem}s
        return extractFeaturesFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String strUrl) {
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null)
            return jsonResponse;

        HttpURLConnection httpUrlConnection = null;
        InputStream inputStream = null;
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setReadTimeout(10000 /* milliseconds */);
            httpUrlConnection.setConnectTimeout(15000 /* milliseconds */);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (httpUrlConnection.getResponseCode() == 200) {
                inputStream = httpUrlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + httpUrlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (httpUrlConnection != null)
                httpUrlConnection.disconnect();
            if (inputStream != null)
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<NewsItem> extractFeaturesFromJson(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (jsonResponse == null || jsonResponse.length() == 0)
            return null;

        // Create an empty ArrayList that we can start adding NewsItem to
        ArrayList<NewsItem> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseResponse = new JSONObject(jsonResponse);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONObject responseObj = baseResponse.getJSONObject("response");
            JSONArray resultsArray = responseObj.getJSONArray("results");

            if (resultsArray.length() > 0) {
                for (int i = 0; i < resultsArray.length(); ++i) {
                    JSONObject obj = resultsArray.getJSONObject(i);

                    String authorName = null;
                    try {
                        authorName = obj.getJSONArray("tags").
                                getJSONObject(0).
                                getString("webTitle");
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Problem getting author name of article", e);
                    }

                    newsList.add(new NewsItem(obj.getString("webTitle"),
                            obj.getString("sectionName"),
                            authorName != null ? authorName : "Not available",
                            obj.getString("webPublicationDate"),
                            obj.getString("webUrl")));
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }

        // Return the list of news.
        return newsList;
    }
}
