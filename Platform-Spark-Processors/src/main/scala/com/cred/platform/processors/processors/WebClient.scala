package com.cred.platform.processors.processors

import okhttp3.{OkHttpClient, Request, Response}

import java.util.concurrent.TimeUnit

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
      .url(s"$baseUrl/api/v1/events/onboarding/$jobId")
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