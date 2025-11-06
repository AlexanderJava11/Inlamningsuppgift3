// Klassen Tile representerar en "ruta" (t.ex. i ett spelbräde) som innehåller ett värde.
// Klassen är final, vilket betyder att den inte kan ärvas av andra klasser.
public final class Tile {
    
    // Variabeln 'value' lagrar värdet för rutan. Den är final, så den kan inte ändras efter att objektet skapats.
    private final int value;

    // Konstruktor som tar emot ett värde och tilldelar det till 'value'-variabeln.
    public Tile(int value) {
        this.value = value;
    }

    // Metoden isEmpty() returnerar true om värdet är 0, annars false.
    // Detta kan användas för att kolla om rutan är "tom".
    public boolean isEmpty() {
        return this.value == 0;
    }

    // Metoden value() returnerar värdet på rutan.
    public int value() {
        return this.value;
    }

    // toString() skriver ut rutan som text.
    // Om rutan är tom (värde 0) returneras en tom sträng,
    // annars returneras värdet som text (t.ex. "4").
    @Override
    public String toString() {
        return this.isEmpty() ? "" : Integer.toString(this.value);
    }
}
