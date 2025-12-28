package com.seekho.anime.util

import kotlinx.coroutines.flow.*

fun <ResultType, RequestType> networkBoundResource(
    query: () -> Flow<ResultType>,
    fetch: suspend () -> RequestType,
    saveFetchResult: suspend (RequestType) -> Unit,
    shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()
    
    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))
        
        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
            
        } catch (throwable: Throwable) {
            val error = when {
                throwable is retrofit2.HttpException && throwable.code() == 429 -> {
                    Exception("Rate limit exceeded. Please wait a moment.")
                }
                throwable is java.io.IOException -> {
                    Exception("No internet connection. Please check your network.")
                }
                else -> throwable
            }
            query().map { Resource.Error(error, it) }
        }
        
    } else {
        query().map { Resource.Success(it) }
    }
    
    emitAll(flow)
}

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : Resource<T>(data, throwable)
}
