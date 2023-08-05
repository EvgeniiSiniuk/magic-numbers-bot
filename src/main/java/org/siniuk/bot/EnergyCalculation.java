package org.siniuk.bot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnergyCalculation {

    protected static int calculateYearEnergy(String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDate now = LocalDate.now();
        List<Integer> digits = new ArrayList<>();
        digits.addAll(getSeparateNumbers(localDate.getYear()));
        digits.addAll(getSeparateNumbers(now.getYear()));
        int twoYearsSum = getSumFromList(digits);
        return getSumFromList(getSeparateNumbers(twoYearsSum));
    }

    protected static int calculateMonthEnergy(String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Integer> firstDigits = getSeparateNumbers(localDate.getYear());
        firstDigits.add(localDate.getMonthValue());
        firstDigits.add(localDate.getDayOfMonth());
        int firstSum = getSumFromList(firstDigits);

        LocalDate now = LocalDate.now();
        List<Integer> secondDigits = getSeparateNumbers(now.getYear());
        secondDigits.add(now.getMonthValue());
        int secondSum = getSumFromList(secondDigits);

        int thirdSum = firstSum + secondSum;

        while (thirdSum > 22) {
            thirdSum = thirdSum - 22;
        }

        return thirdSum;
    }

    protected static int calculatePersonalEnergy(String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Integer> firstDigits = getSeparateNumbers(localDate.getYear());
        firstDigits.add(localDate.getMonthValue());
        int firstSum = getSumFromList(firstDigits);

        LocalDate now = LocalDate.now();
        List<Integer> secondDigits = getSeparateNumbers(now.getYear());
        secondDigits.add(now.getMonthValue());
        int secondSum = getSumFromList(secondDigits);

        int thirdSum = firstSum + secondSum;

        while (thirdSum > 22) {
            thirdSum = thirdSum - 22;
        }

        return thirdSum;
    }

    private static List<Integer> getSeparateNumbers(int number) {
        List<Integer> result = new ArrayList<>();

        int divider = 1;

        for (int i = 0; i < getNumberOfDigits(number); i++) {
            int digit = number / divider % 10;
            divider = divider * 10;
            result.add(digit);
        }
        return result;
    }

    private static int getNumberOfDigits(int number) {
        String numberString = String.valueOf(number);
        return numberString.length();
    }

    private static int getSumFromList(List<Integer> list) {
        int result = 0;

        for (Integer digit : list) {
            result = result + digit;
        }
        return result;
    }
}
