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
        PluginLoader.loadFromBucket(bucket, directory)
    }
}