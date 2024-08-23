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


@WebServlet(name = "GenreServlet", urlPatterns = "/moviedb/single-genre-movie")
public class GenreServlet extends HttpServlet {
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


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");
        String page = request.getParameter("page"); // Basically offset (offset = limit * (page-1))
        String limit = request.getParameter("limit");
        String sorting = request.getParameter("sort");
        HttpSession session = request.getSession(true);


        if (sorting == null && limit == null && page == null) {
            id = (session.getAttribute("Rid")).toString();
            page = (session.getAttribute("Rpage")).toString();
            sorting = (session.getAttribute("Rsorting")).toString();
            limit = (session.getAttribute("Rlimit")).toString();

        }
        else if (sorting != null && limit != null && page != null) {
            session.setAttribute("Rpage", page);
            session.setAttribute("Rid", id);
            session.setAttribute("Rsorting", sorting);
            session.setAttribute("Rlimit", limit);
            session.setAttribute("previousResultLink", "single-genre-movie.html");
        }

        String offset = Integer.toString(Integer.parseInt(limit) * (Integer.parseInt(page) - 1));
        System.out.println("sorting: " + sorting);
        System.out.println("limit: " + offset);

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();


        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "select m.id, m.title, g.name, m.director, m.year, r.rating\n" +
                    "from movies m, genres_in_movies gm, genres g, ratings r\n" +
                    "where gm.genreID = ? and g.id = gm.genreID and m.id = gm.movieID and r.movieId = m.id\n";

            if (sorting.equals("titleratingup")) {
                query += "order by m.title asc, r.rating asc";
                session.setAttribute("search_sorting","titleratingup");
            }
            if (sorting.equals("titleratingdown")) {
                query += "order by m.title desc, r.rating desc";
                session.setAttribute("search_sorting","titleratingdown");
            }
            if (sorting.equals("titleupratingdown")) {
                query += "order by m.title asc, r.rating desc";
                session.setAttribute("search_sorting","titleupratingdown");
            }
            if (sorting.equals("titledownratingup")) {
                query += "order by m.title desc, r.rating asc";
                session.setAttribute("search_sorting","titledownratingup");
            }
            if (sorting.equals("ratingtitleup")) {
                query += "order by r.rating asc, m.title asc";
                session.setAttribute("search_sorting","ratingtitleup");
            }
            if (sorting.equals("ratingtitledown")) {
                query += "order by r.rating desc, m.title desc";
                session.setAttribute("search_sorting","ratingtitledown");
            }
            if (sorting.equals("ratinguptitledown")) {
                query += "order by r.rating asc, m.title desc";
                session.setAttribute("search_sorting","ratinguptitledown");
            }
            if (sorting.equals("ratingdowntitleup")) {
                query += "order by r.rating desc, m.title asc";
                session.setAttribute("search_sorting","ratingdowntitleup");
            }

            query += "\nLimit ?\n";
            query += "Offset ?";
            session.setAttribute("search_limit",limit);
            session.setAttribute("search_offset",offset);

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);
            statement.setInt(2, Integer.parseInt(limit));
            statement.setInt(3, Integer.parseInt(offset));
            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String genre_name = rs.getString("name");
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
                jsonObject.addProperty("genre_name", genre_name);
                jsonObject.addProperty("director", m_director);
                jsonObject.addProperty("year", m_year);
                jsonObject.addProperty("rating", m_rating);
                jsonObject.addProperty("movie_stars", star_sequence);
                jsonObject.addProperty("star_Id", star_ids);
                jsonObject.addProperty("movie_genres", genresStr);
                jsonObject.addProperty("genre_Id", genresID);

                jsonObject.addProperty("Rpage", page);
                jsonObject.addProperty("Rid", id);
                jsonObject.addProperty("Rsort", sorting);
                jsonObject.addProperty("Rlimit", limit);
                jsonObject.addProperty("previousResultLink", "single-genre-movie.html");

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
