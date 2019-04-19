package util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReader {
    public List<String> readLines(String resourceName) throws IOException {
        try(Stream<String> streamLinks = Files.lines(getResPath(resourceName))) {
            return streamLinks.collect(Collectors.toList());
        } catch (URISyntaxException | IOException e) {
            throw new IOException(e);
        }
    }

    private Path getResPath(String name) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(name).toURI());
    }
}
