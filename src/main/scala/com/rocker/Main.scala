package com.rocker

import scala.io.StdIn.readLine

object Main extends App {
  Program
    .readFile(args)
    .fold(
      println,
      folder => {
        val index = Program.index(folder)
        index.failedFileNames.foreach(fileName => println(f"failed to load $fileName"))
        println(f"${index.fileNames.size} files read in directory ${folder.getPath}")
        iterate(index)
      }
    )

  private def iterate(index: Index): Unit = {
    print(s"search> ")
    val searchString = readLine()

    Program.countWordsInFiles(index, searchString)
      .foreach(result => println(f"${result.fileName}: ${result.percent}%%"))

    iterate(index)
  }
}