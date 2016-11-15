package solutions;

import java.util.*;

/**
 *
 * Problem from www.google.com/foobar
 *
 * Minglish lesson
 * ===============
 *
 * Welcome to the lab, minion. Henceforth you shall do the bidding of Professor Boolean. Some say he's mad, trying to
 * develop a zombie serum and all... but we think he's brilliant!
 *
 * First things first - Minions don't speak English, we speak Minglish. Use the Minglish dictionary to learn! The first
 * thing you'll learn is how to use the dictionary.
 *
 * Open the dictionary. Read the page numbers, figure out which pages come before others. You recognize the same letters
 * used in English, but the order of letters is completely different in Minglish than English (a < b < c < ...).
 *
 * Given a sorted list of dictionary words (you know they are sorted because you can read the page numbers), can you
 * find the alphabetical order of the Minglish alphabet? For example, if the words were ["z", "yx", "yz"] the
 * alphabetical order would be "xzy," which means x < z < y. The first two words tell you that z < y, and the last
 * two words tell you that x < z.
 *
 * Write a function answer(words) which, given a list of words sorted alphabetically in the Minglish alphabet, outputs
 * a string that contains each letter present in the list of words exactly once; the order of the letters in the output
 * must follow the order of letters in the Minglish alphabet.
 *
 * The list will contain at least 1 and no more than 50 words, and each word will consist of at least 1 and no more than
 * 50 lowercase letters [a-z]. It is guaranteed that a total ordering can be developed from the input provided
 * (i.e. given any two distinct letters, you can tell which is greater), and so the answer will exist and be unique.
 *
 * Languages
 * =========
 *
 * To provide a Python solution, edit solution.py
 * To provide a Java solution, edit solution.java
 *
 * Test cases
 * ==========
 *
 * Inputs:
 * (string list) words = ["y", "z", "xy"]
 * Output:
 * (string) "yzx"
 *
 * Inputs:
 * (string list) words = ["ba", "ab", "cb"]
 * Output:
 * (string) "bac"
 *
 * Use verify [file] to test your solution and see how it does. When you are finished editing your code, use
 * submit [file] to submit your answer. If your solution passes the test cases, it will be removed from your home folder.
 * =====================================================================================================================
 *
 * My Solution
 * ===========
 *
 * Parse the dictionary and where a comparison can be made between two characters (i.e. first occurrence of differing
 * characters at the same index in adjacent words) create an edge in a directed graph.
 *
 * Once the graph is complete, perform a topological sort on the graph.
 *
 * Created by richardspellman on 26/10/2016.
 */

public class MinglishLesson {
  static List unorderedAlphabet;
  static HashMap intToCharHashMap;
  static HashMap charToIntHashMap;

  public static String answer(String[] words) {

    unorderedAlphabet = Utils.unorderedAlphabet(words);
    intToCharHashMap = Utils.createIntToCharHashMap(unorderedAlphabet);
    charToIntHashMap = Utils.createCharToIntHashMap(unorderedAlphabet);

    return Utils.parseResult(Utils.parseDictionary(words).topologicalSort());
  }


  /**
   * Helper methods for parsing dictionary and converting chars so graph nodes can just be integers.
   */

  private static class Utils {

    /** Parses the dictionary and creates a graph object where the edges are ordering rules between pairs of
     *  characters inferred from the dictionary.
     *
     * Graph nodes are integers which are converted from the characters by charToIntHashMap() and can later
     * converted back to characters by intToCharHashMap
     *
     * @param dictionary A String array object containing a sorted list of words
     * @return The Graph representing ordering rules for pairs of characters
     */
    public static Graph parseDictionary(String[] dictionary) {

      Graph graph = new Graph(unorderedAlphabet.size());

      for (int i = 1; i < dictionary.length; i++) {
        String word1 = dictionary[i - 1];
        String word2 = dictionary[i];

        int minLength = Math.min(word1.length(), word2.length());
        int characterIndex = 0;

        while (characterIndex < minLength) {
          char character1 = word1.charAt(characterIndex);
          char character2 = word2.charAt(characterIndex);

          if (character1 != character2) {
            graph.addEdge((int) charToIntHashMap.get(character1), (int) charToIntHashMap.get(character2));
            break;
          } else {
            characterIndex++;
          }
        }
      }
      return graph;
    }

