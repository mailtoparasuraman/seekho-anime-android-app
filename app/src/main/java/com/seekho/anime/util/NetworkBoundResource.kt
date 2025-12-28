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
            query().map { Resource.Error(throwable, it) }
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
