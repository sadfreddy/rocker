package com.rocker

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec


class ProgramSpec extends AnyFlatSpec with EitherValues{

  it should "return sorted files with matching words percentage" in {
    val result = Program.countWordsInFiles(index, "hello world and test")

    val expectedResult = List(
      FileWordsCount("test1.txt", 100),
      FileWordsCount("test2.txt", 25),
      FileWordsCount("test3.txt", 0)
    )

    assert(result == expectedResult)
  }

  it should "return limited sorted files with matching words percentage" in {
    val result = Program.countWordsInFiles(index, "hello world and test", 2)

    val expectedResult = List(
      FileWordsCount("test1.txt", 100),
      FileWordsCount("test2.txt", 25),
    )

    assert(result == expectedResult)
  }

  it should "return zeroes if all files don't contain any words from search string" in {
    val result = Program.countWordsInFiles(index, "not exist")

    val expectedResult = List(
      FileWordsCount("test1.txt", 0),
      FileWordsCount("test2.txt", 0),
      FileWordsCount("test3.txt", 0),
    )

    assert(result == expectedResult)
  }

  it should "return unread fileNames" in {
    val folder = Program.readFile(Array("src/test/resources/failedFiles")).value
    val result = Program.index(folder)

    val expectedResult = Index(
      fileNames = Set.empty,
      filesWords = Map.empty,
      failedFileNames = Set("204.txt")
    )

    assert(result == expectedResult)
  }

  it should "return map of words from files" in {
    val folder = Program.readFile(Array("src/test/resources/files")).value
    val result = Program.index(folder)

    val expectedResult = Index(
      fileNames = Set("test2.txt", "test1.txt"),
      filesWords = Map(
        "world"  -> Set("test2.txt", "test1.txt"),
        "second" -> Set("test2.txt"),
        "new"    -> Set("test2.txt", "test1.txt"),
        "hello"  -> Set("test1.txt", "test2.txt"),
      ),
      failedFileNames = Set.empty
    )

    assert(result == expectedResult)
  }


  val index = Index(
    fileNames = Set("test1.txt", "test2.txt", "test3.txt"),
    filesWords = Map(
      "hello" -> Set("test1.txt", "test2.txt"),
      "world" -> Set("test1.txt"),
      "and"   -> Set("test1.txt"),
      "test"  -> Set("test1.txt")
    ),
    failedFileNames = Set.empty
  )
}
