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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "Confirmation", urlPatterns = "/moviedb/confirm")
public class Confirmation extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
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
        HttpSession session = request.getSession();

        JsonObject responseJsonObject = new JsonObject();

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<String>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        String cid = session.getAttribute("cId").toString();
        System.out.println(cid);

        Date curr = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fDate = sdf.format(curr);
        System.out.println(fDate);

        try {
            Connection conn = dataSource.getConnection();

            String query = "SELECT * FROM sales WHERE customerId = ? and saleDate = ? ";

            request.getServletContext().log("query：" + query);

            // Declare the statements
            PreparedStatement statement = conn.prepareStatement(query);
            System.out.println("current statement: " + statement);
            System.out.println("current id: " + cid);
            System.out.println("current date: " + fDate);


            statement.setInt(1, Integer.parseInt(cid));
            statement.setString(2, fDate);
            System.out.println(statement);
            ResultSet rs = statement.executeQuery();

            System.out.println("executed query");

            rs.next();
            String saleid = rs.getString("id");
            responseJsonObject.addProperty("saleId", saleid);

          statement.close();
            rs.close();
            conn.close();

        }
        catch (Exception e) {
            // Write error message JSON object to output
            System.out.println("confirmation java file caught exception");
            System.out.println(e);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }

        previousItems.clear();
        // write all the data into the jsonObject
        System.out.println("made it here!");
        response.getWriter().write(responseJsonObject.toString());
    }
}