import java.io.IOException;
import java.util.Scanner;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class MyServer {

    private Server server;

    public MyServer(int port) {
        this.server = ServerBuilder
                .forPort(port)
                .addService(new MyService())
                .build();
    }

    public void start() throws IOException {
        server.start();
    }

    public void shutdown() {
        server.shutdown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MyServer server = new MyServer(1664);
        server.start();
        System.out.println("Serveur OK");
        new Scanner(System.in).next();
        server.shutdown();
    }

}
