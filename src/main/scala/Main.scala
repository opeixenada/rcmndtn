import java.io.File

import org.json4s.jackson.JsonMethods.parse
import recommendation.Model.Items
import recommendation.{Engine, ItemsDaoImpl}

import scala.collection.immutable.HashMap
import scala.io.Source


/** Initializes recommendation engine with items from a given JSON file, runs CLI. */
object Main {

  /** @param args path to source JSON file containing items */
  def main(args: Array[String]): Unit = {

    println("Starting engine...")

    if (args.length < 1) {
      println("Pass me the path to JSON file")
      return
    }

    val path = args(0)
    val items = parseInput(path)

    runCli(new Engine(new ItemsDaoImpl(items)))
  }

  private def parseInput(path: String): Items = {
    val file = new File(path)

    implicit val formats = org.json4s.DefaultFormats

    val input = for {
      l <- Source.fromFile(file).getLines()
      (name, attributes) <- parse(l).extract[Map[String, Map[String, String]]]
    } yield {
      name -> HashMap(attributes.toSeq: _*)
    }

    HashMap(input.toSeq: _*)
  }

  /**
    * Runs a very basic CLI to access recommendation engine.
    *
    * @param engine `recommendation.Engine` instance
    */
  private def runCli(engine: Engine): Unit = {
    var ok = true

    println("Enter SKU to get a list of recommendations >")

    while (ok) {
      val cmnd = scala.io.StdIn.readLine()

      try {
        cmnd match {
          case "exit" =>
            println("Bye")
            ok = false

          case s =>
            println(engine.getTopSimilar(s) match {
              case Some(xs) => xs.map { case (name, rate) => s"$name: $rate" }.mkString("\n")
              case _ => "Item not found"
            })
        }
      }
      catch {
        case e: Exception =>
          println(s"Failed to execute: $cmnd")
          println(e)
      }
    }
  }
}
