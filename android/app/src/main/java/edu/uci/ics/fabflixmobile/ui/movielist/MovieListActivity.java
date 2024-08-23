package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movie.SingleMovieAcitivity;

import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Iterator;


public class MovieListActivity extends AppCompatActivity {

    private final String host = "18.220.127.127";
    private final String port = "8443";
    private final String domain = "cs122b-project1";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private Integer movieLength = 0;

    Integer page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        // TODO: this should be retrieved from the backend server
        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        final Button nextButton = binding.nextButton;
        final Button prevButton = binding.previousButton;

        //assign a listener to call a function to handle the user request when clicking a button
        nextButton.setOnClickListener(view -> next());
        prevButton.setOnClickListener(view -> previous());


        Intent movieList = getIntent();
        page = Integer.parseInt(movieList.getStringExtra("pageNum"));
        Log.d("searchResult", movieList.getStringExtra("movieList"));
        final ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(movieList.getStringExtra("movieList"));
            createList(jsonArray, movies);
        } catch (JSONException e) {
            Log.d("list.error", e.toString());
        }

        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
//            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %s", position, movie.getName(), movie.getYear());
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            Intent singleMovie = new Intent(MovieListActivity.this, SingleMovieAcitivity.class);
            singleMovie.putExtra("title", movie.getName());
            singleMovie.putExtra("year", movie.getYear());
            singleMovie.putExtra("director", movie.getDirector());
            singleMovie.putExtra("genre", movie.getAllGenres());
            singleMovie.putExtra("stars", movie.getAllStars());
            startActivity(singleMovie);
        });
    }

    private void createList(JSONArray jsonArray, ArrayList<Movie> list) throws JSONException {

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movie = jsonArray.getJSONObject(i);
            String title = movie.getString("movie_title");
            String year = movie.getString("movie_year");
            String director = movie.getString("movie_director");
            String genres = movie.getString("movie_genres");
            String stars = movie.getString("movie_stars");
            list.add(new Movie(title, year, director, genres, stars));
        }
        movieLength = list.size();
    }

    public void next() {
        if (movieLength < 10) {
            @SuppressLint("DefaultLocale") String message = "Already at end";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        Intent movieList = getIntent();

        String sort = "titleratingup";
        String title = movieList.getStringExtra("movieSearch");
        String limit = "10";
        page ++;
        String urlPage = page.toString();
        try {
            sort = URLEncoder.encode(sort, "UTF-8");
            title = URLEncoder.encode(title, "UTF-8");
            limit = URLEncoder.encode(limit, "UTF-8");
            urlPage = URLEncoder.encode(urlPage, "UTF-8");

        } catch (Exception e) {
            Log.d("submit.error", e.toString());
        }

        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/moviedb/search" + "?query=" + title + "&page=" + urlPage + "&sort=" + sort + "&limit=" + limit,
                response -> {
                    Log.d("submit.success", response);

                    Intent MoviePage = new Intent(MovieListActivity.this, MovieListActivity.class);
                    //activate the list page.
                    MoviePage.putExtra("movieList", response);
                    MoviePage.putExtra("movieSearch", movieList.getStringExtra("movieSearch"));
                    MoviePage.putExtra("pageNum", page.toString());
                    startActivity(MoviePage);
                },
                error -> {
                    // error
                    Log.d("submit.error", error.getClass().toGenericString());
                }) {
        };
        queue.add(searchRequest);
    }

    public void previous() {
        // use the same network queue across our application
        if (page == 1) {
            @SuppressLint("DefaultLocale") String message = "Already at beginning";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        Intent movieList = getIntent();

        String sort = "titleratingup";
        String title = movieList.getStringExtra("movieSearch");
        String limit = "10";
        page --;
        String urlPage = page.toString();
        try {
            sort = URLEncoder.encode(sort, "UTF-8");
            title = URLEncoder.encode(title, "UTF-8");
            limit = URLEncoder.encode(limit, "UTF-8");
            urlPage = URLEncoder.encode(urlPage, "UTF-8");

        } catch (Exception e) {
            Log.d("submit.error", e.toString());
        }

        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/moviedb/search" + "?query=" + title + "&page=" + urlPage + "&sort=" + sort + "&limit=" + limit,
                response -> {
                    Log.d("submit.success", response);

                    Intent MoviePage = new Intent(MovieListActivity.this, MovieListActivity.class);
                    //activate the list page.
                    MoviePage.putExtra("movieList", response);
                    MoviePage.putExtra("movieSearch", movieList.getStringExtra("movieSearch"));
                    MoviePage.putExtra("pageNum", page.toString());
                    startActivity(MoviePage);
                },
                error -> {
                    // error
                    Log.d("submit.error", error.getClass().toGenericString());
                }) {
        };
        queue.add(searchRequest);
    }
}