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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MessageSender {
    private LinkedList<Message> queue;
    private String  url;
    private Priority priority;
    private MessageManger manger;

    public MessageSender(String url, LinkedList<Message> queue, Priority priority,MessageManger manger){
        this.queue = queue;
        this.url = url;
        this.priority = priority;
        this.manger = manger;
    }

    public synchronized Message tryPop(){
        return queue.pollFirst();
    }

    public synchronized void send(Message message) throws UnsupportedEncodingException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        JSONObject json = new JSONObject();
        json.put("text",message.getText());

        switch (priority){
            case PUSH:
                json.put("address",message.getToken());
                SettingsContextManager.getInstance(manger.getContext()).addPushes();
                break;
            case SMS:
                json.put("address",message.getPhone());
                SettingsContextManager.getInstance(manger.getContext()).addSMSs();
                break;
            case EMAIL:
                json.put("address",message.getEmail());
                SettingsContextManager.getInstance(manger.getContext()).addEmails();
                break;
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("json",json.toString()));
        post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
        try {
            HttpResponse response = httpClient.execute(post);
            InputStream is = response.getEntity().getContent();
            int size = is.available();
            byte [] bytes = new byte[size];
            is.read(bytes);
            String string = new String(bytes,"utf-8");
            JSONObject jsonObject = new JSONObject(string);
            if (jsonObject.getString("status").equals("accepted")){
                //System.out.println("\n--- Start Checker ---\n");
                manger.pushToChecker(jsonObject.getString("id"),message,priority);
            }else{
                switch (priority){
                    case PUSH:
                        SettingsContextManager.getInstance(manger.getContext()).addPushes_redir();
                        break;
                    case SMS:
                        SettingsContextManager.getInstance(manger.getContext()).addSMSs_redir();
                        break;
                }
                manger.sendAgain(message,priority);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
