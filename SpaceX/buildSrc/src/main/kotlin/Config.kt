import org.gradle.api.JavaVersion

object Config {
    const val minSdk = 27
    const val targetSdk = 33
    const val compileSdk = 33
    const val versionCode = 1
    const val versionName = "1.0"
    val javaVersion = JavaVersion.VERSION_11
}