package gym.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Append-only audit trail of who did what and when. */
public class ActivityLog {
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path file;

    public ActivityLog(Path file) {
        this.file = file;
    }

    public void record(String username, String action) {
        String line = LocalDateTime.now().format(STAMP) + " | " + username + " | " + action;
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("  Warning: could not write to the activity log: " + e.getMessage());
        }
    }

    public List<String> recent(int count) throws IOException {
        if (!Files.exists(file)) return List.of();
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        return lines.subList(Math.max(0, lines.size() - count), lines.size());
    }
}
