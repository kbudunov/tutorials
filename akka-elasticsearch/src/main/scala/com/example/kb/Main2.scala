package com.example.kb

object Main2 {
  def main(args: Array[String]): Unit = {
    import java.util
    val userMetadata: util.Map[String, String] =
    new util.TreeMap[String, String](String.CASE_INSENSITIVE_ORDER)

    userMetadata.put("a", "a")
    userMetadata.put("a", "a")
    userMetadata.put("a", "a")

    println(userMetadata.size())
  }
}
