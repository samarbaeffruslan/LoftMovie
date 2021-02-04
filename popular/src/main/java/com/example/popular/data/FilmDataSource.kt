package com.example.popular.data


import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.core.db.dao.FilmDao
import com.example.core.db.dao.entities.FilmEntity
import com.example.core.network.responses.FilmDTO.FilmDTO
import com.example.core.network.responses.FilmDTO.FilmResultResponse
import com.example.core.network.service.MovieService
import com.example.core.utils.Resource
import com.example.popular.mapper.Mapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

class FilmDataSource @Inject constructor(
    private val service: MovieService,
    private val dao: FilmDao,
) : PageKeyedDataSource<Int, FilmDTO>() {

    val state: MutableLiveData<Resource<FilmResultResponse>> = MutableLiveData()

    private val mapper = Mapper()


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, FilmDTO>,
    ) {
        state.postValue(Resource.Loading())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getPopularFilms(page = 1)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        state.postValue(Resource.Success(resultResponse))
                        dao.insertAll(resultResponse.items.let {
                            mapper.map(it)
                        })
                        callback.onResult(resultResponse.items, null, 2)
                    }
                } else {
                    state.postValue(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> state.postValue(Resource.Error("No internet connection"))
                }
            }

        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, FilmDTO>) {
        state.postValue(Resource.Loading())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getPopularFilms(page = params.key)
                if (response.isSuccessful) {

                    response.body()?.let { resultResponse ->
                        state.postValue(Resource.Success(resultResponse))
                        dao.insertAll(resultResponse.items.let {
                            mapper.map(it)
                        })
                        callback.onResult(resultResponse.items, params.key + 1)
                    }
                } else {
                    state.postValue(Resource.Error(response.message()))
                }


            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> state.postValue(Resource.Error("No internet connection"))
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, FilmDTO>) {


    }


}
