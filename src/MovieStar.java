import java.util.ArrayList;

public class MovieStar {

    private final String mId;
    private final String starName;
    private final String title;

    public MovieStar(String mid, String star, String title) {
        this.mId = mid;
        this.starName = star;
        this.title = title;
    }

    public String getmId() {
        return mId;
    }
    public String getTitle() {
        return title;
    }

    public String getStarName() {
        return starName;
    }

    public String toString() {

        return "Movie ID:" + getmId() +
                " Star Name: " + getStarName() +
                " Movie title: " + getTitle();
    }
}
