package com

package object rocker {
  type FilesWords = Map[String, Set[String]]

  case class Index(fileNames: Set[String],  filesWords: FilesWords, failedFileNames: Set[String])
  case class FileWordsCount(fileName: String, percent: Int)
}
