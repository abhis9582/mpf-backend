package com.mypropertyfact.estate.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;

@Component
public class ImageIOInitializer {

    @PostConstruct
    public void initImageIO() {
        ImageIO.scanForPlugins();
        System.out.println("ImageIO plugins scanned.");
    }
}
