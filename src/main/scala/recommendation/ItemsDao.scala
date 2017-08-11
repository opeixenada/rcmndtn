package recommendation

import recommendation.Model.{Attributes, Items}


trait ItemsDao {
  def all: Iterator[(String, Attributes)]
  def getAttributes(name: String): Option[Attributes]
}

class ItemsDaoImpl(items: Items) extends ItemsDao {
  def all: Iterator[(String, Attributes)] = items.toIterator
  def getAttributes(name: String): Option[Attributes] = items.get(name)
}