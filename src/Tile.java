public final class Tile {
    private final int value;

    public Tile(int value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return this.value == 0;
    }

    public int value() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.isEmpty() ? "" : Integer.toString(this.value);
    }
}
