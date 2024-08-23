import java.util.List;
import java.util.Set;

public class Movie {

    private final String id;
    private final String title;
    private final int year;
    private final String director;
    private final Set<String> genres;


    public Movie(String id, String title, int year, String director, Set<String> genres) {
        this.title = title;
        this.id = id;
        this.year = year;
        this.director = director;
        this.genres = genres;

    }

    public int getYear() {
        return year;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public Set<String> getGenres() { return genres; }
    public String toString() {

        return "Title:" + getTitle() + ", " +
                "ID:" + getId() + ", " +
                "Year:" + getYear() + ", " +
                "Director:" + getDirector() + ", " +
                "Genres:" + getGenres() + ".";
    }
}
