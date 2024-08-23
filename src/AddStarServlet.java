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
import java.sql.SQLException;
import java.util.Objects;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "AddStarServlet", urlPatterns = "/api/_dashboard/addstar")
public class AddStarServlet extends HttpServlet {

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
            String name = request.getParameter("star");
            String year = request.getParameter("year");

            String query_getID = "select max(id) as id from stars";

            PreparedStatement statement = dbCon.prepareStatement(query_getID);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            String maxID = "";

            while (rs.next()) {
                maxID = rs.getString("id");
                String numberID = maxID.replaceAll("[^\\d]", "");
                String letterID = maxID.replaceAll("[^a-zA-Z]", "");
                int parsedID = Integer.parseInt(numberID);
                parsedID++;
                maxID = letterID + parsedID;
            }
            rs.close();


            String insertStatement = "INSERT INTO stars(id, name, birthYear) VALUES(?, ?, ?); ";
            PreparedStatement newStatement = dbCon.prepareStatement(insertStatement);

            newStatement.setString(1, maxID);
            newStatement.setString(2, name);
            if (!(Objects.equals(year, ""))) {
                newStatement.setInt(3, Integer.parseInt(year));
            } else {
                newStatement.setNull(3, java.sql.Types.INTEGER);
            }
            int resultSet = newStatement.executeUpdate();


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("star_id", maxID);
            jsonObject.addProperty("star_name", name);
            jsonObject.addProperty("star_birthyear", year);
            jsonObject.addProperty("message", "adding star to database: " + name + " with the following id generated: " + maxID);
            jsonArray.add(jsonObject);


            // Close all structures

            dbCon.close();
            out.write(jsonArray.toString());
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
    }
}

