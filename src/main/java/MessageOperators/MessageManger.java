package MessageOperators;


import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MessageManger {
    public static final String ATTRIBUTE_NAME = "MessageManager";
    private static final String URL_PUSH = "http://localhost:8080/receive/test";
    private static final String URL_SMS = "http://localhost:8080/receive/testS";
    private static final String URL_EMAIL = "http://localhost:8080/receive/testE";

    private ServletContext context;
    private LinkedList<Message> pushes;
    private LinkedList<Message> SMSs;
    private LinkedList<Message> emails;
    private Thread[] threads;
    private List<Thread> foreignThreads;

    public MessageManger(ServletContext context){
        pushes = new LinkedList<Message>();
        SMSs = new LinkedList<Message>();
        emails = new LinkedList<Message>();
        threads = new Thread[3];
        this.context = context;
        foreignThreads = new ArrayList<Thread>();
        initSenders();
    }

    public static MessageManger getInstance(ServletContext context){
        return (MessageManger) context.getAttribute(ATTRIBUTE_NAME);
    }

    public void addMessage(Message message){
        Priority priority = getPriority(message);
        pushToQueue(message,priority);
    }

    private void pushToQueue(Message message, Priority priority){
        switch (priority){
            case PUSH:
                pushes.addLast(message);
                synchronized (threads[0]) {
                    threads[0].notify();
                }
                break;

            case SMS:
                SMSs.addLast(message);
                synchronized (threads[1]) {
                    threads[1].notify();
                }
                break;

            case EMAIL:
                emails.addLast(message);
                synchronized (threads[2]) {
                    threads[2].notify();
                }
                break;
        }
    }

    private Priority getPriority(Message message){
        if (message.getToken() != null && !message.getToken().equals("")){
            return Priority.PUSH;
        }else if (message.getPhone() != null && !message.getPhone().equals("")){
            return Priority.SMS;
        }else {
            return Priority.EMAIL;
        }
    }

    private Priority getNextPriority(Message message, Priority priority){
        if (priority == Priority.PUSH){
            if (message.getPhone() != null && !message.getPhone().equals("")){
                return Priority.SMS;
            }else{
                return getNextPriority(message, Priority.SMS);
            }
        }else if (priority == Priority.SMS){
            if (message.getEmail() != null && !message.getEmail().equals("")){
                return Priority.EMAIL;
            }
        }
        SettingsContextManager.getInstance(context).addSkipped();
        return Priority.SKIP;
    }

    public synchronized void sendAgain(Message message, Priority priority){
        Priority p = getNextPriority(message,priority);
        pushToQueue(message,p);
        //System.out.println("\n--- "+message.getNotification_id()+" redirected to "+p.name()+" ---\n");
    }

    private void initSenders() {
        MessageSender push = new MessageSender(URL_PUSH, pushes, Priority.PUSH,this);
        MessageSender sms = new MessageSender(URL_SMS, SMSs, Priority.SMS,this);
        MessageSender email = new MessageSender(URL_EMAIL, emails, Priority.EMAIL,this);
        threads[0] = new SenderThread(push);
        threads[1] = new SenderThread(sms);
        threads[2] = new SenderThread(email);
        for (Thread th:
                threads) {
            th.start();
        }
    }

    public void interruptThreads() {
        for (Thread th: threads) {
            th.interrupt();
        }
        for (Thread th: foreignThreads){
            th.interrupt();
        }
    }

    public ServletContext getContext() {
        return context;
    }
    public void addThread(Thread th){
        foreignThreads.add(th);
    }
}
