package com.blueduck.crud

import com.blueduck.crud.model.Student
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StudentApiService {
    @GET("getStudents")
    suspend fun getStudents(): List<Student>

    @POST("addStudent")
    suspend fun createStudent(@Body student: Student): Boolean

    @PUT("updateStudent")
    suspend fun updateStudent(@Body student: Student): Boolean

    @GET("removeStudent")
    suspend fun removeStudent(@Query("phone") phone: Int) : Boolean
}
