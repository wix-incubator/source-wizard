package plugin.extensions

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.IndexableSetContributor
import infra.SourceCacheDirectory

class IndexableNpmSourcesContributor : IndexableSetContributor() {
    override fun getAdditionalRootsToIndex(): MutableSet<VirtualFile> {
        val sourcesDirectory = SourceCacheDirectory.getSourceCacheDirectoryFile()
        return mutableSetOf(sourcesDirectory)
    }
}