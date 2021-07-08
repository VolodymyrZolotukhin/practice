package MessageOperators;

import java.io.UnsupportedEncodingException;

public class SenderThread extends Thread{
    private MessageSender sender;

    public SenderThread(MessageSender sender){
        super();
        this.sender = sender;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Message message = sender.tryPop();
                if (message != null) {
                    if (!isInterrupted()) {
                        try {
                            sender.send(message);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    synchronized (this) {
                        wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            interrupt();
        }

    }

}
