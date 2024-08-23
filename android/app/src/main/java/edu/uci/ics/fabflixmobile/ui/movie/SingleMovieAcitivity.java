package edu.uci.ics.fabflixmobile.ui.movie;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;
import android.content.Intent;

import java.util.ArrayList;


public class SingleMovieAcitivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlemovie);
        // TODO: this should be retrieved from the backend server

        Intent movieInfo = getIntent();
        TextView mTitle = findViewById(R.id.movie_title);
        TextView mYear = findViewById(R.id.movie_year);
        TextView mDirector = findViewById(R.id.movie_director);
        TextView genres = findViewById(R.id.movie_genre);
        TextView stars = findViewById(R.id.movie_stars);

        mTitle.setText("Title: " + movieInfo.getStringExtra("title"));
        mYear.setText("Released: " + movieInfo.getStringExtra("year"));
        mDirector.setText("Directed by: " + movieInfo.getStringExtra("director"));
        genres.setText("Genres: " + movieInfo.getStringExtra("genre"));
        stars.setText("Starring: " + movieInfo.getStringExtra("stars"));

    }
}
