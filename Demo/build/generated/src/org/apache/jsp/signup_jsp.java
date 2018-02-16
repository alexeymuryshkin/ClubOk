package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class signup_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("    <script src=\"http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/md5.js\"></script>\n");
      out.write("    <script>\n");
      out.write("        \n");
      out.write("        function encrypt(){\n");
      out.write("            var pass1 = document.getElementById(\"signup_pass\");\n");
      out.write("            pass1.innerHTML = \"CryptoJS.MD5(pass2)\";\n");
      out.write("            var pass2 = document.getElementById(\"signup_pass2\");\n");
      out.write("            pass2.innerHTML = \"CryptoJS.MD5(pass2)\";\n");
      out.write("        }\n");
      out.write("    </script>\n");
      out.write("</head>\n");
      out.write("<body>\n");
      out.write("    <form action=\"signup\" method=\"post\">\n");
      out.write(" \n");
      out.write("        <pre>\n");
      out.write("            <input type=\"text\" name=\"signup_user\" placeholder=\"login\" required/>\n");
      out.write("            <input type=\"password\" name=\"signup_pass\" placeholder=\"password\" required/>\n");
      out.write("            <input type=\"password\" name=\"signup_pass2\" placeholder=\"repeat password\" required/>\n");
      out.write("            <input type=\"submit\" value=\"register\" onclick=\"encrypt()\"/>\n");
      out.write("        </pre>\n");
      out.write(" \n");
      out.write("    </form>\n");
      out.write("</body>\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
