package me.santio.plugins3

import io.minio.DownloadObjectArgs
import io.minio.ListObjectsArgs
import java.io.File

object PluginLoader {

    fun loadFromBucket(bucket: String, directory: String) {
        val currentDir = File(System.getProperty("user.dir"))

        val objects = PluginS3.client.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix("$directory/")
                .recursive(true)
                .build()
        )

        for (obj in objects) {
            val item = obj.get()
            if (item.isDir) continue

            val path = item.objectName()
            val name = path.substring(directory.length + 1)

            // Create any directories that don't exist
            File(name).parentFile?.mkdirs()

            // Download file
            PluginS3.client.downloadObject(
                DownloadObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(path)
                    .filename(File(currentDir, name).absolutePath)
                    .overwrite(true)
                    .build()
            )

            println("$path -> $name")
        }
    }

}