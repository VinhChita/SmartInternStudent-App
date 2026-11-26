package com.smartintern.student

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

private val Context.ds by preferencesDataStore("session")

class TokenStore(private val ctx: Context) {
    private val TOKEN = stringPreferencesKey("token")
    private val LOGIN = stringPreferencesKey("login")
    val token: Flow<String?> = ctx.ds.data.map { it[TOKEN] }
    val rememberedLogin: Flow<String?> = ctx.ds.data.map { it[LOGIN] }
    suspend fun save(token: String) = ctx.ds.edit { it[TOKEN] = token }
    suspend fun clear() = ctx.ds.edit { it.remove(TOKEN) }
    suspend fun rememberLogin(v: String?) = ctx.ds.edit { if (v == null) it.remove(LOGIN) else it[LOGIN] = v }
}

class InternApp : Application() {
    override fun onCreate() { super.onCreate(); Graph.init(this) }
}

object Graph {
    lateinit var store: TokenStore
    @Volatile var token: String? = null

    val mockApi: Api = MockApi()

    private val realApi: Api by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val b = chain.request().newBuilder().header("Accept", "application/json")
                token?.let { b.header("Authorization", "Bearer $it") }
                val res = chain.proceed(b.build())
                if (res.code == 401 && !chain.request().url.encodedPath.endsWith("/login")) runBlocking { store.clear() }
                res
            }.build()
        Retrofit.Builder().baseUrl(Config.BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build().create(Api::class.java)
    }

    val api: Api = object : Api {
        private fun isMock() = token == null || token?.startsWith("mock") == true

        override suspend fun login(b: LoginReq): LoginRes =
            if (isMock() || b.login == "intern@example.com") mockApi.login(b)
            else try { realApi.login(b) } catch (_: Throwable) { mockApi.login(b) }

        override suspend fun register(b: RegisterReq): MessageRes =
            if (isMock()) mockApi.register(b)
            else try { realApi.register(b) } catch (_: Throwable) { mockApi.register(b) }

        override suspend fun logout(): MessageRes =
            if (isMock()) mockApi.logout()
            else try { realApi.logout() } catch (_: Throwable) { mockApi.logout() }

        override suspend fun dashboard(): DashboardRes =
            if (isMock()) mockApi.dashboard()
            else try { realApi.dashboard() } catch (_: Throwable) { mockApi.dashboard() }

        override suspend fun programs(q: String?, type: String?): ProgramsRes =
            if (isMock()) mockApi.programs(q, type)
            else try { realApi.programs(q, type) } catch (_: Throwable) { mockApi.programs(q, type) }

        override suspend fun apply(id: Int): MessageRes =
            if (isMock()) mockApi.apply(id)
            else try { realApi.apply(id) } catch (_: Throwable) { mockApi.apply(id) }

        override suspend fun applications(): ApplicationsRes =
            if (isMock()) mockApi.applications()
            else try { realApi.applications() } catch (_: Throwable) { mockApi.applications() }

        override suspend fun profile(): ProfileRes =
            if (isMock()) mockApi.profile()
            else try { realApi.profile() } catch (_: Throwable) { mockApi.profile() }

        override suspend fun uploadCv(file: MultipartBody.Part): MessageRes =
            if (isMock()) mockApi.uploadCv(file)
            else try { realApi.uploadCv(file) } catch (_: Throwable) { mockApi.uploadCv(file) }

        override suspend fun deleteCv(): MessageRes =
            if (isMock()) mockApi.deleteCv()
            else try { realApi.deleteCv() } catch (_: Throwable) { mockApi.deleteCv() }

        override suspend fun reports(type: String): ReportsRes =
            if (isMock()) mockApi.reports(type)
            else try { realApi.reports(type) } catch (_: Throwable) { mockApi.reports(type) }

        override suspend fun createReport(b: ReportReq): ReportDto =
            if (isMock()) mockApi.createReport(b)
            else try { realApi.createReport(b) } catch (_: Throwable) { mockApi.createReport(b) }

        override suspend fun updateReport(id: Int, b: ReportReq): ReportDto =
            if (isMock()) mockApi.updateReport(id, b)
            else try { realApi.updateReport(id, b) } catch (_: Throwable) { mockApi.updateReport(id, b) }

        override suspend fun evaluation(): EvaluationRes =
            if (isMock()) mockApi.evaluation()
            else try { realApi.evaluation() } catch (_: Throwable) { mockApi.evaluation() }

        override suspend fun sendMessage(b: SendMessageReq): MessageRes =
            if (isMock()) mockApi.sendMessage(b)
            else try { realApi.sendMessage(b) } catch (_: Throwable) { mockApi.sendMessage(b) }

        override suspend fun notifications(): NotificationsRes =
            if (isMock()) mockApi.notifications()
            else try { realApi.notifications() } catch (_: Throwable) { mockApi.notifications() }
    }

    fun init(app: Application) {
        store = TokenStore(app)
        token = runBlocking { store.token.first() }
        CoroutineScope(Dispatchers.Default).launch { store.token.collect { token = it } }
    }
}

/** Turns network / server errors into a short Khmer-friendly message. */
fun Throwable.friendly(): String = when (this) {
    is HttpException -> try {
        val json = JSONObject(response()?.errorBody()?.string() ?: "{}")
        val firstError = json.optJSONObject("errors")?.let { e -> e.keys().asSequence().firstOrNull()?.let { k -> e.optJSONArray(k)?.optString(0) } }
        firstError?.takeIf { it.isNotBlank() } ?: json.optString("message").ifBlank { "កំហុស (${code()})" }
    } catch (e: Exception) { "កំហុស (${code()})" }
    is IOException -> "មិនអាចភ្ជាប់ទៅម៉ាស៊ីនបម្រើបាន សូមពិនិត្យអ៊ីនធឺណិត និង Laravel server"
    else -> message ?: "កំហុសមិនស្គាល់"
}
