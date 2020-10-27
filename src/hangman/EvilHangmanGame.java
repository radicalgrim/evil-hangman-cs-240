package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
  Set<String> wordSet;
  SortedSet<Character> guessedLetters;
  String wordSoFar;
  Map<String, TreeSet<String>> patternMap;


  public EvilHangmanGame() {
    wordSet = new HashSet<>();
    guessedLetters = new TreeSet<>();
    wordSoFar = "";
    patternMap= new HashMap<>();
  }


  @Override
  public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {

    Scanner scanner = new Scanner(dictionary);
    scanner.useDelimiter("(#[^\\n]*\\n)|(\\s+)+");
    if (!scanner.hasNext()) throw new EmptyDictionaryException();

    wordSet.clear();
    while(scanner.hasNext()) {
      String str = scanner.next();
      if (str.length() == wordLength) {
        wordSet.add(str);
      }
    }

    /*
    for (String str : wordSet) {
      System.out.print(str);
      System.out.print("\n");
    }
    */

    if (wordSet.isEmpty()) throw new EmptyDictionaryException();
    wordSoFar = "-".repeat(wordLength);

  }


  @Override
  public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
    if (guess >= 'A' && guess <= 'Z') guess += 32;
    if (guessedLetters.contains(guess)) throw new GuessAlreadyMadeException();
    guessedLetters.add(guess);

    String key = findBiggestSubset(guess);
    int numHits = updateWordSoFar(guess, key);
    respondToUser(guess, numHits);

    /*
    for (String str : wordSet) {
      System.out.print(str);
      System.out.print("\n");
    }
    */

    return wordSet;
  }


  private String findBiggestSubset(char guess) {
    StringBuilder builder = new StringBuilder();
    TreeSet<String> temp;
    int max = 0;

    patternMap.clear();

    for (String str : wordSet) {
      builder.setLength(0);
      for (int i = 0; i < str.length(); i++) {
        if (str.charAt(i) == guess) {
          builder.append(guess);
        }
        else builder.append('-');
      }
      temp = patternMap.getOrDefault(builder.toString(), new TreeSet<>());
      temp.add(str);
      patternMap.put(builder.toString(), temp);
      if (temp.size() > max) max = temp.size();
    }

    TreeSet<String> sortedPatterns = new TreeSet<>();
    for (String str : patternMap.keySet()) {
      if (patternMap.get(str).size() == max) sortedPatterns.add(str);
    }

    wordSet = patternMap.get(sortedPatterns.first());

    return sortedPatterns.first();
  }


  private int updateWordSoFar(char guess, String key) {
    int numHits = 0;
    for (int i = 0; i < key.length(); i++) {
      if (key.charAt(i) == guess) {
        wordSoFar = wordSoFar.substring(0, i) + guess + wordSoFar.substring(i + 1);
        numHits++;
      }
    }
    return numHits;
  }


  private void respondToUser(char guess, int numHits) {
    StringBuilder builder = new StringBuilder();
    if (numHits > 0) {
      builder.append("Yes, there is ").append(numHits).append(" ").append(guess).append("\n\n");
    }
    else {
      builder.append("Sorry, there are no ").append(guess).append("'s\n\n");
    }
    System.out.print(builder);
  }


  @Override
  public SortedSet<Character> getGuessedLetters() {
    return guessedLetters;
  }


  public String getWordSoFar() {
    return wordSoFar;
  }
}
