package MessageOperators;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Message {
    private String notification_id;
    private String text;
    private int ttl;
    private Date time;
    private String phone;
    private String email;
    private String token;
    private Calendar outOfDate;

    public Message(JSONObject json) throws JSONFormatException {
        this.notification_id = json.getString("notification_id");
        this.text = json.getString("text");
        this.ttl = json.getInt("ttl");
        try {
            this.time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(json.getString("time"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject clnt_ids = json.getJSONObject("clnt_ids");
        if (notification_id == null || text == null || time == null || clnt_ids == null){
            throw new JSONFormatException();
        }
        this.phone = clnt_ids.getString("phone");
        this.token = clnt_ids.getString("token");
        this.email = clnt_ids.getString("email");
        if((this.token == null && this.phone == null && this.email == null)
                ||(this.token.equals("") && this.phone.equals("") && this.email.equals(""))){
            throw new JSONFormatException();
        }
        outOfDate = new GregorianCalendar();
        outOfDate.setTime(time);
        outOfDate.add(Calendar.SECOND,ttl);

       /* System.out.println("\n --- received "+time+" --- ");
        System.out.println(" --- out "+outOfDate.getTime()+" --- ");
        System.out.println(" --- "+new Date()+" --- ");
        System.out.println(" --- if out "+getOutOfDate().after(new Date())+" --- \n");*/
    }

    public String getNotification_id() {
        return notification_id;
    }

    public String getText() {
        return text;
    }

    public Date getTime() {
        return time;
    }

    public int getTtl() {
        return ttl;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getToken() {
        return token;
    }

    public Calendar getOutOfDate() {
        return outOfDate;
    }
}
