package com.knoldus
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object ClassAverageDriver extends App {
  val path = "/home/knoldus/Downloads/LSL-P-lightbend-scala-language-professional/StudentGrades/src/main/scala/grades.csv"
  // Creating an instance of GradeCalculator class
  private val gradeCalculator = new GradeCalculator
  val parsedData = gradeCalculator.parseCsv(path)

  // Calculating the average score of each student and storing the result in studentAverages variable
  val studentAverages: Future[List[(String, Double)]] = gradeCalculator.calculateStudentAverages(parsedData)
  private val studentAverageResult = Try(Await.result(studentAverages, 2.seconds))
  studentAverageResult match {
    case Success(studentAverageResult) => println(s"Average of Each and Every Student: $studentAverageResult")
    case Failure(ex) => println(s"Error: ${ex.getMessage}")
  }

  // Calculating the average score of the entire class and storing the result in result variable
  val result: Future[Double] = gradeCalculator.calculateGrades(path)
  private val finalResult = Try(Await.result(result, 2.seconds))
  finalResult match {
    case Success(finalResult) => println(s"Class average: $finalResult")
    case Failure(ex) => println(s"Error: ${ex.getMessage}")
  }
}
