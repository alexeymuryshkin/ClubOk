<%-- 
    Document   : login
    Created on : 12.02.2018, 3:34:33
    Author     : aibek
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <script src="http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/md5.js"></script>
    <script>
        
        function encrypt(){
            document.getElementById("signup_pass").innerHTML = "CryptoJS.MD5(pass2)";
            document.getElementById("signup_pass2").innerHTML = "CryptoJS.MD5(pass2)";
            document.getElementById("signup_pass").value;
        }
    </script>
</head>
<body>
    <form action="signup" method="post">
 
        <pre>
            <input type="text" name="signup_user" placeholder="login" required/>
            <input type="password" name="signup_pass" placeholder="password" required/>
            <input type="password" name="signup_pass2" placeholder="repeat password" required/>
            <input type="submit" value="register" onclick="encrypt()"/>
        </pre>
 
    
</body>
</html>