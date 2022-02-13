package files.impl

import MockProject.mockProject
import MockProject.withService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.openapi.vfs.newvfs.impl.FakeVirtualFile
import files.FileFinderService
import sourcecache.SourceCacheRefresher
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import npmmodules.NpmModuleFileExaminer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class IndexedSourcesFinderServiceTest {

    private val mockFile: VirtualFile
    private val npmModuleDirectory: VirtualFile
    private val fileDirectory: VirtualFile
    private val file: VirtualFile
    private val fileSource: VirtualFile
    private val sourceCacheRefresher: SourceCacheRefresher
    private val fileFinderService: FileFinderService
    private val npmModuleFileExaminer: NpmModuleFileExaminer
    private val project: Project
    private val indexedSourcesFinderService: IndexedSourcesFinderService


    init {
        val fileSystem = mockk<VirtualFileSystem>()
        mockFile = mockk()
        every { mockFile.fileSystem } returns fileSystem
        npmModuleDirectory = FakeVirtualFile(mockFile, "module")
        fileDirectory = FakeVirtualFile(mockFile, "directory")
        file = FakeVirtualFile(fileDirectory, "file.ts")
        fileSource = FakeVirtualFile(fileDirectory, "file-source.ts")
        sourceCacheRefresher = mockk()
        fileFinderService = mockk()
        npmModuleFileExaminer = mockk()
        project = mockProject()
            .withService(SourceCacheRefresher::class.java, sourceCacheRefresher)
            .withService(FileFinderService::class.java, fileFinderService)
            .withService(NpmModuleFileExaminer::class.java, npmModuleFileExaminer)

        indexedSourcesFinderService = IndexedSourcesFinderService(project)
    }

    @Test
    fun `find should return source file and update source cache by pulling from remote repository`() {
        val repositoryUrl = "repository-url"
        every { npmModuleFileExaminer.extractNpmModuleDirectory(any()) } returns npmModuleDirectory
        every { npmModuleFileExaminer.extractRepositoryUrl(any()) } returns repositoryUrl
        every { fileFinderService.findFile(any(), any(), any(), any()) } returns listOf(fileSource)
        every { sourceCacheRefresher.refreshSourceCache(any()) } returns Unit

        val result = indexedSourcesFinderService.find(project, file)

        assertEquals(fileSource, result.first())
        verify { npmModuleFileExaminer.extractNpmModuleDirectory(file) }
        verify { npmModuleFileExaminer.extractRepositoryUrl(file) }
        verify { fileFinderService.findFile(project, "module", "file.ts", "directory") }
        verify { sourceCacheRefresher.refreshSourceCache(repositoryUrl) }
    }

    @Test
    fun `find should not pull from remote repository when sources are not found`() {
        every { npmModuleFileExaminer.extractNpmModuleDirectory(any()) } returns npmModuleDirectory
        every { fileFinderService.findFile(any(), any(), any(), any()) } returns emptyList()

        val result = indexedSourcesFinderService.find(project, file)

        assertEquals(0, result.size)
        verify(exactly = 0) { sourceCacheRefresher.refreshSourceCache(any()) }
    }


}