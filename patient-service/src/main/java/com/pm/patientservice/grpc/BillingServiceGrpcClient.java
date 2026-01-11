package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;


    //localhost:9001/BillingService/CreatePatientAccount  in local
    //aws.grpc:123432/BillingService/CreatePatientAccount  in production


    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,  //ye bta raha Billing-service kaha chal rahi hai
            @Value("${billing.service.grpc.port:9001}") int serverPort
    ){
        log.info("Connecting to billing service GRPC service at{}:{}",serverAddress,serverPort);

        //1. ManagedChannel (The Wire/Pipe)
        //Yeh ek abstraction hai jo client (Patient-Service) aur server (Billing-Service) ke beech ka connection handle karta hai
        //Yeh connection ko Zinda (Alive) rakhta hai.
        //Agar connection toot jaye, toh yeh use Reconnect karne ki koshish karta hai.

        //2. ManagedChannelBuilder.forAddress(serverAddress, serverPort)
        //Yahan aap batate hain ki aapko kahan connect hona hai.
        //Address: Jaise localhost ya AWS ka koi IP address.
        //Port: Jaise 9001.

        //3. .usePlaintext() (No Security) real world me SSL use hota security ke liye but since dev environment
        //4. .build()
        //Yeh saari settings (address, port, security) ko final karke connection Start kar deta hai.

        ManagedChannel channel= ManagedChannelBuilder.forAddress(serverAddress,serverPort).usePlaintext().build();

        blockingStub=BillingServiceGrpc.newBlockingStub(channel);

        //Channel: Connection provide karta hai.
        //
        //Stub: Us connection ke upar functions call karne ki power deta hai.
    }

    public BillingResponse createBillingAccount(String patientId,String name,String email){

        //newBuilder ki help se data(patientId,name,email) fill kar re jo .proto file se ara

        BillingRequest request=BillingRequest.newBuilder().setPatientId(patientId).setName(name).setEmail(email).build();

        BillingResponse response=blockingStub.createBillingAccount(request);

        log.info("Received response from billing service via GRPC : {}",response);

        return response;
    }
}
