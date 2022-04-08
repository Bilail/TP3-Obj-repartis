import fr.polytech.grpc.proto.Service.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import fr.polytech.grpc.proto.NewsletterGrpc;
import fr.polytech.grpc.proto.NewsletterGrpc.NewsletterStub;


import java.util.*;


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


    private StreamObserver<Reply> replyObserver = new StreamObserver<>() {

        @Override
        public void onNext(Reply value) {
            System.out.println(value.getMsg());
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onCompleted() {
            System.out.println("Stream Has Ended.");
        }
    };

    public void getData(String name) {
        Request request = Request.newBuilder().setName(name).build();
        asyncStub.getData(request, replyObserver);
    }

    public void subscribe(String name){
        Request request = Request.newBuilder().setName(name).build();
        Reply reply = blockingStub.subscribe(request);

        System.out.println(reply.getMsg());
    }

    public void unsubscribe(String name){
        Request request = Request.newBuilder().setName(name).build();
        Reply reply = blockingStub.unsubscribe(request);

        System.out.println(reply.getMsg());
    }


    public static void main(String[] args) throws InterruptedException {

        Client client = new Client("localhost", 1664);

        String name = "Bilail";
        // Abonnement
        client.subscribe(name);

        // Read Data
        client.getData(name);

        Thread.sleep(10000);

        //Désinscription
        client.unsubscribe(name);

    }

}
