package plugin.extensions

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider
import com.intellij.openapi.roots.SyntheticLibrary
import com.intellij.openapi.vfs.VirtualFile
import infra.SourceCacheDirectory
import javax.swing.Icon

class NpmSourcesLibraryRootProvider : AdditionalLibraryRootsProvider() {

    override fun getAdditionalProjectLibraries(project: Project): MutableCollection<SyntheticLibrary> {
        val rootDirectoryFile = SourceCacheDirectory.getSourceCacheDirectoryFile()
        val library = NpmSourcesSyntheticLibrary(rootDirectoryFile)
        return mutableListOf(library)
    }

    override fun getRootsToWatch(project: Project): MutableCollection<VirtualFile> {
        val rootDirectoryFile = SourceCacheDirectory.getSourceCacheDirectoryFile()
        return mutableListOf(rootDirectoryFile)
    }
}

class NpmSourcesSyntheticLibrary(private val libraryRootDirectory: VirtualFile) : SyntheticLibrary(), ItemPresentation {

    override fun getPresentableText(): String = "npm sources"

    override fun getIcon(unused: Boolean): Icon? = null

    override fun getSourceRoots(): Collection<VirtualFile> = listOf(libraryRootDirectory)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as NpmSourcesSyntheticLibrary

        if (libraryRootDirectory != other.libraryRootDirectory) return false

        return true
    }

    override fun hashCode(): Int = libraryRootDirectory.hashCode()
}

