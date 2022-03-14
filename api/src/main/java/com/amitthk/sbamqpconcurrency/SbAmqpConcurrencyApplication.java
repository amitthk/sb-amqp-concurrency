package com.amitthk.sbamqpconcurrency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocket
@EnableWebSocketMessageBroker
public class SbAmqpConcurrencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbAmqpConcurrencyApplication.class, args);
	}

}
