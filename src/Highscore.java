import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Highscore {
    private static final File FILE = new File("Highscore.txt");
    private static final int MAX = 10;

    public static List<Entry> load() {
        List<Entry> list = new ArrayList();
        if (!FILE.exists()) {
            return list;
        } else {
            String line;
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                while((line = br.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length >= 3) {
                        String name = parts[0];
                        int moves = Integer.parseInt(parts[1]);
                        long time = Long.parseLong(parts[2]);
                        list.add(new Entry(name, moves, time));
                    }
                }
            } catch (NumberFormatException | IOException var10) {
            }

            Collections.sort(list);
            return list;
        }
    }

    public static void save(List<Entry> list) {
        Collections.sort(list);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {
            int limit = Math.min(10, list.size());

            for(int i = 0; i < limit; ++i) {
                Entry entry = (Entry)list.get(i);
                bw.write(entry.name + ";" + entry.moves + ";" + entry.timeMs);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addEntry(Entry entry) {
        List<Entry> list = load();
        list.add(entry);
        save(list);
    }

    public static final class Entry implements Comparable<Entry> {
        public final String name;
        public final int moves;
        public final long timeMs;

        public Entry(String name, int moves, long timeMs) {
            this.name = name;
            this.moves = moves;
            this.timeMs = timeMs;
        }

        public int compareTo(Entry o) {
            return this.moves != o.moves ? Integer.compare(this.moves, o.moves) : Long.compare(this.timeMs, o.timeMs);
        }

        public String toString() {
            double seconds = (double)this.timeMs / (double)1000.0F;
            return String.format("%s löste spelet på %d drag och %d milisekunder (≈ %.1f sekunder)", this.name, this.moves, this.timeMs, seconds);
        }
    }
}
