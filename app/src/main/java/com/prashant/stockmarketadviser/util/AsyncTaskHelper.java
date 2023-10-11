package com.prashant.stockmarketadviser.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncTaskHelper {

    public interface AsyncTaskCallback<T> {
        void onPreExecute();

        void onPostExecute(T result);

        void onError(Exception e);
    }

    // Example of performing an HTTP GET request
    public static void performHttpGetRequest(String url, AsyncTaskCallback<String> callback) {
        new HttpGetRequestTask(callback).execute(url);
    }

    private static class HttpGetRequestTask extends AsyncTask<String, Void, String> {
        private AsyncTaskCallback<String> callback;

        HttpGetRequestTask(AsyncTaskCallback<String> callback) {
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            if (callback != null) {
                callback.onPreExecute();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                String url = urls[0];
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError(e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                callback.onPostExecute(result);
            }
        }
    }

    // Example of running a task on the background thread
    public static void runInBackground(Runnable runnable) {
        new BackgroundTask(runnable).execute();
    }

    private static class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private Runnable runnable;

        BackgroundTask(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (runnable != null) {
                runnable.run();
            }
            return null;
        }
    }

    // Example of running a task on the main UI thread
    public static void runOnUiThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }
}
