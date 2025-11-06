import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Klassen Highscore hanterar att läsa och skriva highscore-listan i en textfil
public class Highscore {

    // Filen där highscores sparas
    private static final File FILE = new File("Highscore.txt");

    // Max antal resultat som ska sparas (t.ex. topp 10)
    private static final int MAX = 10;

    // Läser in highscores från filen och returnerar en lista med Entry-objekt
    public static List<Entry> load() {
        List<Entry> list = new ArrayList<>(); // Lista som byggs upp

        // Om filen inte finns, returnera tom lista direkt
        if (!FILE.exists()) {
            return list;
        } else {
            String line;

            // Läs filen rad för rad
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                while ((line = br.readLine()) != null) {
                    // Dela raden på semikolon: "Namn;drag;tid"
                    String[] parts = line.split(";");

                    // Om raden har minst 3 delar, tolka dem
                    if (parts.length >= 3) {
                        String name = parts[0]; // Namn
                        int moves = Integer.parseInt(parts[1]); // Antal drag
                        long time = Long.parseLong(parts[2]); // Tid i ms

                        // Lägg till en ny Entry i listan
                        list.add(new Entry(name, moves, time));
                    }
                }
            } catch (NumberFormatException | IOException var10) {
                // Om något går fel (t.ex. trasigt innehåll) så ignorerar vi raden/felet här
                // (kan förbättras genom att logga eller visa fel)
            }

            // Sortera listan så bästa resultat kommer först
            Collections.sort(list);
            return list; // Returnera sorterad lista
        }
    }

    // Sparar en lista med Entry till fil, bara topp MAX objekt sparas
    public static void save(List<Entry> list) {
        // Sortera först så att bäst kommer först
        Collections.sort(list);

        // Skriv ut till fil
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {

            // Bestäm hur många vi ska skriva (högst MAX)
            int limit = Math.min(MAX, list.size());

            // Skriv varje entry som "name;moves;timeMs"
            for (int i = 0; i < limit; ++i) {
                Entry entry = list.get(i);
                bw.write(entry.name + ";" + entry.moves + ";" + entry.timeMs);
                bw.newLine(); // Nya raden i filen
            }
        } catch (IOException e) {
            // Om skrivningen misslyckas: skriv stacktrace till konsolen
            e.printStackTrace();
        }
    }

    // Lägger till en ny entry i filen (läser in befintliga, lägger till och sparar)
    public static void addEntry(Entry entry) {
        List<Entry> list = load(); // Läs befintliga highscores
        list.add(entry);           // Lägg till nya
        save(list);                // Spara tillbaka till fil
    }

    // Entry representerar ett highscore-resultat (namn, drag, tid)
    public static final class Entry implements Comparable<Entry> {

        public final String name;   // Spelarens namn
        public final int moves;     // Antal drag
        public final long timeMs;   // Tid i millisekunder

        // Skapar en ny Entry
        public Entry(String name, int moves, long timeMs) {
            this.name = name;
            this.moves = moves;
            this.timeMs = timeMs;
        }

        // Jämför två Entry för sortering: först moves, om lika -> timeMs
        @Override
        public int compareTo(Entry o) {
            return this.moves != o.moves
                    ? Integer.compare(this.moves, o.moves)
                    : Long.compare(this.timeMs, o.timeMs);
        }

        // Ger en enkel textbeskrivning av entry
        @Override
        public String toString() {
            double seconds = (double) this.timeMs / 1000.0;
            return String.format(
                    "%s löste spelet på %d drag och %d millisekunder (≈ %.1f sekunder)",
                    this.name, this.moves, this.timeMs, seconds
            );
        }
    }
}
