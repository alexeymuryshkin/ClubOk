/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author aibek
 */
public class signup extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String login = request.getParameter("signup_user");
            String pass = request.getParameter("signup_pass");
            String pass2 = request.getParameter("signup_pass2");
            System.out.println(pass);
            System.out.println(pass2);
            if (pass.equals(pass2)){
                MyDB db = new MyDB();
                Connection conn = db.getConn();
                String Query = "select * from login where name=?;";
                PreparedStatement ps = conn.prepareStatement(Query);
                ps.setString(1, login);
                Statement st = conn.createStatement();
                ResultSet rs = ps.executeQuery();

                if (rs.next()){
                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/signup.jsp");
                    out.println("<font color=red>This user has already registered.</font>");
                    rd.include(request, response);
                }else{
                    st.executeUpdate("insert into login (name, pass) values ('" + login + "','" + pass + "')");
                    response.sendRedirect("login.jsp");
                }
                rs.close();
                ps.close();
                conn.close();
            }else{
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/signup.jsp");
		out.println("<font color=red>Passwords do not match.</font>");
		rd.include(request, response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
