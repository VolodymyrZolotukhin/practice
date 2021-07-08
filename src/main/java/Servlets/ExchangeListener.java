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
        servletContextEvent.getServletContext().setAttribute("delay",1000);
        servletContextEvent.getServletContext().setAttribute("Timer"+ Priority.PUSH,new Timer(Priority.PUSH.toString(),true));
        servletContextEvent.getServletContext().setAttribute("Timer"+ Priority.SMS,new Timer(Priority.SMS.toString(),true));
        servletContextEvent.getServletContext().setAttribute("Timer"+ Priority.EMAIL,new Timer(Priority.EMAIL.toString(),true));

        SettingsContextManager contextManager = new SettingsContextManager();
        servletContextEvent.getServletContext().setAttribute(SettingsContextManager.ATTRIBUTE_NAME,contextManager);

        MessageManger messageManger = new MessageManger(servletContextEvent.getServletContext());
        servletContextEvent.getServletContext().setAttribute(MessageManger.ATTRIBUTE_NAME,messageManger);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ((Timer) servletContextEvent.getServletContext().getAttribute("Timer"+ Priority.PUSH)).cancel();
        ((Timer) servletContextEvent.getServletContext().getAttribute("Timer"+ Priority.SMS)).cancel();
        ((Timer) servletContextEvent.getServletContext().getAttribute("Timer"+ Priority.EMAIL)).cancel();
        MessageManger.getInstance(servletContextEvent.getServletContext()).interruptThreads();
    }
}
