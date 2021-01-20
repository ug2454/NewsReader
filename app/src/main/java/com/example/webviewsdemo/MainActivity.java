package com.example.webviewsdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ArrayList<Integer> newsId = new ArrayList<>();
    ArrayList<String> newsTitle = new ArrayList<>();
    ArrayList<String> newsUrl = new ArrayList<>();
    String title = "";
    String url = "";
    ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);


        HackerNews hackerNews = new HackerNews();

        try {
            hackerNews.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
//            for(int i=0;i<newsId.size();i++) {
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, newsTitle);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
            intent.putExtra("url", newsUrl.get(i));

            startActivity(intent);
        });

    }

    class HackerNews extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress  = new ProgressBar(MainActivity.this);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                String result = "";
                while (data != -1) {
                    char current = (char) data;

                    result += current;
                    data = inputStreamReader.read();

                }
                JSONArray newsId = new JSONArray(result);
                int numberOfItems = 20;
                if (numberOfItems < 20) {
                    numberOfItems = newsId.length();
                }
                for (int i = 0; i < numberOfItems; i++) {
                    String articleId = newsId.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json?print=pretty");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    inputStream = httpURLConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);
                    data = inputStreamReader.read();
                    String articleInfo = "";
                    while (data != -1) {
                        char current = (char) data;

                        articleInfo += current;
                        data = inputStreamReader.read();


                    }

//                    System.out.println(articleInfo);
                    JSONObject jsonObject = new JSONObject(articleInfo);
                    System.out.println(jsonObject.getString("title"));
                    System.out.println(jsonObject.getString("id"));

                    JsonObject asJsonObject = JsonParser.parseString(articleInfo).getAsJsonObject();
                    if (asJsonObject.has("url")) {
                        newsUrl.add(jsonObject.getString("url"));
                    } else {
                        Toast.makeText(MainActivity.this, "URL not available for this Story", Toast.LENGTH_SHORT).show();
                        newsUrl.add("");
                    }

                    if (asJsonObject.has("title")) {
                        newsTitle.add(jsonObject.getString("title"));
                    } else {
                        Toast.makeText(MainActivity.this, "Title not available for this Story", Toast.LENGTH_SHORT).show();
                        newsTitle.add("");
                    }


                }
                System.out.println(newsTitle.toString());
                System.out.println(newsUrl.toString());
                return result;
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.setVisibility(View.INVISIBLE);
        }
    }


}