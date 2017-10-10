package com.artkostm.integrator.watcher

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, SimpleFileVisitor, Files, Path}
import com.artkostm.integrator.watcher.RegistryTypes._

/**
  * Trait for allowing a block of code to be run recursively given a directory path
  */
trait RecursiveFileActions {

  /**
    * Recursively performs an action given a directory path
    *
    * Ignores all paths that are not directories. Uses the Java 7 API to walk
    * a directory tree
    *
    * @param path Path object to a directory
    * @param callback Callback to perform on each subdirectory path
    * @return Unit
    */
  def forEachDir(path: Path)(callback: Callback) = {
    if (path.toFile.isDirectory)
      Files.walkFileTree(path, new SimpleFileVisitor[Path] {
        override def preVisitDirectory(dir: Path, attributes: BasicFileAttributes) = {
          callback(dir)
          FileVisitResult.CONTINUE
        }
      })
  }
}