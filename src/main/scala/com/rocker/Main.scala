package com.rocker

object Main extends App {
  Program
    .readFile(args)
    .fold(
      println,
      folder => {
        val index = Program.index(folder)
        index.failedFileNames.foreach(fileName => println(f"failed to load $fileName"))
        println(f"${index.fileNames.size} files read in directory ${folder.getPath}")
        Program.iterate(index)
      }
    )
}