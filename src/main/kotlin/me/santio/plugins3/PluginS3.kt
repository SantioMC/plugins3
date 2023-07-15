package me.santio.plugins3

import io.minio.MinioClient
import java.io.File
import kotlin.system.exitProcess

object PluginS3 {
    lateinit var client: MinioClient

    @JvmStatic
    fun main(args: Array<String>) {
        // Get env vars
        val endpoint = System.getenv("MINIO_ENDPOINT")
        val accessKey = System.getenv("MINIO_ACCESS_KEY")
        val secretKey = System.getenv("MINIO_SECRET_KEY")

        client = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()

        println("Connected to $endpoint")

        val bucket = System.getenv("MINIO_BUCKET")
        val directory = System.getenv("MINIO_DIR") ?: ""

        println("Loading files from $bucket/$directory")
        PluginLoader.loadFromBucket(bucket, directory)

        println("Done, beginning clean up")
        val startupCmd = System.getenv("STARTUP_CMD") ?: let {
            val startupFile = File(".startup")

            if (!startupFile.exists())
                error("No startup command found, exiting")

            startupFile.readText().also {
                startupFile.delete()
            }
        }

        for (env in System.getenv().keys) {
            if (env.startsWith("MINIO_")) {
                System.clearProperty(env)
            }
        }

        System.clearProperty("STARTUP_CMD")
        println("Cleaned up, executing: $startupCmd")

        val process = Runtime.getRuntime().exec(startupCmd)

        process.inputStream.copyTo(System.out)
        process.errorStream.copyTo(System.err)

        exitProcess(process.waitFor())
    }
}