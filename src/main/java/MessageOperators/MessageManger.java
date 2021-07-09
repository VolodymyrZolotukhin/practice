package MessageOperators;


import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MessageManger {
    public static final String ATTRIBUTE_NAME = "MessageManager";
    private static final String URL_PUSH = "http://localhost:8080/receive/test";
    private static final String URL_SMS = "http://localhost:8080/receive/test";
    private static final String URL_EMAIL = "http://localhost:8080/receive/test";

    private ServletContext context;
    private LinkedList<Message> pushes;
    private LinkedList<Message> SMSs;
    private LinkedList<Message> emails;
    private Thread[] threads;
    private handleRunnable[] runnables;
    StatusChecker pchecker;
    StatusChecker schecker;
    StatusChecker echecker;

    public MessageManger(ServletContext context){
        pushes = new LinkedList<Message>();
        SMSs = new LinkedList<Message>();
        emails = new LinkedList<Message>();
        threads = new Thread[6];
        runnables = new handleRunnable[6];
        this.context = context;
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
                runnables[0].notifyMutex();
                break;

            case SMS:
                SMSs.addLast(message);
                runnables[1].notifyMutex();
                break;

            case EMAIL:
                emails.addLast(message);
                runnables[2].notifyMutex();
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
        pchecker = new StatusChecker(this,Priority.PUSH,URL_PUSH);
        schecker = new StatusChecker(this,Priority.SMS,URL_SMS);
        echecker = new StatusChecker(this,Priority.EMAIL,URL_EMAIL);

        runnables[3] = new RequestThread(pchecker);
        runnables[4] = new RequestThread(schecker);
        runnables[5] = new RequestThread(echecker);

        threads[3] = new Thread(runnables[3]);
        threads[4] = new Thread(runnables[4]);
        threads[5] = new Thread(runnables[5]);

        MessageSender push = new MessageSender(URL_PUSH, pushes, Priority.PUSH,this);
        MessageSender sms = new MessageSender(URL_SMS, SMSs, Priority.SMS,this);
        MessageSender email = new MessageSender(URL_EMAIL, emails, Priority.EMAIL,this);

        runnables[0] = new SenderThread(push);
        runnables[1] = new SenderThread(sms);
        runnables[2] = new SenderThread(email);

        threads[0] = new Thread(runnables[0]);
        threads[1] = new Thread(runnables[1]);
        threads[2] = new Thread(runnables[2]);

        for (Thread th:
                threads) {
            th.start();
        }
    }

    public void interruptThreads() {
        for (handleRunnable r: runnables){
            r.stop();
        }

        for (Thread th: threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ServletContext getContext() {
        return context;
    }

    public void pushToChecker(String id, Message message, Priority priority){
        switch (priority){
            case PUSH:
                pchecker.addMessage(id, message);
                break;
            case SMS:
                schecker.addMessage(id, message);
                break;
            case EMAIL:
                echecker.addMessage(id, message);
                break;
        }
    }
}
