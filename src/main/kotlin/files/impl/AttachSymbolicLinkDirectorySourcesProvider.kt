package files.impl

import com.intellij.util.io.createSymbolicLink
import files.AttachDirectorySourcesProvider
import java.nio.file.Path

class AttachSymbolicLinkDirectorySourcesProvider : AttachDirectorySourcesProvider {
    override fun attachSourceDirectory(sourcePath: Path, rootSourceDirectoryPath: Path) {
        val fileName = sourcePath.fileName
        val resolve = rootSourceDirectoryPath.resolve(fileName)
        resolve.createSymbolicLink(sourcePath)
    }
}