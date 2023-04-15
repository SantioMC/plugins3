package me.santio.plugins3

import io.minio.DownloadObjectArgs
import io.minio.ListObjectsArgs

object PluginLoader {

    fun loadFromBucket(bucket: String, directory: String) {
        val objects = PluginS3.client.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix("$directory/")
                .build()
        )

        for (obj in objects) {
            val item = obj.get()
            if (item.isDir) continue

            val path = item.objectName()
            val name = path.substringAfterLast("/")

            // Download file
            PluginS3.client.downloadObject(
                DownloadObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(path)
                    .filename(name)
                    .build()
            )

            println("Downloaded plugin: $name")
        }

        val startupCmd = System.getenv("STARTUP_CMD")
        println("Done, ready for server start")
        println("Command executing: $startupCmd")

        Runtime.getRuntime().exec(startupCmd)
    }

}