<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html;charset=cp1251" %>
<%@ page pageEncoding="cp1251" %>

<html>
    <head>
        <title>Settings</title>
    </head>

    <body>
        <div>
            <form method="POST">
                Delay before status request <input type="text" name="delay" value="${delay}">
                <input type="submit" value="Change">
            </form>
        </div>
        <div>
            <br>
            <h2>General</h2>
            Messages received <c:out value = "${received}"/><br>
            Messages received with error <c:out value = "${with_error}"/><br>
            Messages skipped <c:out value = "${skipped}"/><br>
            Messages skipped by ttl <c:out value = "${skipped_by_ttl}"/><br>
            Total delivered <c:out value = "${total}"/><br>
            <br>
            <h2>Pushes</h2>
            Messages send <c:out value = "${pushes}"/><br>
            Messages redirected <c:out value = "${pushes_redir}"/><br>
            <br>
            <h2>SMSs</h2>
            Messages send <c:out value = "${SMSs}"/><br>
            Messages redirected <c:out value = "${SMSs_redir}"/><br>
            <br>
            <h2>Emails</h2>
            Messages send <c:out value = "${emails}"/><br>
        </div>

    </body>

</html>