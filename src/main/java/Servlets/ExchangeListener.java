package Servlets;

import MessageOperators.MessageManger;
import MessageOperators.Priority;
import MessageOperators.SettingsContextManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Timer;

@WebListener
public class ExchangeListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().setAttribute("delay", 1000L);

        SettingsContextManager contextManager = new SettingsContextManager();
        servletContextEvent.getServletContext().setAttribute(SettingsContextManager.ATTRIBUTE_NAME,contextManager);

        MessageManger messageManger = new MessageManger(servletContextEvent.getServletContext());
        servletContextEvent.getServletContext().setAttribute(MessageManger.ATTRIBUTE_NAME,messageManger);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        MessageManger.getInstance(servletContextEvent.getServletContext()).interruptThreads();
    }
}
