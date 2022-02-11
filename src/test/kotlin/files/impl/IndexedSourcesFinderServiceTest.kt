package files.impl

import MockProject.mockProject
import MockProject.withService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.openapi.vfs.newvfs.impl.FakeVirtualFile
import files.FileFinderService
import infra.MissingRepositoryUrlException
import infra.PluginRepositoryManager
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
    private val pluginRepositoryManager: PluginRepositoryManager
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
        pluginRepositoryManager = mockk()
        fileFinderService = mockk()
        npmModuleFileExaminer = mockk()
        project = mockProject()
            .withService(PluginRepositoryManager::class.java, pluginRepositoryManager)
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
        every { pluginRepositoryManager.pullRepositoryAsync(any()) } returns Unit

        val result = indexedSourcesFinderService.find(project, file)

        assertEquals(fileSource, result.first())
        verify { npmModuleFileExaminer.extractNpmModuleDirectory(file) }
        verify { npmModuleFileExaminer.extractRepositoryUrl(file) }
        verify { fileFinderService.findFile(project, "module", "file.ts", "directory") }
        verify { pluginRepositoryManager.pullRepositoryAsync(repositoryUrl) }
    }

    @Test
    fun `find should not pull from remote repository when sources are not found`() {
        every { npmModuleFileExaminer.extractNpmModuleDirectory(any()) } returns npmModuleDirectory
        every { fileFinderService.findFile(any(), any(), any(), any()) } returns emptyList()

        val result = indexedSourcesFinderService.find(project, file)

        assertEquals(0, result.size)
        verify(exactly = 0) { pluginRepositoryManager.pullRepositoryAsync(any()) }
    }

    @Test
    fun `downloadRepositoryAndFind should download repository and find sources`() {
        val repositoryUrl = "repository-url"
        every { npmModuleFileExaminer.extractNpmModuleDirectory(any()) } returns npmModuleDirectory
        every { npmModuleFileExaminer.extractRepositoryUrl(any()) } returns repositoryUrl
        every { fileFinderService.findFile(any(), any(), any(), any()) } returns listOf(fileSource)
        every { pluginRepositoryManager.cloneRepositorySync(any()) } returns Unit

        val result = indexedSourcesFinderService.downloadRepositoryAndFind(project, file)

        assertEquals(fileSource, result.first())
        verify { npmModuleFileExaminer.extractNpmModuleDirectory(file) }
        verify { npmModuleFileExaminer.extractRepositoryUrl(file) }
        verify { fileFinderService.findFile(project, "module", "file.ts", "directory") }
        verify { pluginRepositoryManager.cloneRepositorySync(repositoryUrl) }
    }

    @Test
    fun `downloadRepositoryAndFind should fail with exception if repository url not found`() {
        every { npmModuleFileExaminer.extractRepositoryUrl(any()) } returns null

        assertThrows(MissingRepositoryUrlException::class.java) {
            indexedSourcesFinderService.downloadRepositoryAndFind(project, file)
        }
    }
}