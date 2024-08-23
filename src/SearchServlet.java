import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Objects;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "SearchServlet", urlPatterns = "/moviedb/search")
public class SearchServlet extends HttpServlet {

    private static final long serialVersionUID = 8L;
    // Create a dataSource which registered in web.xml
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String file = "/home/ubuntu/stats.txt";
        long startTime = System.nanoTime();
        long JDBC_elapsedTime = System.nanoTime();
        response.setContentType("application/json");    // Response mime type
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(true);
        String queryTitle = request.getParameter("query");
        Boolean adBool = false;
        Enumeration<String> parameterNames = request.getParameterNames();
        System.out.print(queryTitle);
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            System.out.println("\nParam Name: " + paramName);
            if (paramName.equals("query")) {
                adBool = false;
                System.out.println("hey!!!!");
                session.setAttribute("query", "t");
                break; // You can break out of the loop since you found the parameter
            }
            if (paramName.equals("movie")) {
                adBool = true;
                session.setAttribute("query", "f");
                break;
            }
        }
        System.out.println(adBool);
        System.out.println(session.getAttribute("query"));
        if (adBool || (session.getAttribute("query") == "f") || (adBool && (session.getAttribute("query") == "f" || (session.getAttribute("query") == null)))) {
            try {
                System.out.println("SearchServlet: no query here");
                Connection dbCon = dataSource.getConnection();

                String name = request.getParameter("name");
                String year = request.getParameter("year");
                String director = request.getParameter("director");
                String mName = request.getParameter("movie");
                String page = request.getParameter("page"); // Basically offset (offset = limit * (page-1))
                String sorting = request.getParameter("sort");
                String limit = request.getParameter("limit");

                if (sorting == null && limit == null && page == null) {
                    page = (session.getAttribute("page")).toString();
                    sorting = (session.getAttribute("sorting")).toString();
                    limit = (session.getAttribute("limit")).toString();
                    name = (session.getAttribute("star_name")).toString();
                    year = (session.getAttribute("year")).toString();
                    director = (session.getAttribute("director")).toString();
                    mName = (session.getAttribute("movie")).toString();
                }
                else if (sorting != null && limit != null && page != null) {
                    System.out.println("saving the parameters");
                    session.setAttribute("star_name", name);
                    session.setAttribute("year", year);
                    session.setAttribute("director", director);
                    session.setAttribute("movie", mName);
                    session.setAttribute("page", page);
                    session.setAttribute("sorting", sorting);
                    session.setAttribute("limit", limit);
                    session.setAttribute("previousResultLink", "search.html");
                    session.setAttribute("query", "f");
                    session.removeAttribute("queryTitle");
                }

                String offset = Integer.toString(Integer.parseInt(limit) * (Integer.parseInt(page) - 1));
                String query = "SELECT distinct m.title, m.id, m.director, m.year, r.rating\n" +
                        "From stars s, movies m, stars_in_movies sm, ratings r\n" +
                        "Where sm.starId = s.id and sm.movieId = m.id and r.movieId = m.id " +
                        "and s.name LIKE ? and m.title LIKE ? and m.director LIKE ? ";
                if (!year.isEmpty()) {
                    query += " and m.year = ?";
                }
                query += setSorting(sorting);
                query += "\nLimit ?\n";
                query += "Offset ?";

                long JDBCstart = System.nanoTime();
                PreparedStatement statement = dbCon.prepareStatement(query);
                statement.setString(1, "%" + name + "%");
                statement.setString(2, "%" + mName + "%");
                statement.setString(3, "%" + director + "%");
                if (!year.isEmpty()) {
                    statement.setString(4, year);
                    statement.setInt(5, Integer.parseInt(limit));
                    statement.setInt(6, Integer.parseInt(offset));
                }
                else {
                    statement.setInt(4, Integer.parseInt(limit));
                    statement.setInt(5, Integer.parseInt(offset));
                }

                request.getServletContext().log("query：" + query);
                ResultSet rs = statement.executeQuery();
                JsonArray jsonArray = new JsonArray();

                while (rs.next()) {
                    String m_ID = rs.getString("id");
                    String m_title = rs.getString("title");
                    String m_director = rs.getString("director");
                    String m_year = rs.getString("year");
                    String m_rating = rs.getString("rating");

                    String queryGenre = buildGenreQuery();
                    String queryStars = buildStarQuery();
                    PreparedStatement statement2 = dbCon.prepareStatement(queryGenre);
                    PreparedStatement statement3 = dbCon.prepareStatement(queryStars);
                    statement2.setString(1, m_ID);
                    statement3.setString(1, m_ID);
                    ResultSet rs2 = statement2.executeQuery();
                    ResultSet rs3 = statement3.executeQuery();

                    String star_sequence = "";
                    String star_ids = "";
                    while (rs3.next()) {
                        star_sequence += rs3.getString("star_name") + ", ";
                        star_ids += rs3.getString("star_id") + ", ";
                    }
                    star_sequence = star_sequence.substring(0, star_sequence.length() - 2);
                    star_ids = star_ids.substring(0, star_ids.length() - 1);

                    String genresStr = "";
                    String genresID = "";
                    while (rs2.next()) {
                        genresStr += rs2.getString("genres") + ", ";
                        genresID += rs2.getString("genre_ids") + ", ";
                    }

                    long JDBCend = System.nanoTime();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", m_ID);
                    jsonObject.addProperty("movie_title", m_title);
                    jsonObject.addProperty("movie_director", m_director);
                    jsonObject.addProperty("movie_year", m_year);
                    jsonObject.addProperty("movie_rating", m_rating);
                    jsonObject.addProperty("movie_stars", star_sequence);
                    jsonObject.addProperty("star_Id", star_ids);
                    jsonObject.addProperty("movie_genres", genresStr);
                    jsonObject.addProperty("genre_Id", genresID);

                    String previousLink = (String) session.getAttribute("previousResultLink");
                    jsonObject.addProperty("previousLink", previousLink);
                    jsonObject.addProperty("Sstar_name", name);
                    jsonObject.addProperty("Syear", year);
                    jsonObject.addProperty("Sdirector", director);
                    jsonObject.addProperty("Smovie", mName);
                    jsonObject.addProperty("Spage", page);
                    jsonObject.addProperty("Ssort", sorting);
                    jsonObject.addProperty("Slimit", limit);

                    jsonArray.add(jsonObject);
                    rs2.close();
                    statement2.close();
                    rs3.close();
                    statement3.close();
                    JDBC_elapsedTime = JDBCend - JDBCstart;

                }
                rs.close();
                statement.close();
                dbCon.close();

                out.write(jsonArray.toString());
                response.setStatus(200);
            }
            catch (Exception e) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());

                request.getServletContext().log("Error:", e);
                response.setStatus(500);
            }
            finally {
                out.close();
            }
        }
        else {
            try {
                Connection dbCon = dataSource.getConnection();
                System.out.println("SearchServlet: in else, full text");

                String page = (request.getParameter("page"));
                String sorting = (request.getParameter("sort"));
                String limit = (request.getParameter("limit"));

                if (request.getParameter("limit") != null) {
                    session.setAttribute("page", page);
                    session.setAttribute("sorting", sorting);
                    session.setAttribute("limit", limit);
                    session.setAttribute("previousResultLink", "search.html");
                    session.setAttribute("query", "t");
                    session.setAttribute("queryTitle", queryTitle);
                }
                else {
                    sorting = (String) session.getAttribute("sorting");
                    limit = (String) session.getAttribute("limit");
                    page = (String) session.getAttribute("page");
                    queryTitle = (String) session.getAttribute("queryTitle");
                    session.setAttribute("query", "t");
                }
                String offset = Integer.toString(Integer.parseInt(limit) * (Integer.parseInt(page) - 1));

                String booleanMatch = "SELECT * FROM movies m JOIN ratings r on (r.movieId = m.id)\n" +
                        "WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE) ";
                booleanMatch += setSorting(sorting);
                booleanMatch += "\nLimit ?\n";
                booleanMatch += "Offset ?";

                long JDBCstart = System.nanoTime();
                PreparedStatement statement = dbCon.prepareStatement(booleanMatch);
                String newQuery = "";
                for (String q : queryTitle.split(" ")) {
                    newQuery += "+" + q + "* ";
                }
                statement.setString(1, newQuery);
                statement.setInt(2, Integer.parseInt(limit));
                statement.setInt(3, Integer.parseInt(offset));

                ResultSet rs = statement.executeQuery();
                JsonArray jsonArray = new JsonArray();

                while (rs.next()) {
                    String m_ID = rs.getString("id");
                    String m_title = rs.getString("title");
                    String m_director = rs.getString("director");
                    String m_year = rs.getString("year");
                    String m_rating = rs.getString("rating");

                    String queryGenre = buildGenreQuery(); //returns query String
                    String queryStars = buildStarQuery(); //returns query String

                    PreparedStatement statement2 = dbCon.prepareStatement(queryGenre);
                    PreparedStatement statement3 = dbCon.prepareStatement(queryStars);
                    statement2.setString(1, m_ID);
                    statement3.setString(1, m_ID);
                    ResultSet rs2 = statement2.executeQuery();
                    ResultSet rs3 = statement3.executeQuery();

                    String star_sequence = "";
                    String star_ids = "";
                    while (rs3.next()) {
                        star_sequence += rs3.getString("star_name") + ", ";
                        star_ids += rs3.getString("star_id") + ", ";
                    }
                    if (star_sequence != "") {
                        star_sequence = star_sequence.substring(0, star_sequence.length() - 2);
                        star_ids = star_ids.substring(0, star_ids.length() - 1);
                    }

                    String genresStr = "";
                    String genresID = "";
                    while (rs2.next()) {
                        genresStr += rs2.getString("genres") + ", ";
                        genresID += rs2.getString("genre_ids") + ", ";
                    }

                    long JDBCend = System.nanoTime();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", m_ID);
                    jsonObject.addProperty("movie_title", m_title);
                    jsonObject.addProperty("movie_director", m_director);
                    jsonObject.addProperty("movie_year", m_year);
                    jsonObject.addProperty("movie_rating", m_rating);
                    jsonObject.addProperty("movie_stars", star_sequence);
                    jsonObject.addProperty("star_Id", star_ids);
                    jsonObject.addProperty("movie_genres", genresStr);
                    jsonObject.addProperty("genre_Id", genresID);

                    String previousLink = (String) session.getAttribute("previousResultLink");
                    jsonObject.addProperty("previousLink", previousLink);
                    jsonObject.addProperty("Squery", queryTitle);
                    jsonObject.addProperty("Spage", page);
                    jsonObject.addProperty("Ssort", sorting);
                    jsonObject.addProperty("Slimit", limit);
                    jsonObject.addProperty("message", "not empty");

                    jsonArray.add(jsonObject);

                    rs2.close();
                    statement2.close();
                    rs3.close();
                    statement3.close();
                    JDBC_elapsedTime = JDBCend - JDBCstart;
                }

                rs.close();
                statement.close();
                dbCon.close();

                if (jsonArray.isEmpty()) {
                    System.out.println("query returns empty");
                    JsonObject jsonObject = new JsonObject();
                    String previousLink = (String) session.getAttribute("previousResultLink");
                    jsonObject.addProperty("previousLink", previousLink);
                    jsonObject.addProperty("Squery", queryTitle);
                    jsonObject.addProperty("Spage", page);
                    jsonObject.addProperty("Ssort", sorting);
                    jsonObject.addProperty("Slimit", limit);
                    jsonObject.addProperty("message", "empty");
                    jsonArray.add(jsonObject);
                }

                out.write(jsonArray.toString());
                response.setStatus(200);
            }
            catch (Exception e) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());

                request.getServletContext().log("Error:", e);
                response.setStatus(500);
            }
            finally {
                out.close();
            }
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;

        String currentDirectory = System.getProperty("user.dir");
        System.out.println("Current Directory: " + currentDirectory);
        System.out.println(file);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            writer.println(elapsedTime + "," + JDBC_elapsedTime + "/n");
            System.out.println("SearchServlet: wrote to file");
        } catch (Exception e) {
            System.out.println("SearchServlet: error writing to file");
            e.printStackTrace();
        }
        System.out.println("Search servlet: total exec time = " + elapsedTime);
        System.out.println("Search servlet: JDBC exec time = " + JDBC_elapsedTime);
    }

    private String setSorting(String sorting) {
        String sortSetting = "";

        if (sorting.equals("titleratingup")) {
            sortSetting = "\norder by m.title asc, r.rating asc";
        }
        if (sorting.equals("titleratingdown")) {
            sortSetting = "\norder by m.title desc, r.rating desc";
        }
        if (sorting.equals("titleupratingdown")) {
            sortSetting = "\norder by m.title asc, r.rating desc";
        }
        if (sorting.equals("titledownratingup")) {
            sortSetting = "\norder by m.title desc, r.rating asc";
        }
        if (sorting.equals("ratingtitleup")) {
            sortSetting = "\norder by r.rating asc, m.title asc";
        }
        if (sorting.equals("ratingtitledown")) {
            sortSetting = "\norder by r.rating desc, m.title desc";
        }
        if (sorting.equals("ratinguptitledown")) {
            sortSetting = "\norder by r.rating asc, m.title desc";
        }
        if (sorting.equals("ratingdowntitleup")) {
            sortSetting = "\norder by r.rating desc, m.title asc";
        }

        return sortSetting;
    }

    private String buildStarQuery() {
        return "SELECT s.name AS star_name, s.id AS star_id, COUNT(DISTINCT sm.movieId) AS total_movie_count\n" +
                "FROM stars s, stars_in_movies sm\n" +
                "WHERE s.id = sm.starId and sm.movieId = ?\n" +
                "GROUP BY s.name, s.id\n" +
                "ORDER BY total_movie_count DESC, s.name";
    }

    private String buildGenreQuery() {
        return "SELECT GROUP_CONCAT(g.name) as genres, GROUP_CONCAT(g.id) as genre_ids\n" +
                "FROM genres g\n" +
                "JOIN genres_in_movies gm ON g.id = gm.genreId\n" +
                "JOIN movies m2 ON gm.movieId = m2.id\n" +
                "JOIN ratings r ON r.movieId = m2.id\n" +
                "WHERE m2.id = ?\n" +
                "GROUP BY m2.id, m2.title, m2.year, m2.director, r.rating";
    }
}

