package de.goork.mapflip.baselineprofile

import android.content.Intent
import android.net.Uri
import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Macrobenchmark verifying cold startup performance for MainActivity and RedirectActivity
 * with and without Baseline Profile compilation.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    private val packageName: String
        get() = InstrumentationRegistry.getArguments().getString("targetAppId") ?: "de.goork.mapflip"

    @Test
    fun startupMainActivityNoCompilation() = benchmarkMainActivity(CompilationMode.None())

    @Test
    fun startupMainActivityBaselineProfile() = benchmarkMainActivity(
        CompilationMode.Partial(BaselineProfileMode.Require)
    )

    @Test
    fun startupRedirectHotpathBaselineProfile() {
        rule.measureRepeated(
            packageName = packageName,
            metrics = listOf(StartupTimingMetric()),
            compilationMode = CompilationMode.Partial(BaselineProfileMode.Require),
            startupMode = StartupMode.COLD,
            iterations = 5,
            setupBlock = {
                pressHome()
            }
        ) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://maps.apple.com/?ll=52.5200,13.4050&q=Berlin")
            ).apply {
                setPackage(packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivityAndWait(intent)
        }
    }

    private fun benchmarkMainActivity(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = packageName,
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 5,
            setupBlock = {
                pressHome()
            }
        ) {
            startActivityAndWait()
        }
    }
}
