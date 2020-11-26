package dk.aau.sw711e20

import android.content.Context
import android.util.Base64
import java.io.*
import java.io.File.*

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

const val mainFolderName = "jobs"
val jobFolderPath = separator + mainFolderName + separator
const val jobFilesFolderName = "programfiles"
val jobFilesPath = jobFolderPath + jobFilesFolderName + separator
const val resultsFolderName = "results"
val resultsFilePath = jobFilesPath + separator + resultsFolderName

fun encodeFileForUpload(path: String): ByteArray {
    return Base64.encode(File(path).readBytes(), 0)
}

fun decodeDownloadedFile(data: ByteArray): ByteArray {
    return Base64.decode(data, 0)
}

fun unzipJobToDisk(context: Context, jobData: ByteArray) {
    val zipStream = ZipInputStream(ByteArrayInputStream(jobData))

    var ze: ZipEntry? = zipStream.nextEntry
    val buffer = ByteArray(1024)
    while (ze != null) {
        val fileName = ze.name
        val newFile = File(context.filesDir, jobFilesPath + separator + fileName)
        println("Unzipping to $jobFilesPath")

        //create directories for sub directories in zip
        File(newFile.parent).mkdirs()

        val fos = FileOutputStream(newFile)
        var len: Int
        while (true) {
            len = zipStream.read(buffer)
            if (len <= 0)
                break
            fos.write(buffer, 0, len)
        }

        fos.close()
        zipStream.closeEntry()
        ze = zipStream.nextEntry
    }

    // ToDo: Do we need to also close bytearray stream?
    zipStream.close()
}

fun createDirIfNotExisting(context: Context, targetDirectory: String): File {
    val newDir = File(targetDirectory)
    if (!newDir.exists()) newDir.mkdirs()
    return newDir
}

fun deleteJob(context: Context): File {
    throw NotImplementedError()
}

fun zipDir(directory: String, destPath: String) {
    val sourceFile = File(directory)

    ZipOutputStream(BufferedOutputStream(FileOutputStream(destPath))).use {
        it.use {
            zipFiles(it, sourceFile, "")
        }
    }
}

private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {
    val data = ByteArray(2048)

    for (f in sourceFile.listFiles()) {
        if (f.isDirectory) {
            val entry = ZipEntry(f.name + separator)
            entry.time = f.lastModified()
            entry.isDirectory
            entry.size = f.length()

            zipOut.putNextEntry(entry)

            //Call recursively to add files within this directory
            zipFiles(zipOut, f, f.name)
        } else {
            if (!f.name.contains(".zip")) { //If folder contains a file with extension ".zip", skip it
                FileInputStream(f).use { fi ->
                    BufferedInputStream(fi).use { origin ->
                        val path = parentDirPath + separator + f.name
                        val entry = ZipEntry(path)
                        entry.time = f.lastModified()
                        entry.isDirectory
                        entry.size = f.length()
                        zipOut.putNextEntry(entry)
                        while (true) {
                            val readBytes = origin.read(data)
                            if (readBytes == -1) {
                                break
                            }
                            zipOut.write(data, 0, readBytes)
                        }
                    }
                }
            } else {
                zipOut.closeEntry()
                zipOut.close()
            }
        }
    }
}
