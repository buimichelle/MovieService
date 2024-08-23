import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/moviedb/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String previousLink = (String) (session.getAttribute("previousResultLink"));
        System.out.println(sessionId);
        System.out.println(session.getAttribute("previousItems"));
        System.out.println(session.getAttribute("previousResultLink"));
        //long lastAccessTime = session.getLastAccessedTime();

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String queryMain = "SELECT m2.id as movieId, m2.title, m2.year, m2.director, GROUP_CONCAT(g.name) as genres, GROUP_CONCAT(g.id) as genre_ids, r.rating\n" +
                    "FROM genres g\n" +
                    "JOIN genres_in_movies gm ON g.id = gm.genreId\n" +
                    "JOIN movies m2 ON gm.movieId = m2.id\n" +
                    "JOIN ratings r ON r.movieId = m2.id\n" +
                    "WHERE m2.id = ?\n" +
                    "GROUP BY m2.id, m2.title, m2.year, m2.director, r.rating;";

            String queryStars = "SELECT s.name AS star_name, s.id AS star_id, COUNT(DISTINCT sm.movieId) AS total_movie_count\n" +
                    "FROM stars s, stars_in_movies sm\n" +
                    "WHERE s.id = sm.starId and sm.movieId = ?\n" +
                    "GROUP BY s.name, s.id\n" +
                    "ORDER BY total_movie_count DESC, s.name";
            // Declare our statement
            PreparedStatement statement1 = conn.prepareStatement(queryMain);
            PreparedStatement statement2 = conn.prepareStatement(queryStars);


            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement1.setString(1, id);
            statement2.setString(1, id);
            // Perform the query
            ResultSet rs = statement1.executeQuery();
            ResultSet rs2 = statement2.executeQuery();

            JsonArray jsonArray = new JsonArray();


            // Iterate through each row of rs

            JsonObject jsonObject = new JsonObject();

            while (rs.next()) {
                String movie_id = rs.getString("movieId");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String genre_ids = rs.getString("genre_ids");
                String movie_genres = rs.getString("genres");
                String movie_ratings = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                jsonObject.addProperty("movie_ID", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                if (Objects.equals(movie_year, "")) {
                    jsonObject.addProperty("movie_year", "N/A");
                } else {
                    jsonObject.addProperty("movie_year", movie_year);
                }
                if (Objects.equals(movie_director, "")) {
                    jsonObject.addProperty("movie_director", "N/A");
                } else {
                    jsonObject.addProperty("movie_director", movie_director);
                }
                jsonObject.addProperty("genre_ID", genre_ids);
                if (Objects.equals(movie_genres, "")) {
                    jsonObject.addProperty("movie_genres", "N/A");
                } else {
                    jsonObject.addProperty("movie_genres", movie_genres);
                }
                if (Objects.equals(movie_ratings, "")) {
                    jsonObject.addProperty("movie_rating", "N/A");
                } else {
                    jsonObject.addProperty("movie_rating", movie_ratings);
                }
            }
            String star_sequence = "";
            String star_ids = "";
            while (rs2.next()) {
                star_sequence += rs2.getString("star_name") + ", ";
                star_ids += rs2.getString("star_id") + ", ";
            }
            request.getServletContext().log(star_sequence);

            jsonObject.addProperty("star_names", star_sequence);
            jsonObject.addProperty("star_ids", star_ids);

            jsonObject.addProperty("sessionID", sessionId);
            jsonObject.addProperty("previousLink", previousLink);


            jsonArray.add(jsonObject);
            rs.close();
            rs2.close();
            statement1.close();
            statement2.close();
            conn.close();


            // Write JSON string to output
            out.write(jsonArray.toString());
            conn.close();
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}