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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "MetabaseServlet", urlPatterns = "/api/_dashboard/home")
public class MetabaseServlet extends HttpServlet {
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
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT \n" +
                    "    table_name,\n" +
                    "    GROUP_CONCAT(CONCAT(column_name) SEPARATOR ', ') AS attributes,\n" +
                    "\tGROUP_CONCAT(CONCAT(data_type) SEPARATOR ', ') AS datatype\n" +
                    "FROM \n" +
                    "    information_schema.columns\n" +
                    "WHERE \n" +
                    "    table_schema = 'moviedb'\n" +
                    "GROUP BY \n" +
                    "    table_name;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);


            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            System.out.println("connected to the database!");
            // Iterate through each row of rs
            while (rs.next()) {

                String table = rs.getString("table_name");
                String colInfo = rs.getString("attributes");
                String datatype = rs.getString("datatype");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("table", table);
                jsonObject.addProperty("cols", colInfo);
                jsonObject.addProperty("data", datatype);
                jsonArray.add(jsonObject);
            }
            rs.close();
            conn.close();
            statement.close();
            conn.close();

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

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
