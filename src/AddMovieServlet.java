import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Objects;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/_dashboard/addmovie")
public class AddMovieServlet extends HttpServlet {

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
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        response.setContentType("application/json");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();
            // Retrieve parameter from the search form
            String name = request.getParameter("movie");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
            String genre = request.getParameter("genre");

            System.out.println("checkpoint 1");
            String query = "CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?);";

            CallableStatement statement = dbCon.prepareCall(query);

            System.out.println("checkpoint 2");
            statement.setString(1,name);
            statement.setInt(2,Integer.parseInt(year));
            statement.setString(3, director);
            statement.setString(4, star);
            statement.setString(5, genre);
            statement.registerOutParameter(6, Types.INTEGER);
            statement.registerOutParameter(7, Types.VARCHAR);
            statement.registerOutParameter(8, Types.INTEGER);
            statement.registerOutParameter(9, Types.VARCHAR);
            System.out.println("checkpoint 3: ");
            statement.execute();
            int result = statement.getInt(6);
            int genreId = statement.getInt(8);
            String starId = statement.getString(9);



            System.out.println("checkpoint 4: " + result);
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();

            if (result == 0) {
                System.out.println("movie already existed.");
                jsonObject.addProperty("message", name + " already exist.");
                jsonArray.add(jsonObject);
            }
            else {
                String newQuery = "Select id from movies m where m.title = ? and m.director = ? and m.year = ?";
                PreparedStatement statement1 = dbCon.prepareStatement(newQuery);
                statement1.setString(1, name);
                statement1.setString(2, director);
                statement1.setInt(3, Integer.parseInt(year));
                ResultSet rs = statement1.executeQuery();
                String movieID = null;
                while (rs.next()) {
                    movieID = rs.getString("id");
                }
                String message = name + " was added!  |  RELEASE: " + year +
                        "  |  GENERATED MOVIE ID: " + movieID +
                        "  |  GENERATED GENRE ID: " + genreId +
                        "  |  GENERATED ACTOR ID: " + starId;
                jsonObject.addProperty("message", message);
                jsonArray.add(jsonObject);
                rs.close();
            }

            statement.close();
            dbCon.close();
            out.write(jsonArray.toString());
            out.close();
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            System.out.println(e.getMessage());
            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}

