package com.mypropertyfact.estate.configs;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;

@Component
@Slf4j
public class ImageIOInitializer {

    @PostConstruct
    public void initImageIO() {
        ImageIO.scanForPlugins();
        log.info("ImageIO plugins scanned.");
    }
}
