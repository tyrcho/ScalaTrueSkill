package jskills

import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.Collections
import java.util.Comparator
import java.util.HashMap
import java.util.List
import java.util.Map
import collection.JavaConversions._

/**
 * Helper class to sort ranks in non-decreasing order.
 */
object RankSorter {
  /**
   * Returns a list of all the elements in items, sorted in non-descending
   * order, according to itemRanks. Uses a stable sort.
   *
   * @param items
   *            The items to sort according to the order specified by ranks.
   * @param ranks
   *            The ranks for each item where 1 is first place.
   * @return the items sorted according to their ranks
   */
  def sort[T](items: Collection[T], itemRanks: Seq[Int]): List[T] = {
    Guard.argumentNotNull(items, "items")
    Guard.argumentNotNull(itemRanks, "itemRanks")

    val map = items.toSeq.zipWithIndex
    val sorted = map.sortBy(i => itemRanks(i._2))
    sorted.map(_._1)
  }
}