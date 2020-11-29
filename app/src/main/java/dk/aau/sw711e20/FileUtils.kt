package dk.aau.sw711e20

import android.content.Context
import android.util.Log
import org.apache.commons.codec.binary.Base64

import java.io.*
import java.io.File.*
import java.util.*

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
    return encodeData(File(path).readBytes())
}

fun encodeData(bytes: ByteArray): ByteArray {
    Log.i("test", Arrays.toString(bytes))
    return Base64.encodeBase64(bytes)
}

fun decodeData(data: ByteArray): ByteArray {
    return Base64.decodeBase64(data)
}

fun unzipJobToDisk(context: Context, jobData: ByteArray) {
    val zipStream = ZipInputStream(ByteArrayInputStream(jobData))

    var ze: ZipEntry? = zipStream.nextEntry
    val buffer = ByteArray(1024)
    while (ze != null) {
        val fileName = ze.name
        val newFile = File(context.filesDir, jobFilesPath + fileName)

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

fun zipResult(context: Context): ByteArray {
    val resultFolder = File(context.filesDir, resultsFilePath)
    val byteStream = ByteArrayOutputStream()
    zipAll(resultFolder, byteStream)
    return byteStream.toByteArray()
}

fun createDirIfNotExisting(context: Context, targetDirectory: String): File {
    val newDir = File(targetDirectory)
    if (!newDir.exists()) newDir.mkdirs()
    return newDir
}

fun zipAll(folderToZip: File, outputStream: OutputStream) {
    zipRecursive(ZipOutputStream(outputStream), folderToZip, "")
}

private fun zipRecursive(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {
    val data = ByteArray(2048)
    sourceFile.listFiles()?.forEach { f ->
        if (f.isDirectory) {
            val path = if (parentDirPath == "") {
                f.name
            } else {
                parentDirPath + File.separator + f.name
            }
            val entry = ZipEntry(path + File.separator)
            entry.time = f.lastModified()
            entry.isDirectory
            entry.size = f.length()
            zipOut.putNextEntry(entry)
            //Call recursively to add files within this directory
            zipRecursive(zipOut, f, path)
        } else {
            FileInputStream(f).use { fi ->
                BufferedInputStream(fi).use { origin ->

                    val path = (if (parentDirPath.isNotEmpty()) parentDirPath + File.separator else "") + f.name

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
        }
        zipOut.closeEntry()
    }

    zipOut.close()
}

fun deleteJobFiles(context: Context) {
    val jobRootDir = File(context.filesDir, jobFilesFolderName)
    deleteRecursive(jobRootDir)
}

private fun deleteRecursive(file: File) {
    if (file.isDirectory) {
        for (f in file.listFiles()) {
            deleteRecursive(f)
        }
    }

    file.delete()
}
