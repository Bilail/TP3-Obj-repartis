import java.sql.Timestamp;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Feeder {
    private Map<String, Queue<String>> map;

    public Feeder(Map<String, Queue<String>> map){
        this.map = map;
    }

    public void getNews(){
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(
                () -> {
                        String msg = "message" + new Timestamp(System.currentTimeMillis());
                        map.forEach((k, v) -> v.add(msg));
                        System.out.println(msg);
                    },
                0,
                5,
                TimeUnit.SECONDS
                );
    }
}
