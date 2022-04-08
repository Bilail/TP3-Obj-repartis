import fr.polytech.grpc.proto.NewsletterGrpc;
import fr.polytech.grpc.proto.Service.*;

import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MyService extends NewsletterGrpc.NewsletterImplBase {



    Map<String, Queue<String>> Bdd = new HashMap<>();
    Feeder feeder = new Feeder(Bdd);

    public MyService() {
        new Thread(feeder).start();
    }

    @Override
    public void subscribe(Request request, StreamObserver<Reply> responseObserver){
        String name = request.getName();
        Reply.Builder reply = Reply.newBuilder();
        synchronized (Bdd) {
            if (Bdd.containsKey(name)) {
                System.out.println(" ERROR : " + name + " est déja inscrit");
                reply.setMsg(" ERROR : " + name + " est déja inscrit");
            } else {

                System.out.println("Nouveau Abonnée : " + name);
                Bdd.put(name, new LinkedList<>());
                reply.setMsg("Nouveau Abonnée : " + name);
            }
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void unsubscribe(Request request, StreamObserver<Reply> responseObserver) {
        String name = request.getName();
        Reply.Builder reply = Reply.newBuilder();
        synchronized (Bdd) {
            if (Bdd.containsKey(name)) {

                Bdd.remove(name);
                System.out.println(name + " est desabonne");
                reply.setMsg("L'utiliseur  " + name + " est désabonné.");
            } else {
                System.out.println(" ERROR : " + name + " n'est pas inscrit");
                reply.setMsg(" ERROR : " + name + " n'est pas inscrit");
            }
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getData(Request request, StreamObserver<Reply> responseObserver) {
        String name = request.getName();
        Reply.Builder reply = Reply.newBuilder();
        synchronized (Bdd) {
            while (true) {
                try {
                    Bdd.wait();
                } catch (InterruptedException ignored) {}

                if (Bdd.containsKey(name)) {
                    while (!Bdd.get(name).isEmpty()) {
                        reply.setMsg(Bdd.get(name).poll());
                        responseObserver.onNext(reply.build());
                    }
                } else {
                    break;
                }
            }
        }

        responseObserver.onCompleted();
    }

}
