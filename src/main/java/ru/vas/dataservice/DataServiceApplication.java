package ru.vas.dataservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.db.repo.BlockedResourceRepository;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "ru.vas.dataservice.db.repo")
public class DataServiceApplication implements CommandLineRunner {

    @Autowired
    private BlockedResourceRepository resourceRepository;

    public static void main(String[] args) {
        SpringApplication.run(DataServiceApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println();
    }
}
