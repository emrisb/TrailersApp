package com.an.trailers.data

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.*

abstract class NetworkBoundResource<ResultType, RequestType> @MainThread
protected constructor() {

    private val asFlow: Flow<Resource<ResultType>>

    init {
        val source: Flow<Resource<ResultType>>
        if (shouldFetch()) {

            source = createCall()
                .onEach {
                    saveCallResult(processResponse(it)!!)
                }

                .flatMapConcat {
                    loadFromDb()
                        .map { Resource.success(it) }
                }

                .catch { onFetchFailed() }

                .catch { t: Throwable ->
                    emitAll(loadFromDb().map {
                        Resource.error(t.message!!, it)
                    })
                }

        } else {
            source = loadFromDb()
                .map { Resource.success(it) }
        }

        asFlow = loadFromDb()
            .map { Resource.loading(it) }
            .take(1)
            .onCompletion { emitAll(source) }
    }

    fun getAsFlow(): Flow<Resource<ResultType>> {
        return asFlow
    }

    private fun onFetchFailed() {}

    protected fun processResponse(response: Resource<RequestType>): RequestType? {
        return response.data
    }

    protected abstract suspend fun saveCallResult(item: RequestType)

    protected abstract fun shouldFetch(): Boolean

    protected abstract fun loadFromDb(): Flow<ResultType>

    protected abstract fun createCall(): Flow<Resource<RequestType>>
}