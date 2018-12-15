package com.company;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class Game {
    private static final List<Integer> POSSIBLE_SCORES =
            Arrays.asList(0, 1, 2, 3, 4, 10, 20, 30, 40, 11, 12, 13, 21, 22, 31);

    private Set<List<Integer>> possibleGuesses;
    private Set<List<Integer>> remainingPossibilities;
    private List<Integer> nextGuess = Arrays.asList(1, 1, 2, 2);

    Game() {
        Integer[] arr = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        List<Integer> l = Arrays.asList(arr);
        List<List<Integer>> results = Lists.cartesianProduct(l, l, l, l);
        this.remainingPossibilities = new HashSet<>(results);
        this.possibleGuesses = new HashSet<>(this.remainingPossibilities);
    }

    void enterScore(int score) {
        this.remainingPossibilities = this.removeCodesWithSameScore(this.remainingPossibilities, nextGuess, score);
        this.nextGuess = minMax();
    }

    String getNextGuess() {
        return nextGuess.toString();
    }

    private int scoreGuess(List<Integer> guess, List<Integer> code) {
        int whites = 0;
        int colored = 0;

        // white pegs
        List<Integer> temp = new ArrayList<>(code);
        for (int i = 0; i < 4; i++) {
            int current = guess.get(i);
            if (temp.contains(current)) {
                whites += 1;
                temp.remove((Integer) current);
            }
        }

        for (int i = 0; i < 4; i++) {
            if (guess.get(i).equals(code.get(i))) {
                colored += 10;
                // if a correctly place number is found it must have been added in the previous loop
                // and should be removed
                if (whites > 0) whites -= 1;
            }
        }

        return whites + colored;
    }

    private int minimumNumberRemoved(List<Integer> guess) {
        List<Integer> removed =  new ArrayList<>();
        for (int score : POSSIBLE_SCORES) {
            int before = this.remainingPossibilities.size();
            int after = removeCodesWithSameScore(this.remainingPossibilities, guess, score).size();
            removed.add(before - after);
        }

        return Collections.min(removed);
    }

    private List<Integer> minMax() {
        Map<List<Integer>, Integer> minimums = new ConcurrentHashMap<>();

        possibleGuesses.parallelStream().forEach((guess) -> {
            minimums.put(guess, minimumNumberRemoved(guess));
        });

        int max = Collections.max(minimums.entrySet(), Map.Entry.comparingByValue()).getValue();

        List<Map.Entry<List<Integer>, Integer>> maxes = minimums.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == max)
                .collect(Collectors.toList());

        for (Map.Entry<List<Integer>, Integer> entry : maxes) {
            if (remainingPossibilities.contains(entry.getKey())) return entry.getKey();
        }
        return maxes.get(0).getKey();
    }


    private Set<List<Integer>> removeCodesWithSameScore(Set<List<Integer>> codes, List<Integer> guess, int score) {
        return codes
                .stream()
                .filter(code -> this.scoreGuess(guess, code) == score)
                .collect(Collectors.toSet());
    }





    public static void main(String[] args) {
        // testing
        Game g = new Game();
        List<Integer> correctAnswer = Arrays.asList(1, 2, 4, 9);

        while (!g.nextGuess.equals(correctAnswer)) {
            System.out.println(String.format("guessing %s", g.nextGuess));
            int score = g.scoreGuess(g.nextGuess, correctAnswer);
            System.out.println(String.format("score was %d", score));
            g.enterScore(score);
        }
        System.out.println(String.format("guessed correctly %s", g.nextGuess));
    }
}

