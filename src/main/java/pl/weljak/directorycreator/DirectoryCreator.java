package pl.weljak.directorycreator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;


public class DirectoryCreator {
    private static Logger log = LoggerFactory.getLogger(DirectoryCreator.class);

    public void createDirectory(Path path) throws IOException {
        log.info("Starting creating new directory:{}", path);
        Objects.requireNonNull(path);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
            log.info("Created new directory at: {}", path.toAbsolutePath());
        } else {
            log.info("Directory {} already exists", path.toAbsolutePath());
        }
    }
}
