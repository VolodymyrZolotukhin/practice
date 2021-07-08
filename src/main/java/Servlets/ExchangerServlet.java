package Servlets;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import MessageOperators.JSONFormatException;
import MessageOperators.Message;
import MessageOperators.MessageManger;
import MessageOperators.SettingsContextManager;
import org.json.JSONObject;

@WebServlet("/main")
public class ExchangerServlet extends HttpServlet {

    private String str = "nil";
    private String s = "nil";
    private MessageManger messageManger;

    @Override
    public void init() throws ServletException {
        super.init();
        messageManger = MessageManger.getInstance(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
        JSONObject json = new JSONObject(req.getParameter("jsondata"));
        s = req.getParameter("jsondata");
        try {
            Message message = new Message(json);
            str = message.getToken()+" "+message.getText();
            SettingsContextManager.getInstance(getServletContext()).addReceived();
            messageManger.addMessage(message);
        } catch (JSONFormatException e) {
            SettingsContextManager.getInstance(getServletContext()).addWith_error();
            log("\n--- "+e.getMessage()+" ---\n");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("str",str);
        req.setAttribute("s",s);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
        dispatcher.forward(req,resp);
    }

    @Override
    public void destroy() {
        getServletContext().setAttribute("cycle",Boolean.FALSE);
    }
}
