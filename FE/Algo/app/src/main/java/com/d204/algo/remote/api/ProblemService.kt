package com.d204.algo.remote.api

import com.d204.algo.remote.model.ProblemModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProblemService {
    @GET("api/problem")
    suspend fun getProblems(): List<ProblemModel>

    @GET("api/problem/{id}")
    suspend fun getProblem(@Path("id") id: Long): ProblemModel

    @GET("recommend/strong/{userId}")
    suspend fun getStrongProblems(@Path("userId") userId: Long): List<ProblemModel>

    @GET("recommend/weak/{userId}")
    suspend fun getWeakProblems(@Path("userId") userId: Long): List<ProblemModel>

    @GET("recommend/similar/{userId}")
    suspend fun getSimilarProblems(@Path("userId") userId: Long): List<ProblemModel>

    @POST("recommend/like")
<<<<<<< HEAD
    suspend fun postLikeProblems(@Body problem: ProblemModel): Unit
=======
    suspend fun postLikeProblems(@Body problem: ProblemModel)
>>>>>>> apiService

    @GET("my/like/{userId}")
    suspend fun getLikeProblems(@Path("userId") userId: Long): List<ProblemModel>
}
