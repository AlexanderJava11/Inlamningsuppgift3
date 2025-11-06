import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Klassen Highscore hanterar sparning och inläsning av highscore-listan för spelet.
// Den läser och skriver till en textfil ("Highscore.txt") där spelresultat lagras.
public class Highscore {

    // Filen där highscore-resultaten sparas.
    private static final File FILE = new File("Highscore.txt");

    // Max antal resultat som sparas i listan (t.ex. topp 10).
    private static final int MAX = 10;

    // Metoden load() läser in highscore-listan från filen "Highscore.txt".
    // Den returnerar en lista med Entry-objekt (namn, drag, tid).
    public static List<Entry> load() {
        List<Entry> list = new ArrayList<>();

        // Om filen inte finns, returnera en tom lista.
        if (!FILE.exists()) {
            return list;
        } else {
            String line;

            // Läser filen rad för rad.
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                while ((line = br.readLine()) != null) {
                    // Varje rad är uppdelad med semikolon (t.ex. "Anna;42;12345")
                    String[] parts = line.split(";");

                    // Om raden innehåller minst tre delar (namn, drag, tid)
                    if (parts.length >= 3) {
                        String name = parts[0];
                        int moves = Integer.parseInt(parts[1]);
                        long time = Long.parseLong(parts[2]);

                        // Skapar en ny Entry och lägger till i listan.
                        list.add(new Entry(name, moves, time));
                    }
                }
            } catch (NumberFormatException | IOException var10) {
                // Om något går fel (t.ex. trasig fil), ignoreras felet tyst.
                // (Detta kan göras bättre med felmeddelande, men här hoppar vi över det.)
            }

            // Sorterar listan så att bästa resultaten hamnar först.
            Collections.sort(list);
            return list;
        }
    }

    // Metoden save() sparar highscore-listan till filen.
    // Endast de 10 bästa (eller färre) resultaten sparas.
    public static void save(List<Entry> list) {
        // Sorterar listan så att den bästa poängen kommer först.
        Collections.sort(list);

        // Skriver till filen med BufferedWriter.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {

            // Bestämmer hur många resultat som ska sparas (max 10).
            int limit = Math.min(MAX, list.size());

            // Skriver varje entry som en rad i filen.
            for (int i = 0; i < limit; ++i) {
                Entry entry = list.get(i);
                bw.write(entry.name + ";" + entry.moves + ";" + entry.timeMs);
                bw.newLine(); // Går till nästa rad.
            }
        } catch (IOException e) {
            // Skriver ut fel i konsolen om filen inte kunde sparas.
            e.printStackTrace();
        }
    }

    // Metoden addEntry() lägger till ett nytt resultat i listan och sparar den.
    public static void addEntry(Entry entry) {
        // Laddar befintliga highscores.
        List<Entry> list = load();

        // Lägger till det nya resultatet.
        list.add(entry);

        // Sparar listan igen.
        save(list);
    }

    // Klassen Entry representerar ett highscore-resultat.
    // Den är static och final, vilket betyder att den bara används inom Highscore
    // och inte kan ändras eller ärvas.
    public static final class Entry implements Comparable<Entry> {

        // Spelarens namn.
        public final String name;

        // Antal drag som spelaren behövde.
        public final int moves;

        // Speltid i millisekunder.
        public final long timeMs;

        // Konstruktor som skapar ett nytt highscore-objekt.
        public Entry(String name, int moves, long timeMs) {
            this.name = name;
            this.moves = moves;
            this.timeMs = timeMs;
        }

        // Metoden compareTo() används för att sortera resultaten.
        // Först jämförs antal drag — om lika, jämförs tiden.
        @Override
        public int compareTo(Entry o) {
            return this.moves != o.moves
                    ? Integer.compare(this.moves, o.moves)
                    : Long.compare(this.timeMs, o.timeMs);
        }

        // Metoden toString() returnerar en textversion av resultatet.
        // Den visar namn, antal drag och tid både i millisekunder och sekunder.
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
