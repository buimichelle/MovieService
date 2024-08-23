package edu.uci.ics.fabflixmobile.data.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final String year;
    private final String director;
    private final String genres;
    private final String stars;



    public Movie(String name, String year, String director, String genres, String stars) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }
    public String getDirector() {return director; }
    public String getAllGenres() {return genres; }
    public String getAllStars() {return stars; }

    public String getStarsList() {
        String[] indvStars = stars.split(",");
        String threeStars = "";

        if (indvStars.length < 3 || indvStars.length == 3 ) {
            threeStars = stars.replace(",", " ");
        }
        else {
            for (int i = 0; i < 3 ; i++) {
                threeStars += indvStars[i] + " ";
            }
        }

        return threeStars;
    }

    public String getGenresList() {
        String[] indvGenres = genres.split(",");
        String threeGenres = "";

        if (indvGenres.length < 3 || indvGenres.length == 3 ) {
            threeGenres = genres.replace(",", " ");
        }
        else {
            for (int i = 0; i < 3 ; i++) {
                threeGenres += indvGenres[i] + " ";
            }
        }
        return threeGenres;
    }
}