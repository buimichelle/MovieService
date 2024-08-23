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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

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


//        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {

            String query = "select *\n" +
                    "from customers c\n" +
                    "where c.email = ?\n";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();
            JsonObject responseJsonObject = new JsonObject();

            if (rs.next()) {
                System.out.println("logging in:");
                if (VerifyPassword.verifyCredentials(email, password)) {
                    try {
                        //RecaptchaVerifyUtils.verify(gRecaptchaResponse);

                        User newUser = new User();
                        // set this user into the session
                        String cId = rs.getString("id");
                        request.getSession().setAttribute("user", email);
                        request.getSession().setAttribute("userId", cId);
                        request.getSession().setAttribute("password", password);

                        newUser.setEmail(email);
                        newUser.setPassword(password);

                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "success");
                        System.out.println("USER LOGGED IN!");
                    }
                    catch (Exception e) {
                        System.out.println("recaptcha unverified");
                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "Please prove that you are not a bot.");
                        response.getWriter().write(responseJsonObject.toString());
                        out.close();
                    }
                }
                else {
                    responseJsonObject.addProperty("status", "fail");
                    request.getServletContext().log("Password failed");
                    responseJsonObject.addProperty("message", "Incorrect User/jPassword!");
                }
            }
            else {
                System.out.println("USER EMPTY!");
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "Please enter user and password.");
            }
            response.getWriter().write(responseJsonObject.toString());
            out.close();
            rs.close();
            statement.close();
            conn.close();

        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            request.getServletContext().log("Error:", e);
            response.setStatus(500); //internal server error
        }
        finally {
            out.close();
        }
    }
}
