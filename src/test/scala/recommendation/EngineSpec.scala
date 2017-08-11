package recommendation

import org.scalatest.{Matchers, WordSpec}
import scala.collection.immutable.HashMap


class EngineSpec extends WordSpec with Matchers {

  "Similarity rate" should {
    "be 0 if there are no matching attributes" in {
      val attrs1 = HashMap(
        "attr-a" -> "attr-a-1",
        "attr-b" -> "attr-b-1",
        "attr-c" -> "attr-c-1"
      )

      val attrs2 = HashMap(
        "attr-a" -> "attr-a-2",
        "attr-b" -> "attr-b-2",
        "attr-c" -> "attr-c-2"
      )

      Engine.calculateSimilarity(attrs1, attrs2) should be(0)
    }

    "be 1.25 if there is one matching attribute 'b'" in {
      val attrs1 = HashMap(
        "attr-a" -> "attr-a-1",
        "attr-b" -> "attr-b-1",
        "attr-c" -> "attr-c-1"
      )

      val attrs2 = HashMap(
        "attr-a" -> "attr-a-2",
        "attr-b" -> "attr-b-1",
        "attr-c" -> "attr-c-2"
      )

      Engine.calculateSimilarity(attrs1, attrs2) should be(1 + Math.pow(0.5, 2))
    }

    "be 1.125 if there is one matching attribute 'c'" in {
      val attrs1 = HashMap(
        "attr-a" -> "attr-a-1",
        "attr-b" -> "attr-b-1",
        "attr-c" -> "attr-c-1"
      )

      val attrs2 = HashMap(
        "attr-a" -> "attr-a-2",
        "attr-b" -> "attr-b-2",
        "attr-c" -> "attr-c-1"
      )

      Engine.calculateSimilarity(attrs1, attrs2) should be(1 + Math.pow(0.5, 3))
    }

    "be 2.625 if there are two matching attributes 'a', 'c'" in {
      val attrs1 = HashMap(
        "attr-a" -> "attr-a-1",
        "attr-b" -> "attr-b-1",
        "attr-c" -> "attr-c-1"
      )

      val attrs2 = HashMap(
        "attr-a" -> "attr-a-1",
        "attr-b" -> "attr-b-2",
        "attr-c" -> "attr-c-1"
      )

      Engine.calculateSimilarity(attrs1, attrs2) should be(2 + 0.5 + Math.pow(0.5, 3))
    }
  }