    /**
     * Creates the list of characters found in the dictionary.
     * Used to create the HashMaps which convert between char and int for creation and interpretation of the graph.
     * Also used to infer the required size of the graph - i.e. the number of unique characters.
     *
     * @param dictionary A String array object containing a sorted list of words
     * @return A List containing 1 and only 1 of each character found in the dictionary
     */
    public static List unorderedAlphabet(String[] dictionary){
      HashSet<Character> characters = new HashSet<>();

      for(String word : dictionary){
        for(int i = 0; i < word.length(); i++){
          characters.add(word.charAt(i));
        }
      }

      List unorderedAlphabet = new ArrayList<>(characters);
      return unorderedAlphabet;
    }


    /**
     * Creates a HashMap used to efficiently convert an integer i in the range 0 <= i < alphabet.size() to a
     * char in the alphabet
     * @param alphabet A unique list of characters
     * @return A HashMap which maps a character in the apphabet to an integer
     */
    public static HashMap createCharToIntHashMap(List<Character> alphabet){
      HashMap<Character, Integer> hashMap = new HashMap<>();
      for(int i = 0; i < alphabet.size(); i++){
        hashMap.put(alphabet.get(i), i);
      }

      return hashMap;
    }

    /**
     * Creates a HashMap used to efficiently convert a char in the apphabet to an integer i
     * where 0 <= i < alphabet.size()
     * @param alphabet A unique list of characters
     * @return A HashMap which maps an integer to a character in the alphabet
     */
    public static HashMap createIntToCharHashMap(List<Character> alphabet){
      HashMap<Integer, Character> hashMap = new HashMap<>();
      for(int i = 0; i < alphabet.size(); i++){
        hashMap.put(i, alphabet.get(i));
      }

      return hashMap;
    }


    /**
     * Takes a Stack which is returned from the topological sort and creates a string which is formatted correctly
     * for output specified in the problem description.
     * @param stack A Stack object representing the sorted Graph.
     * @return A string representing the (sorted) alphabet.
     */
    public static String parseResult(Stack stack) {
      StringBuilder tmpString = new StringBuilder();

      while (stack.isEmpty() == false) {
        tmpString.append(intToCharHashMap.get((int) stack.pop()));
      }
      return tmpString.toString();
    }

  }

  /**
   *
   */
  private static class Graph {
    private int vertexCount;
    private LinkedList<Integer> adjacencyList[];

    /**
     * Construct a graph with a given vertex count
     * @param v an integer representing the number of vertices in the graph
     */
    Graph(int v) {
      vertexCount = v;
      adjacencyList = new LinkedList[vertexCount];
      for (int i = 0; i < vertexCount; i++) {
        adjacencyList[i] = new LinkedList<>();
      }
    }

    /**
     * Adds an edge to the graph from vertex 1 to vertex 2
     * @param vertex1 an integer representing the start of the edge
     * @param vertex2 an integer representing the end point of the edge
     */
    void addEdge(int vertex1, int vertex2) {
      adjacencyList[vertex1].add(vertex2);
    }

    /**
     * Determines whether this graph has an edge starting at vertex 1 and ending at vertex 2. Used for testing
     * creation of graph.
     * @param vertex1 an integer representing the start of the edge being checked
     * @param vertex2 and integer representing the end of the edge being checked
     * @return true if the edge exists, else false
     */
    boolean hasEdge(int vertex1, int vertex2){
      if(adjacencyList[vertex1].contains(vertex2)){
        return true;
      }
      return false;
    }
    /**
     * Recursive helper method used by topologicalSort()
     * @param v an integer representing a node in the graph
     * @param visited an array of already visited nodes
     * @param stack stack representing the sorted graph
     */
    void topologicalSortHelper(int v, boolean visited[],
                               Stack stack) {
      visited[v] = true;
      Integer i;

      Iterator<Integer> it = adjacencyList[v].iterator();
      while (it.hasNext()) {
        i = it.next();
        if (!visited[i])
          topologicalSortHelper(i, visited, stack);
      }

      stack.push(new Integer(v));
    }

    /**
     * Sorts the graph such that for each directed edge v1 -> v2, v1 comes before v2 in the ordering.
     * @return a stack representing the sorted graph
     */
    Stack topologicalSort() {
      Stack stack = new Stack();

      boolean visited[] = new boolean[vertexCount];
      for (int i = 0; i < vertexCount; i++) {
        visited[i] = false;
      }

      for (int i = 0; i < vertexCount; i++) {
        if (visited[i] == false)
          topologicalSortHelper(i, visited, stack);
      }

      return stack;
    }
  }
}

