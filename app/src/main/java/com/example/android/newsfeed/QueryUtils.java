package com.example.android.newsfeed;

import android.print.PrinterId;
import android.text.TextUtils;
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
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static int READ_TIMEOUT = 10000; /* milliseconds */
    private static int CONNECT_TIMEOUT = 15000; /* milliseconds */

    private QueryUtils() {
    }

    public static List<News> fetchNewsData(String requestUrl) {

        Log.i("TEST " + LOG_TAG, "entered fetchNewsData method");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<News> news = extractNews(jsonResponse);

        // Return the list of {@link Earthquake}s
        return news;
    }

    public static URL createUrl(String stringUrl) {
        Log.i("TEST " + LOG_TAG, "entered createUrl method");
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        Log.i("TEST " + LOG_TAG, "entered makeHttpRequest method");
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                Log.i("TEST " + LOG_TAG, "Response code 200");
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        Log.i("TEST " + LOG_TAG, "readFromStream started");
        Log.i("TEST " + LOG_TAG, inputStream.toString());
        if (inputStream != null) {
            Log.i("TEST " + LOG_TAG, "inputStream != null");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader, 28192); // override default buffer size because JSON might not fit in
            String line = reader.readLine();
            while (line != null) {
                Log.i("TEST " + LOG_TAG, line);
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<News> extractNews (String jsonResponse) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<News> news = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse).getJSONObject("response");
            JSONArray NewsArray = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < NewsArray.length(); i++) {
                JSONObject currentNews =NewsArray.getJSONObject(i);

                String sectionName = currentNews.getString("sectionName");
                String title = currentNews.getString("webTitle");
                String url = currentNews.getString("webUrl");
                String datePublished = currentNews.getString("webPublicationDate");
                String dateSplit = datePublished.split("T")[0];
                Log.i("TEST " + LOG_TAG, dateSplit);

                if(currentNews.has("tags")) {
                    JSONArray tags = currentNews.getJSONArray("tags");

                    if (tags.length() > 0) {
                        JSONObject authorJson = (JSONObject) tags.get(0);

                        if(authorJson.getString("type").equals("contributor")){
                            Log.i("TEST " + LOG_TAG,"currentNews object author data exist");
                            String author = authorJson.getString("webTitle");
                            Log.i("TEST " + LOG_TAG, author);
                            News newsObject = new News(sectionName, title, url, dateSplit, "by " + author);
                            news.add(newsObject);

                        } else {
                            Log.i("TEST " + LOG_TAG, "No author data");
                            News newsObject = new News(sectionName, title, url, dateSplit);
                            news.add(newsObject);
                        }

                    } else {
                        Log.i("TEST " + LOG_TAG, "tags array is empty");
                    }

                } else {
                    Log.i("TEST " + LOG_TAG, "No author data");
                    News newsObject = new News(sectionName, title, url, dateSplit);
                    news.add(newsObject);
                }
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("TEST " + LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        return news;
    }
}
