package recommendation

import scala.collection.immutable.HashMap


object Model {
  type Attributes = HashMap[String, String]
  type Items = HashMap[String, Attributes]
}
