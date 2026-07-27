package com.example.LeetDeCode_Battle_Module;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class LeetDeCodeBattleModuleApplication {

	@Bean
	CommandLineRunner testRedisConnection(RedisTemplate<String, Object> redisTemplate) {
		return args -> {
			redisTemplate.opsForValue().set("test:startup", "connected");
			Object result = redisTemplate.opsForValue().get("test:startup");
			System.out.println("Redis connectivity check -> " + result);
			redisTemplate.delete("test:startup");
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(LeetDeCodeBattleModuleApplication.class, args);
	}

}
