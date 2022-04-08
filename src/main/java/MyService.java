import fr.polytech.grpc.proto.NewsletterGrpc;
import fr.polytech.grpc.proto.Service.*;

import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MyService extends NewsletterGrpc.NewsletterImplBase {

    Map<String, Queue<String>> Bdd = new HashMap<>(); // On stocke le nom des clients avec leurs messages accumulés
    Feeder feeder = new Feeder(Bdd);

    public MyService() {
        new Thread(feeder).start();
    }

    /**
     * Fonction d'abonnement, elle ajoute le client si il ne l'etait pas deja
     * dans la Bdd
     * @param request le nom du client
     * @param responseObserver
     */
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

    /**
     * Fonction de desabonnement, elle supprime le client si il etait abonne de la bdd
     * @param request le nom du client
     * @param responseObserver
     */
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

    /**
     * Recuperation du flux de message attache au client
     * @param request
     * @param responseObserver
     */
    @Override
    public void getData(Request request, StreamObserver<Reply> responseObserver) {
        String name = request.getName();
        Reply.Builder reply = Reply.newBuilder();
        synchronized (Bdd) { // on pose un verrou sur la Bdd pour eviter les modification concurrents
            while (true) {
                try {
                    Bdd.wait(); // Attente de nouveau message
                } catch (InterruptedException ignored) {}

                if (Bdd.containsKey(name)) { // on verifie si le client est abonne
                    while (!Bdd.get(name).isEmpty()) { // On verifie si il a des messages non lus
                        reply.setMsg(Bdd.get(name).poll());
                        responseObserver.onNext(reply.build());
                    }
                } else {
                    break; // sinon on sort de la boucle car client plus abonne
                }
            }
        }

        responseObserver.onCompleted();
    }

}
