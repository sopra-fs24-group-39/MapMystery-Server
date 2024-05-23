package ch.uzh.ifi.hase.soprafs24.service;

import java.util.Random;

public class HumanReadableIdGenerator {

    private static final String[] ADJECTIVES = {
            "Brave", "Calm", "Delightful", "Eager", "Faithful", "Gentle", "Happy", "Jolly", "Kind", "Lively",
            "Nice", "Obedient", "Proud", "Relieved", "Silly", "Thankful", "Victorious", "Witty", "Zealous"
    };

    private static final String[] NOUNS = {
            "Lion", "Tiger", "Bear", "Wolf", "Fox", "Eagle", "Shark", "Dolphin", "Panda", "Koala",
            "Penguin", "Elephant", "Giraffe", "Kangaroo", "Leopard", "Monkey", "Owl", "Rabbit", "Swan", "Turtle"
    };

    private static final Random RANDOM = new Random();

    public static String generate() {
        String adjective = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[RANDOM.nextInt(NOUNS.length)];
        int number = RANDOM.nextInt(1000);  // Generates a number between 0 and 999

        return adjective + '-' + noun + '-'+ number;
    }
}