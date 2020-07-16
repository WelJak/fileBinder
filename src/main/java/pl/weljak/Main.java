package pl.weljak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.weljak.filebinder.FileBinder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting...");
        final String currentPath = System.getProperty("user.dir");
        Path path = Paths.get(currentPath);
        log.info("Binding files in path: {}", currentPath);
        FileBinder.bindFiles(path);
    }
}
