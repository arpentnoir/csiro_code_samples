package tests;

import org.junit.Assert;
import org.junit.Test;
import solutions.MinglishLesson;

import static org.junit.Assert.*;

/**
 * Created by richardspellman on 26/10/2016.
 */
public class MinglishLessonTest {

  MinglishLesson ml = new MinglishLesson();

  @Test
  public void testAnswerExample1() throws Exception {
    System.out.println("Example 1 Text");
    String[] words = {"y", "z", "xy"};
    Assert.assertEquals("yzx", ml.answer(words));

  }
  @Test
  public void testAnswerExample2() throws Exception {
    String[] words = {"ba", "ab", "cb"};
    Assert.assertEquals("bac", ml.answer(words));
  }

  @Test
  public void testAnswerAlphabet() throws Exception {
    String[] words = {"ab", "bc", "cd", "de", "ef", "fg", "gh", "hi", "ij", "jk", "kl", "lm", "mn", "no", "op", "pq", "qr", "rs", "st", "tu", "uv", "vw", "wx", "xy", "yz", "z"};
    Assert.assertEquals("abcdefghijklmnopqrstuvwxyz", ml.answer(words));
  }
}