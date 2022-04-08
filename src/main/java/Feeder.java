import java.sql.Timestamp;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public final class Feeder{
    private Map<String, Queue<String>> map;

    public Feeder(Map<String, Queue<String>> map){
        this.map = map;
    }

    private void addMessage(){
        synchronized (map) {
            map.forEach((k, v) ->
                    v.add("message @ " + new Timestamp(System.currentTimeMillis())));
            map.notifyAll();
        }
    }

    public void run() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::addMessage,
                0,
                5,
                TimeUnit.SECONDS
        );
    }
}
