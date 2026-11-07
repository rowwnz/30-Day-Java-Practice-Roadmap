package gym.storage;

import gym.model.CheckIn;
import gym.model.FitnessClass;
import gym.model.Member;
import gym.model.Payment;
import gym.model.Storable;
import gym.model.Trainer;
import gym.model.User;
import gym.util.Csv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Saves each kind of record to its own CSV file inside the data folder.
 * Plain text was chosen over serialization so the files can be opened in Excel or Notepad.
 */
public class FileStorage {
    private static final String USERS = "users.csv";
    private static final String MEMBERS = "members.csv";
    private static final String TRAINERS = "trainers.csv";
    private static final String CLASSES = "classes.csv";
    private static final String PAYMENTS = "payments.csv";
    private static final String CHECK_INS = "checkins.csv";
    private static final String SEQUENCES = "sequences.csv";

    private final Path directory;

    public FileStorage(Path directory) {
        this.directory = directory;
    }

    public Path getDirectory() {
        return directory;
    }

    public boolean exists() {
        return Files.exists(directory.resolve(USERS));
    }

    public GymData load() throws IOException {
        GymData data = new GymData();
        readInto(USERS, User::fromCsv, u -> data.getUsers().put(u.getUsername(), u));
        readInto(MEMBERS, Member::fromCsv, m -> data.getMembers().put(m.getId(), m));
        readInto(TRAINERS, Trainer::fromCsv, t -> data.getTrainers().put(t.getId(), t));
        readInto(CLASSES, FitnessClass::fromCsv, c -> data.getClasses().put(c.getId(), c));
        readInto(PAYMENTS, Payment::fromCsv, data.getPayments()::add);
        readInto(CHECK_INS, CheckIn::fromCsv, data.getCheckIns()::add);
        readInto(SEQUENCES, f -> f, f -> data.ensureSequenceAtLeast(f[0], Integer.parseInt(f[1])));
        syncSequences(data);
        return data;
    }

    public void save(GymData data) throws IOException {
        Files.createDirectories(directory);
        write(USERS, "username,passwordHash,role,mustChangePassword", data.getUsers().values());
        write(MEMBERS, "type,id,fullName,phone,email,joinDate,plan,expiryDate,extraInfo", data.getMembers().values());
        write(TRAINERS, "id,fullName,phone,email,specialty", data.getTrainers().values());
        write(CLASSES, "id,name,trainerId,day,startTime,durationMinutes,capacity,bookings", data.getClasses().values());
        write(PAYMENTS, "id,memberId,memberName,plan,basePrice,discount,amountDue,method,amountTendered,"
                + "date,coverageStart,coverageEnd,processedBy", data.getPayments());
        write(CHECK_INS, "memberId,date,time", data.getCheckIns());

        List<String> lines = new ArrayList<>();
        lines.add("prefix,lastValue");
        for (Map.Entry<String, Integer> e : data.getSequences().entrySet()) {
            lines.add(Csv.join(e.getKey(), e.getValue()));
        }
        writeLines(SEQUENCES, lines);
    }

    private void write(String file, String header, Collection<? extends Storable> records) throws IOException {
        List<String> lines = new ArrayList<>(records.size() + 1);
        lines.add(header);
        for (Storable record : records) lines.add(record.toCsv());
        writeLines(file, lines);
    }

    /** Writes to a temp file first, then swaps it in, so a crash mid-save cannot leave a half-written file. */
    private void writeLines(String file, List<String> lines) throws IOException {
        Path target = directory.resolve(file);
        Path temp = directory.resolve(file + ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8);
        Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /** Parses every data line (line 1 is the header); a corrupted line is skipped with a warning. */
    private <T> void readInto(String file, Function<String[], T> parser, Consumer<T> sink) throws IOException {
        Path path = directory.resolve(file);
        if (!Files.exists(path)) return;
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) continue;
            try {
                sink.accept(parser.apply(Csv.split(line)));
            } catch (RuntimeException e) {
                System.out.printf("  Warning: skipped unreadable line %d in %s%n", i + 1, file);
            }
        }
    }

    /** Guards against a missing or edited sequences.csv handing out an ID that already exists. */
    private static void syncSequences(GymData data) {
        sync(data, "M", data.getMembers().keySet());
        sync(data, "T", data.getTrainers().keySet());
        sync(data, "CLS", data.getClasses().keySet());
        sync(data, "PAY", data.getPayments().stream().map(Payment::id).toList());
    }

    private static void sync(GymData data, String prefix, Collection<String> ids) {
        int max = 0;
        for (String id : ids) {
            try {
                max = Math.max(max, Integer.parseInt(id.substring(id.lastIndexOf('-') + 1)));
            } catch (NumberFormatException ignored) {
                // not one of our generated IDs
            }
        }
        data.ensureSequenceAtLeast(prefix, max);
    }
}
