package jskills

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
   * @ the items sorted according to their ranks
   */
  def sort[T](items: Seq[T], itemRanks: Seq[Int]): Seq[T] = {
    Guard.argumentNotNull(items, "items")
    Guard.argumentNotNull(itemRanks, "itemRanks")
    val map = items.zipWithIndex
    val sorted = map.sortBy(i => itemRanks(i._2))
    sorted.map(_._1)
  }
}