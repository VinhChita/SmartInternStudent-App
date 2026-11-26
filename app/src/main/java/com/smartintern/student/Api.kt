package com.smartintern.student

import okhttp3.MultipartBody
import retrofit2.http.*

interface Api {
    @POST("login") suspend fun login(@Body b: LoginReq): LoginRes
    @POST("register") suspend fun register(@Body b: RegisterReq): MessageRes
    @POST("logout") suspend fun logout(): MessageRes
    @GET("dashboard") suspend fun dashboard(): DashboardRes
    @GET("programs") suspend fun programs(@Query("q") q: String?, @Query("type") type: String?): ProgramsRes
    @POST("programs/{id}/apply") suspend fun apply(@Path("id") id: Int): MessageRes
    @GET("applications") suspend fun applications(): ApplicationsRes
    @GET("profile") suspend fun profile(): ProfileRes
    @Multipart @POST("profile/cv") suspend fun uploadCv(@Part file: MultipartBody.Part): MessageRes
    @DELETE("profile/cv") suspend fun deleteCv(): MessageRes
    @GET("reports") suspend fun reports(@Query("type") type: String): ReportsRes
    @POST("reports") suspend fun createReport(@Body b: ReportReq): ReportDto
    @PUT("reports/{id}") suspend fun updateReport(@Path("id") id: Int, @Body b: ReportReq): ReportDto
    @GET("evaluation") suspend fun evaluation(): EvaluationRes
    @POST("messages") suspend fun sendMessage(@Body b: SendMessageReq): MessageRes
    @GET("notifications") suspend fun notifications(): NotificationsRes
}