  "Engine.getTopSimilar" should {
    "return `None` if item is not found" in {
      val itemsDao = new ItemsDaoImpl(HashMap.empty)
      val engine = new Engine(itemsDao)
      engine.getTopSimilar("sku-1") should be(None)
    }

    "rate items with more matching attributes higher" in {
      val items = HashMap(
        "sku-1" -> HashMap(
          "attr-a" -> "attr-a-1",
          "attr-b" -> "attr-b-1",
          "attr-c" -> "attr-c-1"
        ),
        "sku-2" -> HashMap(
          "attr-a" -> "attr-a-1",
          "attr-b" -> "attr-b-1",
          "attr-c" -> "attr-c-2"
        ),
        "sku-3" -> HashMap(
          "attr-a" -> "attr-a-1",
          "attr-b" -> "attr-b-3",
          "attr-c" -> "attr-c-3"
        )
      )
      val itemsDao = new ItemsDaoImpl(items)
      val engine = new Engine(itemsDao)
      engine.getTopSimilar("sku-1").get.map(_._1) should be(Seq("sku-2", "sku-3"))
    }

    "rate items with heavier matching attributes higher" in {
      val items = HashMap(
        "sku-1" -> HashMap(
          "attr-a" -> "attr-a-1",
          "attr-b" -> "attr-b-1",
          "attr-c" -> "attr-c-1"
        ),
        "sku-2" -> HashMap(
          "attr-a" -> "attr-a-2",
          "attr-b" -> "attr-b-1",
          "attr-c" -> "attr-c-1"
        ),
        "sku-3" -> HashMap(
          "attr-a" -> "attr-a-1",
          "attr-b" -> "attr-b-1",
          "attr-c" -> "attr-c-3"
        )
      )
      val itemsDao = new ItemsDaoImpl(items)
      val engine = new Engine(itemsDao)
      engine.getTopSimilar("sku-1").get.map(_._1) should be(Seq("sku-3", "sku-2"))
    }

    "return not more than `n` similar items" in {
      val items = HashMap(
        "sku-1" -> HashMap(
          "attr-a" -> "attr-a-1",
          "attr-b" -> "attr-b-1",
          "attr-c" -> "attr-c-1"
        ),
        "sku-2" -> HashMap(
          "attr-a" -> "attr-a-2",
          "attr-b" -> "attr-b-1",
          "attr-c" -> "attr-c-1"
        ),
        "sku-3" -> HashMap(
          "attr-a" -> "attr-a-1",
          "attr-b" -> "attr-b-1",
          "attr-c" -> "attr-c-3"
        ),
        "sku-4" -> HashMap(
          "attr-a" -> "attr-a-1",
          "attr-b" -> "attr-b-1",
          "attr-c" -> "attr-c-4"
        )
      )
      val itemsDao = new ItemsDaoImpl(items)
      val engine = new Engine(itemsDao)
      engine.getTopSimilar("sku-1", 2).toSeq.flatten.size should be(2)
    }

    "return the most similar items" in {
      val items = HashMap(
        "sku-1" -> HashMap("att-a" -> "att-a-7", "att-b" -> "att-b-3", "att-c" -> "att-c-10",
          "att-d" -> "att-d-10", "att-e" -> "att-e-15", "att-f" -> "att-f-11",
          "att-g" -> "att-g-2", "att-h" -> "att-h-7", "att-i" -> "att-i-5", "att-j" -> "att-j-14"),
        "sku-2" -> HashMap("att-a" -> "att-a-9", "att-b" -> "att-b-7", "att-c" -> "att-c-12",
          "att-d" -> "att-d-4", "att-e" -> "att-e-10", "att-f" -> "att-f-4", "att-g" -> "att-g-13",
          "att-h" -> "att-h-4", "att-i" -> "att-i-1", "att-j" -> "att-j-13"),
        "sku-3" -> HashMap("att-a" -> "att-a-10", "att-b" -> "att-b-6", "att-c" -> "att-c-1",
          "att-d" -> "att-d-1", "att-e" -> "att-e-13", "att-f" -> "att-f-12", "att-g" -> "att-g-9",
          "att-h" -> "att-h-6", "att-i" -> "att-i-7", "att-j" -> "att-j-4"),
        "sku-4" -> HashMap("att-a" -> "att-a-9", "att-b" -> "att-b-14", "att-c" -> "att-c-7",
          "att-d" -> "att-d-4", "att-e" -> "att-e-8", "att-f" -> "att-f-7", "att-g" -> "att-g-14",
          "att-h" -> "att-h-9", "att-i" -> "att-i-13", "att-j" -> "att-j-3"),
        "sku-5" -> HashMap("att-a" -> "att-a-8", "att-b" -> "att-b-7", "att-c" -> "att-c-10",
          "att-d" -> "att-d-4", "att-e" -> "att-e-11", "att-f" -> "att-f-4", "att-g" -> "att-g-8",
          "att-h" -> "att-h-8", "att-i" -> "att-i-7", "att-j" -> "att-j-8"),
        "sku-6" -> HashMap("att-a" -> "att-a-6", "att-b" -> "att-b-2", "att-c" -> "att-c-13",
          "att-d" -> "att-d-6", "att-e" -> "att-e-2", "att-f" -> "att-f-11", "att-g" -> "att-g-2",
          "att-h" -> "att-h-11", "att-i" -> "att-i-1", "att-j" -> "att-j-9"),
        "sku-7" -> HashMap("att-a" -> "att-a-15", "att-b" -> "att-b-10", "att-c" -> "att-c-7",
          "att-d" -> "att-d-7", "att-e" -> "att-e-13", "att-f" -> "att-f-15", "att-g" -> "att-g-13",
          "att-h" -> "att-h-12", "att-i" -> "att-i-9", "att-j" -> "att-j-11"),
        "sku-8" -> HashMap("att-a" -> "att-a-14", "att-b" -> "att-b-1", "att-c" -> "att-c-2",
          "att-d" -> "att-d-9", "att-e" -> "att-e-4", "att-f" -> "att-f-12", "att-g" -> "att-g-13",
          "att-h" -> "att-h-11", "att-i" -> "att-i-5", "att-j" -> "att-j-5"),
        "sku-9" -> HashMap("att-a" -> "att-a-4", "att-b" -> "att-b-10", "att-c" -> "att-c-7",
          "att-d" -> "att-d-1", "att-e" -> "att-e-15", "att-f" -> "att-f-9", "att-g" -> "att-g-12",
          "att-h" -> "att-h-2", "att-i" -> "att-i-4", "att-j" -> "att-j-12"),
        "sku-10" -> HashMap("att-a" -> "att-a-10", "att-b" -> "att-b-3", "att-c" -> "att-c-7",
          "att-d" -> "att-d-2", "att-e" -> "att-e-9", "att-f" -> "att-f-5", "att-g" -> "att-g-1",
          "att-h" -> "att-h-7", "att-i" -> "att-i-1", "att-j" -> "att-j-9"))
      val itemsDao = new ItemsDaoImpl(items)
      val engine = new Engine(itemsDao)
      engine.getTopSimilar("sku-10", 5).get.map(_._1) should be(
        Seq("sku-1", "sku-6", "sku-3", "sku-9", "sku-4"))
    }
  }
}