package MessageOperators;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class StatusChecker {

    private volatile Map<String, Message> messages;
    private volatile MessageManger messageManger;
    private Priority priority;
    private String url;


    public StatusChecker(MessageManger messageManger, Priority priority, String url, Map<String , Message> map){
        this.priority = priority;
        this.url = url;
        messages = (new HashMap<String, Message>());
        this.messageManger = messageManger;
        this.messages = map;
    }

    public synchronized Status checkStatus(String jsonString) throws UnsupportedEncodingException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("json",jsonString));
        post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));

        try {
            HttpResponse response = httpClient.execute(post);
            InputStream is = response.getEntity().getContent();
            int size = is.available();
            byte [] bytes = new byte[size];
            is.read(bytes);
            String string = new String(bytes,"utf-8");
            JSONObject jsonObject = new JSONObject(string);
            if (jsonObject.getString("status").equals(Status.ACCEPTED.toString())){
                return Status.ACCEPTED;
            }else if (jsonObject.getString("status").equals(Status.DELIVERED.toString())){
                return Status.DELIVERED;
            }else {
                return Status.REJECTED;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Status.ERROR;
    }

    public synchronized void removeMessage(String id){
        messages.remove(id);
    }

    public synchronized void sendAgain(String id){
        if (messages.get(id).getOutOfDate().getTime().after(new Date())) {
            switch (priority){
                case PUSH:
                    SettingsContextManager.getInstance(messageManger.getContext()).addPushes_redir();
                    break;
                case SMS:
                    SettingsContextManager.getInstance(messageManger.getContext()).addSMSs_redir();
                    break;
            }
            messageManger.sendAgain(messages.get(id), priority);
        }else {
            SettingsContextManager.getInstance(messageManger.getContext()).addSkipped_by_ttl();
        }
        removeMessage(id);
    }

    public synchronized void checkAgain(String id){
        if (messages.get(id).getOutOfDate().getTime().before(new Date())) {
            removeMessage(id);
            SettingsContextManager.getInstance(messageManger.getContext()).addSkipped_by_ttl();
        }
    }

    public synchronized MessageManger getMessageManger() {
        return messageManger;
    }


}
