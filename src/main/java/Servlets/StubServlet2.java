package Servlets;

import MessageOperators.Status;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

@WebServlet("/testE")
public class StubServlet2  extends HttpServlet {
    private String s = "nil";
    private int last = -1;
    private HashMap<String, Status> map;
    private Random random;

    @Override
    public void init() throws ServletException {
        super.init();
        map = new HashMap<String, Status>();
        random = new Random();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        s = req.getParameter("json");
        JSONObject jsonObject = new JSONObject(req.getParameter("json"));
        if (!jsonObject.has("message_id")) {
            JSONObject json = new JSONObject();
            last = random.nextInt(9);
            if (last > 1) {
                json.put("status", "accepted");
                json.put("id", DigestUtils.sha256Hex(json.toString()));
                int r = random.nextInt(8);
                if (r < 2) {
                    map.put(json.getString("id"), Status.ACCEPTED);
                } else if (r < 5) {
                    map.put(json.getString("id"), Status.DELIVERED);
                } else {
                    map.put(json.getString("id"), Status.REJECTED);
                }
            } else {
                json.put("status", "rejected");
            }
            resp.getOutputStream().write(json.toString().getBytes());
        }else{
            JSONObject json = new JSONObject();
            json.put("status",map.get(jsonObject.getString("message_id")).toString());
            int r = random.nextInt(8);
            if (r < 2) {
                map.put(jsonObject.getString("message_id"), Status.ACCEPTED);
            } else if (r < 5) {
                map.put(jsonObject.getString("message_id"), Status.DELIVERED);
            } else {
                map.put(jsonObject.getString("message_id"), Status.REJECTED);
            }
            resp.getOutputStream().write(json.toString().getBytes());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("s",s);
        req.setAttribute("str",last);
        /*RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/stub.jsp");
        dispatcher.forward(req,resp);*/
    }
}
