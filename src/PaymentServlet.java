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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;


@WebServlet(name = "PaymentServlet", urlPatterns = "/moviedb/payment")
public class PaymentServlet extends HttpServlet {
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

        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cardId = request.getParameter("CardNumber");
        String firstName = request.getParameter("FirstName");
        String lastName = request.getParameter("LastName");
        String expire = request.getParameter("Expiration");
        HttpSession session = request.getSession();

        try {
            Connection conn = dataSource.getConnection();

            String query = "Select * From creditcards " +
            String.format("Where id = '%s' and firstName = '%s' and lastName = '%s' and expiration = '%s'"
                    , cardId, firstName, lastName, expire);
            System.out.println(query);

            request.getServletContext().log("query：" + query);

            // Declare the statements
            PreparedStatement statement = conn.prepareStatement(query);


            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            System.out.println("executed query");

            JsonObject responseJsonObject = new JsonObject();
            //JsonArray jsonArray = new JsonArray();

            //String cid = session.getAttribute("userId").toString();
            String cid = cardId;
            System.out.println(cid);

            Date curr = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fDate = sdf.format(curr);
            System.out.println(fDate);

            session.setAttribute("cId", cid);

            if (rs.next()) {
                //found match, rs not 0
                System.out.println("success");
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                //add insert query
                ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
                if (previousItems == null) {
                    previousItems = new ArrayList<String>();
                    session.setAttribute("previousItems", previousItems);

                    System.out.println("fail");
                    responseJsonObject.addProperty("status", "fail");

                    request.getServletContext().log("Payment failed");
                    responseJsonObject.addProperty("message", "empty shopping cart");
                }
                else {
                    for (int i = 0; i < previousItems.size(); i++) {
                        String query2 = String.format("Select * From movies Where title = '%s';", previousItems.get(i));
                        System.out.println(query2);

                        PreparedStatement statement3 = conn.prepareStatement(query2);
                        ResultSet rs2 = statement3.executeQuery(query2);
                        System.out.println(rs2);
                        rs2.next();
                        String mId = rs2.getString("id");

                        String q2 =  String.format("Insert INTO sales(customerId, movieId, saleDate) VALUES('%s', '%s', '%s')", cid, mId, fDate);
                        PreparedStatement statement2 = conn.prepareStatement(q2);
                        int rows = statement2.executeUpdate();
                        System.out.println(rows);
                    }
                }
                statement.close();
                rs.close();
                conn.close();

            }
            else {
                // Fail
                System.out.println("fail");
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Payment failed");
                responseJsonObject.addProperty("message", "incorrect payment info, please re-enter");
            }
            response.getWriter().write(responseJsonObject.toString());

            rs.close();
            statement.close();


            conn.close();

            response.setStatus(200);
        }
        catch (Exception e) {
            // Write error message JSON object to output
            System.out.println("caught exception");
            System.out.println(e.getClass().getName());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
    }

}
