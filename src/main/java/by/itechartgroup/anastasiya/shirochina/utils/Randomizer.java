package by.itechartgroup.anastasiya.shirochina.utils;

public class Randomizer {
    public static int randomNumber(int min, int max) {
        int randomPageCount = (int) (Math.random() * (max-min+1) + min);
        return randomPageCount;
    }
}
