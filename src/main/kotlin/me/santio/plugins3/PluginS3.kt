package me.santio.plugins3

import io.minio.MinioClient

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
        val directory = System.getenv("MINIO_DIR")

        println("Loading plugins from $bucket/$directory")
        PluginLoader.loadFromBucket(bucket, directory)

        println("Done, beginning clean up")
        val startupCmd = System.getenv("STARTUP_CMD")

        for (env in System.getenv().keys) {
            if (env.startsWith("MINIO_")) {
                System.clearProperty(env)
            }
        }

        System.clearProperty("STARTUP_CMD")
        println("Cleaned up, executing: $startupCmd")
        Runtime.getRuntime().exec(startupCmd)

    }
}