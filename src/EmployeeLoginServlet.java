
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

@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/_dashboard/login")
public class EmployeeLoginServlet extends HttpServlet {

    private DataSource dataSource;
    private static final long serialVersionUID = 1L;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");

        String password = request.getParameter("password");

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        PrintWriter out = response.getWriter();

        System.out.println("HI");
        try (Connection conn = dataSource.getConnection()) {

            String query = "select *\n" +
                    "from employees c\n" +
                    "where c.email = ?\n";


            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);


            ResultSet rs = statement.executeQuery();
            System.out.println("EXECUTING QUERY");
            JsonObject responseJsonObject = new JsonObject();

            if (rs.next()) {
                if (gRecaptchaResponse.equals("")) {
                    System.out.println("you are a bot lol");
                    responseJsonObject.addProperty("message", "Please prove that you are not a bot.");
                }
                if (VerifyEmployeePassword.verifyCredentials(email, password)) {
                    try {
                        RecaptchaVerifyUtils.verify(gRecaptchaResponse);

                    User newUser = new User();
                    System.out.println("USER LOGGED IN!");
                    // set this user into the session

                    if (request.getSession().getAttribute("user") != null){
                        request.getSession().removeAttribute("user");
                    }
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    request.getSession().setAttribute("userEmployee", email);
                    request.getSession().setAttribute("passEmployee", password);

                        responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    } catch (Exception e) {
                        System.out.println("here");
                        response.getWriter().write(responseJsonObject.toString());
//                        out.println("error");
//                        out.close();
                        return;
                    }
                }
                else {
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Password failed");
                    responseJsonObject.addProperty("message", "incorrect password!");
                }
                conn.close();
                rs.close();
                statement.close();
            } else {
                System.out.println("USER FAILED!");
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "log in has failed, please mich try again!");
            }
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            conn.close();
            statement.close();
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
