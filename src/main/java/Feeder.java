import java.sql.Timestamp;
import java.util.Map;
import java.util.Queue;


public final class Feeder implements Runnable{
    private Map<String, Queue<String>> map;

    public Feeder(Map<String, Queue<String>> map){
        this.map = map;
    }

    @Override
    public void run() {
        while (true) {
            try{
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {}
            synchronized (map) {
                map.forEach((k, v) ->
                        v.add("message @ " + new Timestamp(System.currentTimeMillis())));
                map.notifyAll();
            }
        }
    }
}
