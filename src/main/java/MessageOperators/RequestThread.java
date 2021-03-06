package MessageOperators;

import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

public class RequestThread implements handleRunnable {
    private StatusChecker checker;
    private final Object mutex = new Object();
    private boolean running;
    private Map<String , Message> map;

    public RequestThread(StatusChecker checker, Map<String , Message> map){
        this.checker = checker;
        running = true;
        this.map = map;
    }

    private void completeTask(String id){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message_id", id);
            try {
                Status status = checker.checkStatus(jsonObject.toString());
                switch (status) {
                    case ACCEPTED:
                        //System.out.println("\n --- checker.checkAgain ---\n");
                        checker.checkAgain(id);
                        break;
                    case REJECTED:
                        //System.out.println("\n --- checker.sendAgain --- \n");
                        checker.sendAgain(id);
                        break;
                    case DELIVERED:
                        //System.out.println("\n --- checker.removeMessage --- \n");
                        SettingsContextManager.getInstance(checker.getMessageManger().getContext()).addTotal();
                        checker.removeMessage(id);
                        break;
                }
            } catch (UnsupportedEncodingException e) {
                System.out.println("\n");
                e.printStackTrace();
                System.out.println("\n");
            }

    }

    @Override
    public void run() {
        while (running) {
            synchronized (mutex) {
                try {
                    long l = (Long) checker.getMessageManger().getContext().getAttribute("delay");
                    mutex.wait(l);
                } catch (InterruptedException ignored) {

                }
            }
            Set<String> ids = map.keySet();
            //System.out.println("\n --- "+ids.size()+" "+this.toString()+" --- \n");
                for (String st : ids) {
                    completeTask(st);
                    //ids.remove(st);
                }
        }
    }

    public void stop(){
        synchronized (mutex){
            running = false;
            mutex.notifyAll();
        }
    }

    @Override
    public void notifyMutex() {
        synchronized (mutex) {
            mutex.notifyAll();
        }
    }
}
