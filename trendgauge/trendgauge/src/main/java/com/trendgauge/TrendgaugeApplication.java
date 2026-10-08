package com.trendgauge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrendgaugeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrendgaugeApplication.class, args);
	}

}
