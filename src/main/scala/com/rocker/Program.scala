package com.rocker

import scala.util.{Try, Using}
import scala.io.Source._
import java.io.File

import com.rocker.ReadFileError._

object Program {

  def readFile(args: Array[String]): Either[ReadFileError, File] = {
    for {
      path <- args.headOption.toRight(MissingPathArg)
      file <- Try(new java.io.File(path))
        .fold(
          throwable => Left(FileNotFound(throwable)),
          file =>
            if (file.isDirectory) Right(file)
            else Left(NotDirectory(s"Path [$path] is not a directory"))
        )
    } yield file
  }

  private def extractWords(line: String): List[String] = {
    line
      .replaceAll("[^a-zA-Z ]", "")
      .toLowerCase()
      .split("\\s+")
      .toList
  }

  private def readWords(file: File): Try[List[String]] = {
    Using(fromFile(file)) { source =>
      source.getLines().flatMap(extractWords).toList
    }
  }

  def index(folder: File): Index = {
    val loadedFiles = for {
      file <- folder.listFiles().toList if file.isFile
      result = readWords(file)
        .fold(
          _     => Left(file.getName),
          words => Right(words.map(file.getName -> _))
        )
    } yield result

    val (failedFileNames, words) =
      loadedFiles.foldLeft(Set.empty[String], List.empty[(String, String)]) {
        case ((failedFileNames, allWords), file) => file match {
          case Left(fileName)        => (failedFileNames + fileName, allWords)
          case Right(extractedWords) => (failedFileNames, extractedWords ++ allWords)
        }
      }

    val fileNames = words.groupBy { case (file, _) => file }.keySet

    val filesWords = words
      .groupBy { case (_, word) => word }
      .view
      .mapValues(_.map { case (fileName, _) => fileName }.toSet)
      .toMap

    Index(fileNames, filesWords, failedFileNames)
  }

  def countWordsInFiles(index: Index, searchString: String, top: Int = 10): List[FileWordsCount] = {
    val words = extractWords(searchString)

    val fileWordsCounted = words
      .flatMap(index.filesWords.get)
      .flatten
      .groupBy(identity)
      .map { case(fileName, fileWords) => FileWordsCount(fileName, fileWords.size * 100 / words.size) }
      .toList

    val allFilesCounted =
      fileWordsCounted ++ (index.fileNames -- fileWordsCounted.map(_.fileName).toSet).map(FileWordsCount(_, 0))

    allFilesCounted.sortBy(_.percent)(Ordering[Int].reverse).take(top)
  }
}
