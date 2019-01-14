package com.jit.skiad;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Slf4j
@MapperScan("com.jit.skiad.mapper")
public class SkiadApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkiadApplication.class, args);
		log.info("skiad application is running.........");

	}

}
