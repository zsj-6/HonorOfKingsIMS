package util;

import java.util.Scanner;

/**
 * Safe console input utility that avoids {@link java.util.InputMismatchException}
 * by reading all input as strings and parsing explicitly.
 *
 * <p>All string inputs intended for CSV storage are sanitized to reject
 * commas ({@code ,}) and semicolons ({@code ;}) which serve as delimiters
 * in the persistence layer.</p>
 */
public class InputHelper {

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputHelper() {
    }

    /**
     * Reads a trimmed non-empty string from the console.
     * Re-prompts until valid input is received.
     *
     * @param prompt the prompt to display
     * @return the trimmed, non-empty input string
     */
    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Reads a trimmed string, allowing empty input.
     *
     * @param prompt the prompt to display
     * @return the trimmed input, which may be empty
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /**
     * Reads an integer from the console. Re-prompts on invalid input.
     *
     * @param prompt the prompt to display
     * @return the parsed integer
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter an integer.");
            }
        }
    }

    /**
     * Reads an integer within the specified inclusive range.
     * Re-prompts until a valid integer in range is entered.
     *
     * @param prompt the prompt to display
     * @param min    the minimum allowed value (inclusive)
     * @param max    the maximum allowed value (inclusive)
     * @return the parsed integer within [min, max]
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.printf("Please enter a number between %d and %d.%n", min, max);
        }
    }

    /**
     * Reads a non-empty string that must not contain commas or semicolons.
     * These characters are reserved as CSV delimiters.
     *
     * @param prompt the prompt to display
     * @return the sanitized input string
     */
    public static String readLineSafe(String prompt) {
        while (true) {
            String input = readNonEmptyString(prompt);
            if (input.contains(",") || input.contains(";")) {
                System.out.println("Input cannot contain commas (,) or semicolons (;). "
                        + "Please try again.");
                continue;
            }
            return input;
        }
    }

    /**
     * Reads a password from the console with the same safety constraints
     * as {@link #readLineSafe(String)}.
     *
     * @param prompt the prompt to display
     * @return the password string
     */
    public static String readPassword(String prompt) {
        return readLineSafe(prompt);
    }

    /**
     * Reads a yes/no confirmation.
     *
     * @param prompt the prompt to display
     * @return true if the user enters "y" or "yes" (case-insensitive)
     */
    public static boolean readConfirmation(String prompt) {
        while (true) {
            String input = readNonEmptyString(prompt).toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Please enter Y or N.");
        }
    }
}
