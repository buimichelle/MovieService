import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called MovieServlet, which maps to url "/moviedb/movies"
@WebServlet(name = "MovieServlet", urlPatterns = "/moviedb/movies")
public class MovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "Select *\n" +
                    "From movies m, (Select m1.id as movieId, group_concat(s.name) as stars, group_concat(s.id) as starIds\n" +
                    "\t\t\t\tFrom stars s, stars_in_movies sm, movies m1\n" +
                    "\t\t\t\tWhere sm.movieId = m1.id and s.id = sm.starId\n" +
                    "\t\t\t\tGroup by m1.id) mstars,\n" +
                    "                            ratings r,\n" +
                    "\t\t\t\t(Select m2.id as movieId, group_concat(g.name) as genres\n" +
                    "\t\t\t\tFrom genres g, genres_in_movies gm, movies m2\n" +
                    "\t\t\t\tWhere gm.movieId = m2.id and g.id = gm.genreId\n" +
                    "\t\t\t\tGroup by m2.id) mgenres\n" +
                    "Where m.id = r.movieId and m.id = mstars.movieId and m.id = m.mgenres.movieId \n" +
                    "Order by r.rating desc\n" +
                    "Limit 20;";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                String movie_genres = rs.getString("genres");
                String movie_stars = rs.getString("stars");
                String movie_starIds = rs.getString("starIds");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonObject.addProperty("movie_stars", movie_stars);
                jsonObject.addProperty("movie_starIds", movie_starIds);

                jsonArray.add(jsonObject);
            }
            rs.close();
            conn.close();
            statement.close();
            conn.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            out.close();
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
