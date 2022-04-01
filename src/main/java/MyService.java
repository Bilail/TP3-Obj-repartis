import fr.polytech.grpc.proto.ProtoGrpc;
import fr.polytech.grpc.proto.Service.*;

import io.grpc.stub.StreamObserver;

import java.util.Map;


public class MyService extends ProtoGrpc.ProtoImplBase {

    public MyService() {
    }

    @Override
    public void handle(Request input, StreamObserver<Reply> responseObserver) {
        Info info = input.getInfo();
        Map<String, Data> dataMap = input.getDataMap();

        // Use a StringBuilder to shape the output
        StringBuilder stb = new StringBuilder("\n=====RECEIVED MESSAGE FROM CLIENT=====\n");
        stb.append("Sender: ").append(info.getSender()).append("\n");
        stb.append("Timestamp: ").append(info.getTimestamp()).append("\n");
        stb.append("ID: ").append(info.getId()).append("\n");
        stb.append("\n");
        stb.append("---DECODED DATA MAP---").append("\n");
        stb.append(handleDataMap(dataMap)).append("\n");

        // Print the output on the Server side
        System.out.println("MyService " + this + "  " + stb);

        long now = System.currentTimeMillis()/1000L;

        // Start shaping server response to client
        Reply.Builder replyBuilder = Reply.newBuilder()
                .setInfo(Info.newBuilder().setSender("Server").setTimestamp(now).setId(1))
                .setHandled(dataMap.size());

        // Handle the oneof result field
        // If no data was given an error message is sent
        // else the HTTP OK 400 status is sent
        if (dataMap.size() == 0){
            replyBuilder.setMessage("ERROR: NO DATA GIVEN");
        } else{
            replyBuilder.setStatus(400); // OK HTTP
        }

        // Build and ship the reply to the client side
        responseObserver.onNext(replyBuilder.build());
        responseObserver.onCompleted();
    }

    /**
     * Utility method to transform a dataMap to a String
     * @param dataMap<String, Data> </String,>
     * @return shaped string of the given Map
     */
    private String handleDataMap(Map<String, Data> dataMap) {
        StringBuilder stb = new StringBuilder();
        for (Map.Entry<String, Data> entry : dataMap.entrySet()) {
            Data data = entry.getValue();
            stb.append(entry.getKey()).append(": ").append(data.getData1())
                    .append(", ").append(data.getData2())
                    .append(", ").append(data.getData3List()).append("\n");
        }

        return stb.toString();
    }

}
