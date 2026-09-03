package de.goork.mapflip.analytics

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/**
 * Play Store Flavor Analytics implementation powered by self-hosted Aptabase.
 * Collects strictly anonymous, privacy-friendly telemetry (no PII, no URLs, no coordinates).
 *
 * Automatically filters Google Play Robo-tests, Firebase Test Labs, and emulators into the debug dataset.
 */
object Analytics {
    private const val TAG = "MapFlipAnalytics"
    private const val APP_KEY = "A-SH-1812872922"
    private const val CUSTOM_HOST = "https://telemetry-apps.goork.de"
    private const val SDK_VERSION = "aptabase-android@1.0.0"
    private const val SESSION_TIMEOUT_MS = 60 * 60 * 1000L // 1 hour

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isInitialized = false

    private var appVersion = "1.2.12"
    private var appBuildNumber = "315015"
    private var isDebug = false
    private var osVersion = ""
    private var locale = ""

    private var currentSessionId = ""
    private var lastEventTimestamp = 0L

    fun init(context: Context) {
        if (isInitialized) return
        try {
            val appContext = context.applicationContext
            isDebug = isDebugOrTestEnvironment(appContext)
            osVersion = Build.VERSION.RELEASE ?: ""
            locale = Locale.getDefault().toLanguageTag()

            val pInfo = try {
                appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            } catch (_: Exception) {
                null
            }

            if (pInfo != null) {
                appVersion = pInfo.versionName ?: "1.2.12"
                appBuildNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pInfo.longVersionCode.toString()
                } else {
                    @Suppress("DEPRECATION")
                    pInfo.versionCode.toString()
                }
            }

            refreshSessionId()
            isInitialized = true
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to initialize Analytics", e)
        }
    }

    private fun isDebugOrTestEnvironment(context: Context): Boolean {
        return try {
            val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            val isTestLab = Settings.System.getString(context.contentResolver, "firebase.test.lab") == "true"
            val isTestHarness = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityManager.isRunningInUserTestHarness()
            val isEmulator = Build.FINGERPRINT.startsWith("generic") ||
                    Build.FINGERPRINT.startsWith("unknown") ||
                    Build.BRAND.startsWith("generic") ||
                    Build.DEVICE.startsWith("generic") ||
                    Build.MODEL.contains("google_sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK built for x86") ||
                    Build.MANUFACTURER.contains("Genymotion") ||
                    Build.MANUFACTURER.contains("BlueStacks") ||
                    Build.MANUFACTURER.contains("nox") ||
                    Build.HARDWARE.contains("goldfish") ||
                    Build.HARDWARE.contains("ranchu") ||
                    Build.HARDWARE.contains("cutf") ||
                    Build.HARDWARE.contains("vbox86") ||
                    Build.HARDWARE.contains("nox") ||
                    Build.PRODUCT.contains("sdk_google") ||
                    Build.PRODUCT.contains("google_sdk") ||
                    Build.PRODUCT.contains("sdk") ||
                    Build.PRODUCT.contains("sdk_x86") ||
                    Build.PRODUCT.contains("vbox86p") ||
                    Build.PRODUCT.contains("emulator") ||
                    Build.PRODUCT.contains("simulator") ||
                    Build.PRODUCT.contains("nox") ||
                    Build.PRODUCT.startsWith("aosp_cf_") ||
                    Build.PRODUCT.startsWith("vsoc_") ||
                    Build.DEVICE.startsWith("vsoc") ||
                    Build.BOARD.contains("cutf") ||
                    Build.BOARD.contains("nox") ||
                    Build.HOST == "android-test" ||
                    System.getProperty("ro.kernel.qemu") == "1"

            isDebuggable || isTestLab || isTestHarness || isEmulator
        } catch (_: Throwable) {
            false
        }
    }

    private fun refreshSessionId() {
        currentSessionId = UUID.randomUUID().toString().lowercase()
        lastEventTimestamp = System.currentTimeMillis()
    }

    private fun getValidSessionId(): String {
        val now = System.currentTimeMillis()
        if (currentSessionId.isEmpty() || (now - lastEventTimestamp) > SESSION_TIMEOUT_MS) {
            refreshSessionId()
        }
        lastEventTimestamp = now
        return currentSessionId
    }

    private fun getIso8601Timestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(Date())
    }

    fun trackEvent(eventName: String, properties: Map<String, Any> = emptyMap()) {
        if (!isInitialized) return
        scope.launch {
            try {
                val systemPropsObj = JSONObject().apply {
                    put("isDebug", isDebug)
                    put("osName", "Android")
                    put("osVersion", osVersion)
                    put("locale", locale)
                    put("appVersion", appVersion)
                    put("appBuildNumber", appBuildNumber)
                    put("sdkVersion", SDK_VERSION)
                }

                val propsObj = JSONObject()
                properties.forEach { (k, v) ->
                    propsObj.put(k, v)
                }

                val payload = JSONObject().apply {
                    put("timestamp", getIso8601Timestamp())
                    put("sessionId", getValidSessionId())
                    put("eventName", eventName)
                    put("systemProps", systemPropsObj)
                    put("props", propsObj)
                }

                val url = URL("$CUSTOM_HOST/api/v0/event")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 10000
                    readTimeout = 10000
                    doOutput = true
                    setRequestProperty("App-Key", APP_KEY)
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                }

                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(payload.toString())
                    writer.flush()
                }

                connection.responseCode // Triggers request execution
                connection.disconnect()
            } catch (_: Throwable) {
                // Silently swallow analytics errors to never impact UX
            }
        }
    }
}
