import fr.polytech.grpc.proto.Service.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import fr.polytech.grpc.proto.NewsletterGrpc;
import fr.polytech.grpc.proto.NewsletterGrpc.NewsletterBlockingStub;
import fr.polytech.grpc.proto.NewsletterGrpc.NewsletterStub;


import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Client {

    private ManagedChannel channel;
    protected NewsletterGrpc.NewsletterBlockingStub blockingStub;
    private NewsletterStub asyncStub;

    public Client(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public Client(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = NewsletterGrpc.newBlockingStub(channel);
        asyncStub = NewsletterGrpc.newStub(channel);
    }


    private StreamObserver<Reply> replyObserver1 = new StreamObserver<>() {
        private List<String> msg = new ArrayList<>();

        @Override
        public void onNext(Reply value) {
            msg.add(value.getMsg());
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onCompleted() {
            msg.forEach(System.out::println);
        }
    };

    public Stream<String> readData (String name) {
        Request request = Request.newBuilder().setName(name).build();
        Iterator<Reply> it = blockingStub.readData(request);
        Iterable<Reply> iterable = () -> it;
        return StreamSupport.stream(iterable.spliterator(), false).map(reply -> reply.getMsg());
    }

    public void subscribe(String name){
        Request request = Request.newBuilder().setName(name).build();
        Reply reply = blockingStub.subscribe(request);

        System.out.println(" Bienvenue nouveau Abonnée");
    }

    public void unsubscribe(String name){
        Request request = Request.newBuilder().setName(name).build();
        Reply reply = blockingStub.unsubscribe(request);

        System.out.println(" Au revoir ex-Abonnée");
    }



    public static void main(String[] args) {

        Client client = new Client("localhost", 1664);

        // Abonnement
        client.subscribe("Bilail");

        // Read Data
        client.readData("Bilail").forEach(System.out::println);

        //Désinscription
        client.unsubscribe("Bilail");

    }

}
