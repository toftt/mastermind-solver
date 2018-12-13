package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();

        while (true) {
            System.out.println(String.format("Try this: %s", game.getNextGuess()));
            System.out.print("enter score:\n> ");
            int score = scanner.nextInt();
            game.enterScore(score);
        }
    }
}
