package pl.weljak.countwirter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;


public class CountWriter {
    private final String file;
    private static Logger log = LoggerFactory.getLogger(CountWriter.class);

    public CountWriter(String file) {
        this.file = file;
    }

    public void writeToFile(int jarsMovedToDev, int jarsMovedToTest, int xmlsMovedToDev) throws IOException {
        log.info("Writing to file: {}", this.file);
        FileWriter fileWriter = new FileWriter(this.file);
        String toWrite = "Jar files moved to DEV dir: " +
                jarsMovedToDev +
                "\n" +
                "Jar files moved to TEST dir: " +
                jarsMovedToTest +
                "\n" +
                "XML files moved to DEV dir: " +
                xmlsMovedToDev;
        fileWriter.write(toWrite);
        fileWriter.close();
    }
}
