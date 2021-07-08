package MessageOperators;

import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TimerTask;

public class RequestTimerTask extends TimerTask {
    private Map<String, Message> ids;
    private StatusChecker checker;

    public RequestTimerTask(Map<String, Message> ids, StatusChecker checker){
        this.ids = ids;
        this.checker = checker;
    }

    private void completeTask(){
        for (String id: ids.keySet()) {
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
    }

    @Override
    public void run() {
        completeTask();
    }
}
