package me.santio.plugins3

import io.minio.DownloadObjectArgs
import io.minio.ListObjectsArgs
import java.io.File

object PluginLoader {

    fun loadFromBucket(bucket: String, directory: String) {
        val currentDir = File(System.getProperty("user.dir"))
        val pluginsDir = File(currentDir, "plugins")
        if (!pluginsDir.exists()) pluginsDir.mkdir()

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
                    .filename(File(pluginsDir, name).absolutePath)
                    .build()
            )

            println("Downloaded plugin: $name")
        }
    }

}