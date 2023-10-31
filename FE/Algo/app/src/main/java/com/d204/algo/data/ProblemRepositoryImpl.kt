package com.d204.algo.data

import android.util.Log
import com.d204.algo.data.model.Problem
import com.d204.algo.data.repository.ProblemRepository
import com.d204.algo.data.source.datasource.ProblemDataSourceFactory
import com.d204.algo.remote.mapper.ProblemMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "Algo_ProblemRepositoryImpl"
class ProblemRepositoryImpl @Inject constructor(
    private val dataSourceFactory: ProblemDataSourceFactory,
    private val problemMapper: ProblemMapper,
) : ProblemRepository {
    override suspend fun getProblems(): Flow<List<Problem>> = flow {
        val isRemote = dataSourceFactory.getRemoteDataSource().isRemote()
        emit(dataSourceFactory.getDataSource(isRemote).getProblems())
    }

    override suspend fun getProblem(problemId: Long): Flow<Problem> = flow {
        val isRemote = dataSourceFactory.getRemoteDataSource().isRemote()
        emit(dataSourceFactory.getDataSource(isRemote).getProblem(problemId))
    }

    override suspend fun getStrongProblems(userId: Long): Flow<List<Problem>> = flow {
        val isRemote = dataSourceFactory.getRemoteDataSource().isRemote()
        emit(dataSourceFactory.getDataSource(isRemote).getStrongProblems(userId))
    }

    override suspend fun getWeakProblems(userId: Long): Flow<List<Problem>> = flow {
        val isRemote = dataSourceFactory.getRemoteDataSource().isRemote()
        emit(dataSourceFactory.getDataSource(isRemote).getWeakProblems(userId))
    }

    override suspend fun getSimilarProblems(userId: Long): Flow<List<Problem>> = flow {
        val isRemote = dataSourceFactory.getRemoteDataSource().isRemote()
        emit(dataSourceFactory.getDataSource(isRemote).getSimilarProblems(userId))
    }

    override suspend fun postLikeProblems(problem: Problem): Flow<Unit> = flow {
        val isRemote = dataSourceFactory.getRemoteDataSource().isRemote()
        Log.d("클릭리포지터리임펠", "postLikeProblems: $problem")
        emit(dataSourceFactory.getDataSource(isRemote).postLikeProblems(problem))
    }

//    override suspend fun postLikeProblems(problem: Problem) {
//        val isRemote = dataSourceFactory.getRemoteDataSource().isRemote()
//        Log.d("클릭리포지터리임펠", "postLikeProblems: $problem")
//        dataSourceFactory.getDataSource(isRemote).postLikeProblems(problem)
//    }

    override suspend fun getLikeProblems(userId: Long): Flow<List<Problem>> = flow {
        val isRemote = dataSourceFactory.getRemoteDataSource().isRemote()
        emit(dataSourceFactory.getDataSource(isRemote).getLikeProblems(userId))
    }
}
