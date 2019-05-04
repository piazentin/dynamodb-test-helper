package com.piazentin.dynamodb.tester

import com.almworks.sqlite4java.SQLite
import com.almworks.sqlite4java.SQLiteException
import com.piazentin.dynamodb.tester.SQLiteHelper.OperatingSystem.*
import com.piazentin.dynamodb.tester.SQLiteHelper.SystemArchitecture.*
import java.io.File
import java.util.*

internal object SQLiteHelper {

    enum class OperatingSystem {
        WINDOWS, OS_X, LINUX, ANDROID
    }

    enum class SystemArchitecture {
        X_86, X_64
    }

    fun loadLibrary() {

        if (isLibLoaded()) return

        val resource = "sqlite-libs/${resolveLibName()}"
        val url = Thread.currentThread().contextClassLoader.getResource(resource)
        if (url.protocol == "jar") loadFromJar(resource)
        else System.load(File(url.path).absolutePath)
    }

    private fun resolveLibName() = getLibName(getOs(), getArch())

    private fun isLibLoaded() = try {
        SQLite.getSQLiteVersionNumber()
        true
    } catch (ex: SQLiteException) {
        false
    }

    private fun loadFromJar(resource: String) {

        val file = File.createTempFile("temp", resolveLibName())
        val libBytes = with(Thread.currentThread().contextClassLoader.getResourceAsStream(resource)) {
            this.readBytes()
        }

        file.writeBytes(libBytes)
        file.deleteOnExit()
        file.absolutePath
        file.toPath().parent.toString()
        System.load(file.absolutePath)
        file.delete()
    }

    private fun getArch() =
            System.getProperty("os.arch").orEmpty().toLowerCase(Locale.US).let {
                if (it.contains("64")) X_64
                else X_86
            }

    private fun getOs(): OperatingSystem {

        val os = System.getProperty("os.name").orEmpty().toLowerCase(Locale.US)
        val isAndroid = System.getProperty("java.runtime.name").orEmpty().toLowerCase(Locale.US).contains("android")
        return when {
            isAndroid -> ANDROID
            os.startsWith("windows") -> WINDOWS
            os.startsWith("mac") || os.startsWith("darwin") || os.startsWith("os x") -> OS_X
            else -> LINUX
        }
    }

    private fun getLibName(os: OperatingSystem, arch: SystemArchitecture) =
            when(os) {
                WINDOWS ->
                    if (arch == X_64) "sqlite4java-win32-x64-1.0.392.dll"
                    else "sqlite4java-win32-x86-1.0.392.dll"
                LINUX ->
                    if (arch == X_64) "libsqlite4java-linux-amd64-1.0.392.so"
                    else "libsqlite4java-linux-i386-1.0.392.so"
                OS_X -> "libsqlite4java-osx-1.0.392.dylib"
                else -> "libsqlite4java-linux-i386-1.0.392.so"
            }
}