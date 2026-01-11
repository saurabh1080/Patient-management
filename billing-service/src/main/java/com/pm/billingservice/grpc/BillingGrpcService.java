package com.pm.billingservice.grpc;

import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest, StreamObserver<billing.BillingResponse> responseObserver){

       log.info("crateBillingAccount request received{}",billingRequest.toString());

       // BusinessLogic e.g- save to database,perform calculates

        billing.BillingResponse response= billing.BillingResponse.newBuilder()
                .setAccountId("12345")
                .setStatus("active")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
