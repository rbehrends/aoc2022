package fetch

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.io.File

fun main(args: Array<String>) {
  val day = args[0]
  val cookie = File(System.getProperty("user.home") + "/.aoc_cookie.txt")
        .readText().trim()
  val client = HttpClient.newBuilder().build()
  val request = HttpRequest.newBuilder()
    .uri(URI.create("https://adventofcode.com/2022/day/$day/input"))
    .header("Cookie", "session=$cookie")
    .build()
  val response = client.send(request, HttpResponse.BodyHandlers.ofString())
  val file = File("input/input$day.txt")
  file.writeText(response.body())
}
