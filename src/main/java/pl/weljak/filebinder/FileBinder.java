package pl.weljak.filebinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.weljak.countwirter.CountWriter;
import pl.weljak.directorycreator.DirectoryCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class FileBinder {
    private static final DirectoryCreator directoryCreator = new DirectoryCreator();
    private static final String countFileName = "count.txt";
    private static Logger log = LoggerFactory.getLogger(FileBinder.class);


    public static void bindFiles(final Path path) {
        FileSystem fileSystem = path.getFileSystem();
        WatchKey watchKey;
        int jarsMovedToDevCount = 0;
        int jarsMovedToTestCount = 0;
        int xmlsMovedToDvCount = 0;

        try (WatchService watchService = fileSystem.newWatchService()) {
            if (!checkIfDirectoryIsAFolder(path)) {
                throw new IllegalArgumentException("Given path is not a folder");
            }
            final Path HOME = path.resolve("HOME");
            final Path DEV = path.resolve("DEV");
            final Path TEST = path.resolve("TEST");

            final CountWriter countWriter = new CountWriter(String.valueOf(HOME.resolve(countFileName)));

            log.info("Creating directories: {}, {}, {} ", HOME, DEV, TEST);

            directoryCreator.createDirectory(HOME);
            directoryCreator.createDirectory(DEV);
            directoryCreator.createDirectory(TEST);

            HOME.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            do {
                watchKey = watchService.take();

                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    WatchEvent.Kind<?> eventKind = watchEvent.kind();
                    if (eventKind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                        Path createdFilePath = (Path) watchEvent.context();
                        final Path resolvedCreatedFilePath = HOME.resolve(createdFilePath);
                        log.info("New path created: {}", createdFilePath);
                        final String fileExtension = getFileExtension(createdFilePath.toFile());

                        switch (fileExtension) {
                            case "jar":
                                log.info("Processing file with .jar extension");
                                Path fullJarFilePath = resolvedCreatedFilePath.toAbsolutePath();
                                BasicFileAttributes jarFileAttributes = Files.readAttributes(fullJarFilePath, BasicFileAttributes.class);
                                LocalDateTime jarFileCreationTime = jarFileAttributes.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                                log.info("Jar file created at: {}", jarFileCreationTime);
                                if (jarFileCreationTime.getHour() % 2 == 0) {
                                    log.info("Moving .jar file to DEV directory");
                                    Files.move(resolvedCreatedFilePath, DEV.resolve(createdFilePath));
                                    ++jarsMovedToDevCount;
                                } else {
                                    log.info("Moving .jar file to TEST directory");
                                    Files.move(resolvedCreatedFilePath, TEST.resolve(createdFilePath));
                                    ++jarsMovedToTestCount;
                                }
                                break;
                            case "xml":
                                log.info("Processing file with .xml extension");
                                log.info("Moving .xml file to DEV directory");
                                Files.move(resolvedCreatedFilePath, DEV.resolve(createdFilePath));
                                ++xmlsMovedToDvCount;
                                break;
                        }
                    }
                    countWriter.writeToFile(jarsMovedToDevCount, jarsMovedToTestCount, xmlsMovedToDvCount);
                }
            } while (watchKey.reset());


        } catch (IOException | InterruptedException exception) {
            log.info("Error occurred during binding files");
            exception.printStackTrace();
        }
    }

    private static Boolean checkIfDirectoryIsAFolder(Path path) throws IOException {
        return (Boolean) Files.getAttribute(path, "basic:isDirectory", LinkOption.NOFOLLOW_LINKS);
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

}
