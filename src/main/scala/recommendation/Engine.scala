package recommendation

import recommendation.Model.Attributes

import scala.collection.mutable.{HashMap => MutableHashMap}


class Engine(itemsDao: ItemsDao) {

  /** Maximum number of items to keep in memory */
  private val limit = 1000

  /**
    * Gets the most similar elements.
    * Calculates similarity function for every item with the given one. To prevent keeping all
    * the values in memory, when limit is reached, only the most similar `n` values are left.
    *
    * @param name element name
    * @param n    number of the most similar elements to return
    * @return `None` if there's no element with a given name;
    *         names of the most similar items with their similarity rates otherwise
    */
  def getTopSimilar(name: String, n: Int = 10): Option[List[(String, Double)]] = {
    itemsDao.getAttributes(name) match {

      case Some(attrs1) =>
        var mostSimilar: List[(String, Double)] = List.empty

        for ((name2, attrs2) <- itemsDao.all if name != name2) {
          val similarity = Engine.calculateSimilarity(attrs1, attrs2)
          mostSimilar = (name2, similarity) +: mostSimilar
          if (mostSimilar.size > limit) {
            mostSimilar = truncate(mostSimilar, n)
          }
        }

        Some(truncate(mostSimilar, n))

      case _ => None
    }
  }

  /**
    * Leaves only `n` top elements in the list.
    *
    * @param xs list of `(String, Double)`
    * @param n  number of elements to leave
    * @return new truncated list
    */
  private def truncate(xs: List[(String, Double)], n: Int) =
    xs.sorted(Engine.ordering.reverse).take(n)
}

object Engine {
  val ordering: Ordering[(String, Double)] =
    (x: (String, Double), y: (String, Double)) => {
      x._2.compare(y._2)
    }

  /**
    * Calculates similarity for given `Attributes` hash maps.
    * Every matching attribute is worth `(1 + pow(0.5, c))`.
    * `c` if defined for every attribute name of pattern "att-[a-z]": from "att-a" (`c` = 1) to
    * "att-z" (`c` = 26).
    *
    * @param attrs1 `Attributes` hash map
    * @param attrs2 `Attributes` hash map
    * @return similarity rate (`Double`)
    */
  def calculateSimilarity(attrs1: Attributes, attrs2: Attributes): Double = {
    val similarAttributes = attrs1.toSeq.collect {
      case (attrName, attrValue1) if attrs2.get(attrName).contains(attrValue1) => attrName
    }

    similarAttributes.foldLeft(0.0) { case (rate, name) =>
      rate + 1 + Math.pow(0.5, name.last - 'a' + 1)
    }
  }
}