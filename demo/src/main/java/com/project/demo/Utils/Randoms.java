package com.project.demo.Utils;

import java.util.Random;

public class Randoms {
    public static int getRandomNumberBetween(int low, int high) {
        Random random = new Random();
        return random.nextInt(high - low) + low;
    }
}
