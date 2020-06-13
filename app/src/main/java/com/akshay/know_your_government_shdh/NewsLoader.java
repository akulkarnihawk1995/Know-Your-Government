package com.akshay.know_your_government_shdh;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsLoader extends AsyncTask<String, Void, ArrayList<Article>>
{
    NewsActivity newsActivity;
    private static final String TAG = "NewsLoader";

    public NewsLoader(NewsActivity newsActivity) {
        this.newsActivity = newsActivity;
    }

    @Override
    protected ArrayList<Article> doInBackground(String... strings)
    {
        ArrayList<Article> finalData;
        String token = strings[0];
        Log.d(TAG, "doInBackground: bp: Token: " + token);
        String URL = "http://newsapi.org/v2/everything?q="+token+"&apiKey=" + "5d6888d512254b2ca0d3f1cc56fcdfa9";
        String data = getOfficialDatafromURL(URL);
        finalData = parseJSON(data);
        return finalData;
    }



    private String getOfficialDatafromURL(String URL) {
        Uri dataUri = Uri.parse(URL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try
        {
            java.net.URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line).append('\n');
        }
        catch (Exception e)
        {
            Log.e(TAG, "EXCEPTION | Newsloader: getOfficialDatafromURL: bp:", e);
            return sb.toString();
        }
        return sb.toString();
    }

    private ArrayList<Article> parseJSON(String data) {
        ArrayList<Article> tempList = new ArrayList<>();
        Article tempArticle;

        try
        {
            JSONObject temp = new JSONObject(data);
            JSONArray articles = (JSONArray) temp.get("articles");

            for(int i = 0; i<articles.length(); i++)
            {
                JSONObject article = (JSONObject) articles.get(i);
                tempArticle = new Article();

                tempArticle.setTitle(getTitlefromData(article));
                tempArticle.setAuthor(getAuthorfromData(article));
                tempArticle.setDescription(getDescfromData(article));
                tempArticle.setPublishedAt(convertDate(getPublishingDatefromDATA(article)));            // Converting Date from Z format to Simple Date Format
                tempArticle.setUrl(getArticleUrlfromData(article));
                tempArticle.setUrlToImage(getUrlToImagefromData(article));
                tempList.add(tempArticle);
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | parseJSON: bp: " + e);
        }

        return tempList;
    }

    private String convertDate(String stringDate)
    {
        Date date = null;
        String public_date;
        try
        {
            if (stringDate != null)
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(stringDate);
            String pattern = "MMM dd, yyyy HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            public_date = simpleDateFormat.format(date);
            return public_date;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String getTitlefromData(JSONObject article)
    {
        String title = "";
        try
        {
            if(article.has("title"))
                title = article.getString("title");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getTitlefromData: bp: " + e);
        }
        return title;
    }

    private String getAuthorfromData(JSONObject article)
    {
        String author = "";
        try
        {
            if(article.has("author"))
                author = article.getString("author");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getAuthorfromData: bp: " + e);
        }
        return author;
    }

    private String getDescfromData(JSONObject article)
    {
        String desc = "";
        try
        {
            if(article.has("description"))
                desc = article.getString("description");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getDescfromData: bp: " + e);
        }
        return desc;
    }


    private String getPublishingDatefromDATA(JSONObject article)
    {
        String publishingDate = "";
        try
        {
            if(article.has("publishedAt"))
                publishingDate = article.getString("publishedAt");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getPublishingDatefromDATA: bp: " + e);
        }
        return publishingDate;
    }

    private String getArticleUrlfromData(JSONObject article)
    {
        String articleUrl = "";
        try
        {
            if(article.has("url"))
                articleUrl = article.getString("url");
        }
        catch(Exception e)
        {
            Log.d(TAG, "EXCEPTION | getArticleUrlfromData: bp: " + e);
        }
        return articleUrl;
    }

    private String getUrlToImagefromData(JSONObject article)
    {
        String urlToImage = "";
        try
        {
            if(article.has("urlToImage"))
                urlToImage = article.getString("urlToImage");
        }
        catch(Exception e)
        {
            Log.d(TAG, "EXCEPTION | getUrlToImagefromData: bp: " + e);
        }
        return urlToImage;
    }

    @Override
    protected void onPostExecute(ArrayList<Article> articles)
    {
        Log.d(TAG, "onPostExecute: bp: Total Articles: " + articles.size());
        newsActivity.updateArticles(articles);
        super.onPostExecute(articles);
    }

}
