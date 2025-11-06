// Klassen Tile är en enda ruta i spelet (en bricka med ett värde)
public final class Tile {  // final betyder att ingen annan klass kan ärva den

    private final int value; // Siffran på brickan (0 betyder tom ruta)

    // Skapar en Tile med ett visst värde
    public Tile(int value) {
        this.value = value;
    }

    // Kollar om rutan är tom (värdet är 0)
    public boolean isEmpty() {
        return this.value == 0;
    }

    // Hämtar värdet på rutan
    public int value() {
        return this.value;
    }

    // Returnerar hur brickan ska visas som text
    @Override
    public String toString() {
        // Om brickan är tom → ingen text, annars visa siffran
        return this.isEmpty() ? "" : Integer.toString(this.value);
    }
}
