package edu.uci.ics.fabflixmobile.ui.mainpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityHomeBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import java.net.URLEncoder;

public class HomeActivity extends AppCompatActivity {

    private EditText movie;
    private final String host = "18.220.127.127";
    private final String port = "8443";
    private final String domain = "cs122b-project1";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //search
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        movie = binding.movieSearch;
        final Button searchButton = binding.submit;

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());
    }

    public void search() {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;


        String sort = "titleratingup";
        String title = movie.getText().toString();
        String limit = "10";
        String page = "1";
        String offset = "0";

        String pageCopy = page;

        try {

            sort = URLEncoder.encode(sort, "UTF-8");
            title = URLEncoder.encode(title, "UTF-8");
            limit = URLEncoder.encode(limit, "UTF-8");
            page = URLEncoder.encode(page, "UTF-8");
            offset = URLEncoder.encode(offset, "UTF-8");

        } catch (Exception e) {
            Log.d("submit.error", e.toString());
        }

        final StringRequest searchRequest = new StringRequest(
                    Request.Method.GET,
                    baseURL + "/moviedb/search" + "?query=" + title + "&page=" + page + "&sort=" + sort + "&limit=" + limit,
                    response -> {
                        Log.d("submit.success", response);

                        Intent MoviePage = new Intent(HomeActivity.this, MovieListActivity.class);
                        //activate the list page.
                        MoviePage.putExtra("movieList", response);
                        MoviePage.putExtra("movieSearch", movie.getText().toString());
                        MoviePage.putExtra("pageNum", pageCopy);
                        startActivity(MoviePage);
                    },
                    error -> {
                        // error
                        Log.d("submit.error", error.getClass().toGenericString());
                    }) {
        };
        //important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}

