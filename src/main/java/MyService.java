import fr.polytech.grpc.proto.NewsletterGrpc;
import fr.polytech.grpc.proto.ProtoGrpc;
import fr.polytech.grpc.proto.Service.*;

import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;


public class MyService extends NewsletterGrpc.NewsletterImplBase {

    public MyService() {
    }

    Map<String, ArrayList<String>> Bdd;

    @Override
    public void subscribe(Request request, StreamObserver<Reply> responseObserver){
        String name = request.getName();
        System.out.println("Nouveau Abonnée : " + name);
        Reply.Builder reply = Reply.newBuilder();
        if (Bdd.containsKey(name)){
            System.out.println(" ERROR : " + name+ " est déja inscrit");
        }
        else {
            Bdd.put(name, new ArrayList<String>());
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void unsubscribe(Request request, StreamObserver<Reply> responseObserver){
        String name = request.getName();
        System.out.println(name+ " est desabonne");
        Reply.Builder reply = Reply.newBuilder();
        if (Bdd.containsKey(name)){
            Bdd.remove(name);
        }
        else {
            System.out.println(" ERROR : " + name+ " n'est pas inscrit");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ReadData(Request request, StreamObserver<Reply> responseObserver){
        String name = request.getName();
        if (!Bdd.containsKey(name)){
            System.out.println(" ERROR : " + name+ " n'est pas inscrit");
        }
        else {
            System.out.println("INFO : Début de vos messages");
            while(Bdd.containsKey(name)) {
                Reply reply = Reply.newBuilder().setMsg(Bdd.get(name).poll()).build()
            }
        }


}
