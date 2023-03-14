package com.rakib.testcodeblock;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestCodeBlockApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TestCodeBlockApplication.class, args);
    }

    @Override
    public void run(String... args) {
       // VirtualThread.virtualThreadDemo();
    }


}
