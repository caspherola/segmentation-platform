package com.cred.platform.engine.utility

import okhttp3.{OkHttpClient, Request, Response}

import java.util.concurrent.TimeUnit
import scala.util.{Failure, Success, Try}

object WebClient {
  private val client = new OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  private val baseUrl = "http://localhost:8080"  // Replace with your actual base URL

  /**
   * Fetches data from the API for a given job ID
   *
   * @param jobId The job identifier to fetch data for
   * @return Either a String containing the response or an error message
   */
  def fetchJobData(jobId: String): Option[String] = {
    var result: String=null;
    val request = new Request.Builder()
      .url(s"$baseUrl/api/v1/pipeline-configurations/$jobId")
      .get()
      .build()


      val response: Response = client.newCall(request).execute()
        if (response.isSuccessful) {
          val responseBody = response.body().string()
          return Option[String](responseBody)
        }
      Option[String](null)
  }
}