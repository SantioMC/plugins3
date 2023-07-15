package me.santio.plugins3

import io.minio.DownloadObjectArgs
import io.minio.ListObjectsArgs
import io.minio.errors.MinioException
import java.io.File

object PluginLoader {

    fun loadFromBucket(bucket: String, directory: String) {
        val currentDir = File(System.getProperty("user.dir"))

        val objects = PluginS3.client.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucket)
                .recursive(true)
                .also { if (directory.isNotEmpty()) it.prefix(directory) }
                .build()
        )

        for (obj in objects) {
            val item = obj.get()
            if (item.isDir) continue

            val path = item.objectName()
            val name = path.substring(if (directory.isNotEmpty()) directory.length + 1 else 0)

            // Create any directories that don't exist
            File(name).parentFile?.mkdirs()

            // Download file
            try {
                PluginS3.client.downloadObject(
                    DownloadObjectArgs.builder()
                        .bucket(bucket)
                        .`object`(path)
                        .filename(File(currentDir, name).absolutePath)
                        .overwrite(true)
                        .build()
                )

                println("$path -> $name")
            } catch (e: MinioException) {
                println("Failed to download $path")
                e.printStackTrace()
                println("HTTP Trace:")
                println(e.httpTrace())
            }
        }
    }

}