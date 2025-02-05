package com.hivemq.client.test;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

public class ClientTest {
    public static void main(String[] args) {
        // Client to connetct to HiveMQ
        Mqtt3AsyncClient client = MqttClient.builder()
                .useMqttVersion3()
                .serverHost("localhost")
                .serverPort(1883)
                .buildAsync();

        // connect to HiveMQ

        client.connectWith()
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Connection failed." + throwable.getMessage());
                        return;
                    }
                    System.out.println("Connection successful.");

                    // Subscribe and receive message
                    client.subscribeWith()
                            .topicFilter("test/topic")
                            .qos(MqttQos.AT_LEAST_ONCE)
                            .callback(publish -> {
                                System.out.println("Received message: " + new String(publish.getPayloadAsBytes()));
                            })
                            .send();

                    // Send message
                    client.publishWith()
                            .topic("test/topic")
                            .qos(MqttQos.AT_LEAST_ONCE)
                            .payload("Hello from HiveMQ Client.".getBytes())
                            .send()
                            .whenComplete((pubAck, pubThrowable) -> {
                                if (pubThrowable != null) {
                                    System.out.println("Failed sending message." + pubThrowable.getMessage());
                                } else {
                                    System.out.println("Message successfully sent.");
                                }
                            });

                });
    }

}