package infra

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Files
import java.nio.file.Path

object SourceCacheDirectory {

    const val sourceCacheDirectoryName: String = ".npm-src"

    fun getPathToSourceCacheDirectory(): Path =
        Path.of(FileUtil.expandUserHome("~/$sourceCacheDirectoryName"))

    fun getSourceCacheDirectoryFile(): VirtualFile {
        val sourcesDirectoryPath = getPathToSourceCacheDirectory()
        if (Files.notExists(sourcesDirectoryPath)) {
            Files.createDirectory(sourcesDirectoryPath)
        }

        return VfsUtil.findFile(sourcesDirectoryPath, true)!!
    }
}