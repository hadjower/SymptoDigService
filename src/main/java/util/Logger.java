package util;

public class Logger {
    private static long lastTime = 0;

    public static void log(String text) {
        System.out.println(text);
        lastTime = System.currentTimeMillis();
    }

    public static void logWithTime(String text) {
        long currTime = System.currentTimeMillis();
        System.out.println(text + " | Time: " + (currTime-lastTime)/1000.0 + "s");
        lastTime = currTime;
    }
}
