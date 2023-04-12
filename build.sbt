ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "StudentGrades",
    idePackagePrefix := Some("com.knoldus")
  )

val scalaTestVersion = "3.2.15"
libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % Test
