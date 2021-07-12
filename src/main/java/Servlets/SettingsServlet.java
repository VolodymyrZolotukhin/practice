package Servlets;

import MessageOperators.SettingsContextManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/settings")
public class SettingsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().setAttribute("delay",Long.parseLong(req.getParameter("delay")));
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("delay",getServletContext().getAttribute("delay"));

        SettingsContextManager manager = SettingsContextManager.getInstance(getServletContext());
        req.setAttribute("received",manager.getReceived());
        req.setAttribute("with_error",manager.getWith_error());
        req.setAttribute("skipped",manager.getSkipped());
        req.setAttribute("skipped_by_ttl",manager.getSkipped_by_ttl());
        req.setAttribute("total",manager.getTotal());

        req.setAttribute("pushes",manager.getPushes());
        req.setAttribute("pushes_redir",manager.getPushes_redir());

        req.setAttribute("SMSs",manager.getSMSs());
        req.setAttribute("SMSs_redir",manager.getSMSs_redir());

        req.setAttribute("emails",manager.getEmails());

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/settings.jsp");
        dispatcher.forward(req,resp);
    }
}
