package ru.kn_n.pushapp

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {
    @GET("server.php")
    suspend fun haveChanges(
        @Query("action") action: String
    ): HaveChanges

    @GET("server.php")
    suspend fun getMessage(
        @Query("action") action: String
    ): MessageModel
}