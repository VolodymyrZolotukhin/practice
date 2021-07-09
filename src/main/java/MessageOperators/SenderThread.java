package MessageOperators;

import java.io.UnsupportedEncodingException;

public class SenderThread implements handleRunnable {
    private MessageSender sender;
    private final Object mutex = new Object();
    private boolean running;

    public SenderThread(MessageSender sender){
        this.sender = sender;
        running = true;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Message message = sender.tryPop();
                if (message != null) {
                    try {
                        sender.send(message);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    synchronized (mutex) {
                        mutex.wait();
                    }
                }
            }
        } catch (InterruptedException ignored) {

        }

    }

    @Override
    public void stop() {
        synchronized (mutex){
            running = false;
            mutex.notifyAll();
        }
    }

    public void notifyMutex(){
        synchronized (mutex) {
            mutex.notifyAll();
        }
    }
}
