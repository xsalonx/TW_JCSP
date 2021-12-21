package actors;

public enum ConsoleColors {
    T_RESET("\u001B[0m"),
    T_BLACK("\u001B[30m"),
    T_RED("\u001B[31m"),
    T_GREEN("\u001B[32m"),
    T_YELLOW("\u001B[33m"),
    T_BLUE("\u001B[34m"),
    T_PURPLE("\u001B[35m"),
    T_CYAN("\u001B[36m"),
    T_WHITE("\u001B[37m");

    private ConsoleColors(String v) {
        this.v = v;
    }

    public final String v;
}