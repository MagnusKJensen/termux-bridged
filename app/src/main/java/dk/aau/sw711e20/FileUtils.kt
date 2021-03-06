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
    return Base64.encodeBase64(bytes)
}

fun decodeData(data: ByteArray): ByteArray {
    return Base64.decodeBase64(data)
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
    val zipOut = ZipOutputStream(BufferedOutputStream(outputStream))
    zipRecursive(zipOut, folderToZip, "")
    zipOut.close()
}

private fun zipRecursive(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {
    val data = ByteArray(2048)
    sourceFile.listFiles()?.forEach { f ->
        val path = (if (parentDirPath.isNotEmpty()) parentDirPath + File.separator else "") + f.name
        if (f.isDirectory) {
            val entry = ZipEntry(path + File.separator) // Separator is added to indicate that this is a folder
            entry.time = f.lastModified()
            entry.isDirectory
            entry.size = f.length()
            zipOut.putNextEntry(entry)
            //Call recursively to add files within this directory
            zipRecursive(zipOut, f, path)
            zipOut.closeEntry()
        } else {
            FileInputStream(f).use { fi ->
                BufferedInputStream(fi).use { origin ->
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
                    zipOut.closeEntry()
                }
            }
        }
    }
}

fun unzipJobToDisk(context: Context, jobData: ByteArray) {
    val targetDir = File(context.filesDir, jobFilesPath)
    Log.i("UNZIP", targetDir.absolutePath)
    unzip(ByteArrayInputStream(jobData), targetDir)
}

fun unzip(inStream: InputStream, outputFolder: File) {
    val zipStream = ZipInputStream(inStream)
    var ze: ZipEntry? = zipStream.nextEntry
    val buffer = ByteArray(2048)
    while (ze != null) {
        val fileName = ze.name
        val newFile = File(outputFolder.path + File.separator + fileName)
        if (ze.isDirectory || fileName.endsWith("\\") || fileName.endsWith(File.separator) || fileName.endsWith("/")) {
            newFile.mkdirs()
        } else {
            File(newFile.parent).mkdirs()
            newFile.createNewFile()
            val fos = FileOutputStream(newFile)
            var len: Int
            while (true) {
                len = zipStream.read(buffer)
                if (len <= 0)
                    break
                fos.write(buffer, 0, len)
            }
            fos.close()
        }

        zipStream.closeEntry()
        ze = zipStream.nextEntry
    }
    zipStream.close()
}

fun deleteJobFiles(context: Context) {
    val jobRootDir = File(context.filesDir, jobFilesPath)
    deleteRecursive(jobRootDir)
}

private fun deleteRecursive(file: File) {
    if (! file.exists()) return

    if (file.isDirectory) {
        for (f in file.listFiles()) {
            deleteRecursive(f)
        }
    }

    file.delete()
}
