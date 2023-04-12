package com.knoldus
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.{Failure, Success, Try}

case class Grade(studentID: String, english: Double, physics: Double, chemistry: Double, maths: Double)

object Grade {
  // A companion object for the Grade case class
  def fromMap(map: Map[String, String]): Grade = {
    Grade(
      map("StudentID"),
      map("English").toDouble,
      map("Physics").toDouble,
      map("Chemistry").toDouble,
      map("Maths").toDouble
    )
  }
}

class GradeCalculator {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  // A method to parse a CSV file and return a Future of a list of maps which representing the CSV data
  def parseCsv(filePath: String): Future[List[Map[String, String]]] = Future {
    val file = Source.fromFile(filePath)
    val result = Try {
      val lines = file.getLines().toList
      val headers = lines.head.split(",").map(_.trim)
      val rows = lines.tail
      rows.map(row => {
        val cells = row.split(",").map(_.trim)
        headers.zip(cells).toMap
      })
    }
    file.close()
    result match {
      case Success(data) => data
      case Failure(ex) => throw new Exception(s"Failed to parse CSV file '$filePath': ${ex.getMessage}")
    }
  }

  // A method to calculate the averages for each student in a list of maps and return a Future of a list of tuples
  def calculateStudentAverages(data: Future[List[Map[String, String]]]): Future[List[(String, Double)]] = {
    data.map { dataList =>
      dataList.map { dataMap =>
        val grade = Grade.fromMap(dataMap)
        val avg = (grade.english + grade.physics + grade.chemistry + grade.maths) / 4.0
        (grade.studentID, avg)
      }
    }
  }

  // A method to calculate the class average from a list of tuples and return a Future of the class average
  def calculateClassAverage(studentAverages: Future[List[(String, Double)]]): Future[Double] = {
    studentAverages.map { studentAveragesList =>
      val sum = studentAveragesList.map(_._2).sum
      val count = studentAveragesList.length
      sum / count
    }
  }

  // A method to calculate the overall class average for a CSV file at the given path, and return a Future of the class average
  def calculateGrades(path: String): Future[Double] = (for {
    parsedData <- parseCsv(path)
    studentAverages <- calculateStudentAverages(Future(parsedData))
    classAverage <- calculateClassAverage(Future(studentAverages))
  } yield classAverage).recover {
      case ex => throw new Exception(s"Error calculating grades: ${ex.getMessage}")
  }

}

}
