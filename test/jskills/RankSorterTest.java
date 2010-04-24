using System.Collections.Generic;
using Moserware.Skills;
using NUnit.Framework;

namespace UnitTests
{
    [TestFixture]
    public class RankSorterTest
    {
        [Test]
        public void SortAlreadySortedTest()
        {
            IEnumerable<String> people = new[] { "One", "Two", "Three" };
            int[] ranks = new[] { 1, 2, 3 };

            RankSorter.Sort(ref people, ref ranks);

            CollectionassertEquals(new[] { "One", "Two", "Three" }, people);
            CollectionassertEquals(new[] { 1, 2, 3 }, ranks);
        }

        [Test]
        public void SortUnsortedTest()
        {
            IEnumerable<String> people = new[] { "Five", "Two1", "Two2", "One", "Four" };
            int[] ranks = new[] { 5, 2, 2, 1, 4 };

            RankSorter.Sort(ref people, ref ranks);

            CollectionassertEquals(new[] { "One", "Two1", "Two2", "Four", "Five" }, people);
            CollectionassertEquals(new[] { 1, 2, 2, 4, 5 }, ranks);
        }
    }
}