package com.phirom_02.blog_api.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

//    private final PostDataLoader postDataLoader;
//    private final UserDataLoader userDataLoader;
//    private final TagDataLoader tagDataLoader;
//    private final CategoryDataLoader categoryDataLoader;

    @Override
    public void run(String... args) {
        System.out.println("⏳ Loading seed data...");

//        tagDataLoader.load();
//        categoryDataLoader.load();
//        userDataLoader.load();
//        postDataLoader.load();

        System.out.println("✅ Data loaded successfully.");
    }
}
