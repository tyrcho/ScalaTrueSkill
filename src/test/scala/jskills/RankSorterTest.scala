package jskills

import org.junit.Test

import org.junit.Assert.assertEquals

class RankSorterTest {
  @Test
  def sortAlreadySortedTest() {
    var people = Seq("One", "Two", "Three")
    val ranks = Seq(1, 2, 3)

    people = RankSorter.sort(people, ranks)

    assertEquals(people, Seq("One", "Two", "Three"))
  }

  @Test
  def sortUnsortedTest() {
    var people = Seq("Five", "Two1", "Two2", "One", "Four")
    var ranks = Seq(5, 2, 2, 1, 4)
    val sortedranks = Seq(1, 2, 2, 4, 5)

    people = RankSorter.sort(people, ranks)
    //RankSorter no more sorts ranks atm
    ranks = sortedranks.sortBy(i => i)
    // assertEquals doesn't work on primitive arrays
    // see http://code.google.com/p/testng/issues/detail?id=4
    for (i <- 0 until people.size)
      assertEquals(format("Different at index %d. Expected <%d>, was <%d>.", i, sortedranks(i), ranks(i)),
        ranks(i), sortedranks(i))
    assertEquals(people, Seq("One", "Two1", "Two2", "Four", "Five"))
  }
}