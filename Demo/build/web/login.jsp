<%-- 
    Document   : login
    Created on : 12.02.2018, 3:34:33
    Author     : aibek
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body>
    <form action="login" method="POST">
 
        <pre>
            <input type="text" name="login_user" placeholder="login" required/>
            <input type="password" name="login_pass" placeholder="password" required/>
            <input type="submit" value="Login"/>
        </pre>
 
    </form>
</body>
</html>