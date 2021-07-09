package MessageOperators;

import javax.servlet.ServletContext;


public class SettingsContextManager {

    public static final String ATTRIBUTE_NAME = "SettingsContext";
    private volatile long received = 0;
    private volatile long with_error = 0;
    private volatile long skipped = 0;
    private volatile long pushes = 0;
    private volatile long pushes_redir = 0;
    private volatile long SMSs = 0;
    private volatile long SMSs_redir = 0;
    private volatile long emails = 0;
    private volatile long skipped_by_ttl = 0;
    private volatile long total = 0;

    public SettingsContextManager(){}

    public static synchronized SettingsContextManager getInstance(ServletContext context){
        return (SettingsContextManager) context.getAttribute(ATTRIBUTE_NAME);
    }

    public synchronized void addReceived(){
        received++;
    }
    public synchronized void addWith_error(){
        with_error++;
    }
    public synchronized void addSkipped(){
        skipped++;
    }
    public synchronized void addPushes(){
        pushes++;
    }
    public synchronized void addPushes_redir(){
        pushes_redir++;
    }
    public synchronized void addSMSs(){
        SMSs++;
    }
    public synchronized void addSMSs_redir(){
        SMSs_redir++;
    }
    public synchronized void addEmails(){
        emails++;
    }
    public synchronized void addTotal(){total++;}
    public synchronized void addSkipped_by_ttl(){
        skipped_by_ttl++;
    }

    public synchronized long getReceived() {
        return received;
    }
    public synchronized long getWith_error() {
        return with_error;
    }
    public synchronized long getSkipped() {
        return skipped;
    }
    public synchronized long getPushes() {
        return pushes;
    }
    public synchronized long getPushes_redir() {
        return pushes_redir;
    }
    public synchronized long getSMSs() {
        return SMSs;
    }
    public synchronized long getSMSs_redir() {
        return SMSs_redir;
    }
    public synchronized long getEmails() {
        return emails;
    }
    public synchronized long getSkipped_by_ttl() {
        return skipped_by_ttl;
    }
    public synchronized long getTotal(){
        return total;
    }
}