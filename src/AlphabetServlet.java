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
import java.util.Objects;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "AlphabetServlet", urlPatterns = "/moviedb/single-letter-movie")
public class AlphabetServlet extends HttpServlet {
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

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        // Retrieve parameter id from url request.
        String id = (request.getParameter("id")).split("=")[1];
        System.out.println(id);
        String page = request.getParameter("page"); // Basically offset (offset = limit * (page-1))
        String limit = request.getParameter("limit");
        String sorting = request.getParameter("sort");

        HttpSession session = request.getSession(true);


        if (sorting == null && limit == null && page == null) {
            id = (session.getAttribute("Aid")).toString();
            page = (session.getAttribute("Apage")).toString();
            sorting = (session.getAttribute("Asorting")).toString();
            limit = (session.getAttribute("Alimit")).toString();

        }
        else if (sorting != null && limit != null && page != null) {
            session.setAttribute("Apage", page);
            session.setAttribute("Aid", id);
            session.setAttribute("Asorting", sorting);
            session.setAttribute("Alimit", limit);
            session.setAttribute("previousResultLink", "single-genre-movie.html");
        }



        String offset = Integer.toString(Integer.parseInt(limit) * (Integer.parseInt(page) - 1));


        // The log message can be found in localhost log
        request.getServletContext().log("getting letter: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "";
            PreparedStatement statement;
            if (id.equals("*")) {

                query = "select distinct m.id, m.title, m.year, m.director, r.rating\n" +
                        "from movies m, ratings r\n" +
                        "where m.title regexp '^[^A-Za-z0-9]' and r.movieId = m.id\n";

                if (sorting.equals("titleratingup")) {
                    query += "order by m.title asc, r.rating asc";
                }
                if (sorting.equals("titleratingdown")) {
                    query += "order by m.title desc, r.rating desc";
                }
                if (sorting.equals("titleupratingdown")) {
                    query += "order by m.title asc, r.rating desc";
                }
                if (sorting.equals("titledownratingup")) {
                    query += "order by m.title desc, r.rating asc";
                }
                if (sorting.equals("ratingtitleup")) {
                    query += "order by r.rating asc, m.title asc";
                }
                if (sorting.equals("ratingtitledown")) {
                    query += "order by r.rating desc, m.title desc";
                }
                if (sorting.equals("ratinguptitledown")) {
                    query += "order by r.rating asc, m.title desc";
                }
                if (sorting.equals("ratingdowntitleup")) {
                    query += "order by r.rating desc, m.title asc";
                }

                query += "\nLimit ?\n";
                query += "Offset ?";

                statement = conn.prepareStatement(query);
                statement.setInt(1, Integer.parseInt(limit));
                statement.setInt(2, Integer.parseInt(offset));
            }
            else {
                query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating " +
                        "FROM movies m " +
                        "JOIN ratings r ON r.movieId = m.id " +
                        "WHERE m.title LIKE ? " +
                        "ORDER BY ";
                if (sorting.equals("titleratingup")) {
                    query += "m.title ASC, r.rating ASC";
                }
                else if (sorting.equals("titleratingdown")) {
                    query += "m.title DESC, r.rating DESC";
                }
                else if (sorting.equals("titleupratingdown")) {
                    query += "m.title ASC, r.rating DESC";
                }
                else if (sorting.equals("titledownratingup")) {
                    query += "m.title DESC, r.rating ASC";
                }
                else if (sorting.equals("ratingtitleup")) {
                    query += "r.rating ASC, m.title ASC";
                }
                else if (sorting.equals("ratingtitledown")) {
                    query += "r.rating DESC, m.title DESC";
                }
                else if (sorting.equals("ratinguptitledown")) {
                    query += "r.rating ASC, m.title DESC";
                }
                else if (sorting.equals("ratingdowntitleup")) {
                    query += "r.rating DESC, m.title ASC";
                }
                query += "\nLimit ?\n";
                query += "Offset ?";

                statement = conn.prepareStatement(query);
                statement.setString(1, id + '%');
                statement.setInt(2, Integer.parseInt(limit));
                statement.setInt(3, Integer.parseInt(offset));
            }
            // Declare our statement

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query

            // Perform the query
            ResultSet rs = statement.executeQuery();
            System.out.println(rs);
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String m_director = rs.getString("director");
                String m_year = rs.getString("year");
                String m_rating = rs.getString("rating");

                String queryGenre = "SELECT GROUP_CONCAT(g.name) as genres, GROUP_CONCAT(g.id) as genre_ids\n" +
                        "FROM genres g\n" +
                        "JOIN genres_in_movies gm ON g.id = gm.genreId\n" +
                        "JOIN movies m2 ON gm.movieId = m2.id\n" +
                        "JOIN ratings r ON r.movieId = m2.id\n" +
                        "WHERE m2.id = ?\n" +
                        "GROUP BY m2.id, m2.title, m2.year, m2.director, r.rating\n";


                String queryStars = "SELECT s.name AS star_name, s.id AS star_id, COUNT(DISTINCT sm1.movieId) AS total_movie_count\n" +
                        "FROM stars s\n" +
                        "JOIN stars_in_movies sm1 ON s.id = sm1.starId\n" +
                        "WHERE sm1.starId IN (\n" +
                        "    SELECT sm2.starId\n" +
                        "    FROM stars_in_movies sm2\n" +
                        "    WHERE sm2.movieId = ?\n" +
                        ")\n" +
                        "GROUP BY s.name, s.id\n" +
                        "ORDER BY total_movie_count DESC, s.name\n";

                PreparedStatement statement2 = conn.prepareStatement(queryGenre);
                PreparedStatement statement3 = conn.prepareStatement(queryStars);
                statement2.setString(1, movie_id);
                statement3.setString(1, movie_id);
                ResultSet rs2 = statement2.executeQuery();
                ResultSet rs3 = statement3.executeQuery();

                String star_sequence = "";
                String star_ids = "";
                while (rs3.next()) {
                    star_sequence += rs3.getString("star_name") + ", ";
                    star_ids += rs3.getString("star_id") + ", ";
                }
                String genresStr = "";
                String genresID = "";
                while (rs2.next()) {
                    genresStr += rs2.getString("genres") + ", ";
                    genresID += rs2.getString("genre_ids") + ", ";
                }

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_director", m_director);
                jsonObject.addProperty("movie_year", m_year);
                jsonObject.addProperty("movie_rating", m_rating);
                jsonObject.addProperty("movie_stars", star_sequence);
                jsonObject.addProperty("star_Id", star_ids);
                jsonObject.addProperty("movie_genres", genresStr);
                jsonObject.addProperty("genre_Id", genresID);
                jsonObject.addProperty("Apage", page);
                jsonObject.addProperty("Aid", id);
                jsonObject.addProperty("Asort", sorting);
                jsonObject.addProperty("Alimit", limit);
                jsonObject.addProperty("previousResultLink", "single-letter-movie.html");
                System.out.println("year: " + m_year);
                jsonArray.add(jsonObject);
                rs2.close();
                statement2.close();
                rs3.close();
                statement3.close();
            }
            rs.close();
            statement.close();

            conn.close();
            // Write JSON string to output
            out.write(jsonArray.toString());
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
