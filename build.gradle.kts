import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.7.21"
  id("com.github.johnrengelman.shadow") version "7.1.2"
  kotlin("plugin.serialization") version "1.7.20"
}

group = "dev.pragma"
version = "0.1"

repositories {
  mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

sourceSets["main"].kotlin {
  setSrcDirs(listOf("src", "lib"))
}

sourceSets.create("tools").kotlin {
  setSrcDirs(listOf("tools"))
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "11"
}

val solutions = File("src")
  .listFiles { _, name -> name.startsWith("day") && name.endsWith(".kt") }
  .map { file -> file.run { name.substring(3, name.length - 3) } }

fun inputfile(default: String): String {
  if (project.hasProperty("inputfile"))
    return project.property("inputfile") as String
  return System.getenv("AOC_INPUT_FILE") ?: default
}

for (solution in solutions) {
  val day = Regex("""\d+""").find(solution)!!.value
  tasks.register<JavaExec>("run$solution") {
    jvmArgs = listOf("-Xmx8g")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("day${solution}.Day${solution}Kt")
    environment("AOC_INPUT_FILE", inputfile("input/input${day}.txt"))
  }
  tasks.register<JavaExec>("test$solution") {
    jvmArgs = listOf("-Xmx8g")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("day${solution}.Day${solution}Kt")
    environment("AOC_INPUT_FILE", inputfile("input/test${day}.txt"))
  }
  tasks.register<JavaExec>("fetch$solution") {
    classpath = sourceSets["tools"].runtimeClasspath
    mainClass.set("fetch.FetchKt")
    args(listOf(day))
  }
  tasks.register<Exec>("native$solution") {
    dependsOn(tasks.shadowJar)
    val jarfile = tasks.shadowJar.get().outputs.files.asPath
    val outfile = "bin/day${solution}"
    inputs.file(jarfile)
    outputs.file(outfile)
    File("bin").mkdirs()
    commandLine(
      "native-image",
      "--report-unsupported-elements-at-runtime",
      "-cp",
      jarfile,
      "day${solution}.Day${solution}Kt",
      "-o",
      outfile
    )
  }
}

