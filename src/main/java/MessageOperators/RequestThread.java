package MessageOperators;

import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

public class RequestThread extends Thread {
    private StatusChecker checker;

    public RequestThread( StatusChecker checker){
        super();
        this.checker = checker;
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
            while (!isInterrupted()) {
                Set<String> ids = checker.getKeys();
                    for (String st : ids) {
                        if (!isInterrupted()) {
                            completeTask(st);
                        }
                    }

                synchronized (this) {
                    try {
                        long l = (Long) checker.getMessageManger().getContext().getAttribute("delay");
                        wait(l);
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }
            }
    }
}
