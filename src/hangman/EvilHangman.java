package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangman {

    public static void main(String[] args) {
        try {
            String dictionary = args[0];
            int wordLength = Integer.parseInt(args[1]);
            int guessCount = Integer.parseInt(args[2]);
            if (guessCount < 1) throw new ArrayIndexOutOfBoundsException();
            String unfilteredGuess;
            char currentGuess;
            Set<String> possibleWords = new HashSet<>();
            EvilHangmanGame game = new EvilHangmanGame();
            File file = new File(dictionary);
            Scanner scanner = new Scanner(System.in);

            game.startGame(file, wordLength);


            while(guessCount > 0) {
                try {
                    String wsf = game.getWordSoFar();
                    promptUser(guessCount, game.getGuessedLetters(), wsf);


                    unfilteredGuess = scanner.next();
                    if (unfilteredGuess.isBlank()) throw new Exception();
                    currentGuess = unfilteredGuess.charAt(0);


                    if ((currentGuess < 'a' || currentGuess > 'z') && (currentGuess < 'A' || currentGuess > 'Z')) {
                        throw new Exception();
                    }
                    possibleWords = game.makeGuess(currentGuess);

                    CharSequence dash = "-";
                    if (!game.getWordSoFar().contains(dash)) {
                        guessCount = -1;
                    }

                    if (wsf.equals(game.getWordSoFar())) guessCount--;

                } catch (GuessAlreadyMadeException ex) {
                    System.out.print("\nThat guess was already made\n");
                } catch (Exception ex) {
                    System.out.print("\nInvalid guess\n");
                }
            }

            if (guessCount < 0) {
                System.out.print("You Win!\nThe word is: ");
                System.out.print(possibleWords.toArray()[0]);
            }
            else {
                System.out.print("You lose!\nThe word was: ");
                System.out.print(possibleWords.toArray()[0]);
            }


        } catch(IOException ex) {
            System.out.print("Error reading the file\n");
        } catch(EmptyDictionaryException ex) {
            System.out.print("The dictionary used was either empty, or had no words at the length specified\n");
        } catch(NumberFormatException ex) {
            System.out.print("Invalid arguments. Expected arguments are <filename> <number-of-letters> <number-of-guesses>\n");
        } catch(ArrayIndexOutOfBoundsException ex) {
            System.out.print("Invalid argument. Number of guesses must be 1 or greater.\n");
        } catch(RuntimeException ex) {
            throw ex;
        } catch(Exception ex) {
            System.out.print("Unhandled exception\n");
        }
    }


    public static void promptUser(int guessCount, SortedSet<Character> guessedLetters, String wordSoFar) {
        StringBuilder builder=new StringBuilder();
        builder.append("You have ").append(guessCount).append(" guesses left\n");
        builder.append("Used letters: ");
        for (Character ch : guessedLetters) {
            builder.append(ch).append(" ");
        }
        builder.replace(builder.length() - 1, builder.length(), "\n");
        builder.append("Word: ").append(wordSoFar).append("\n");
        builder.append("Enter guess: ");

        System.out.print(builder);
    }

}
