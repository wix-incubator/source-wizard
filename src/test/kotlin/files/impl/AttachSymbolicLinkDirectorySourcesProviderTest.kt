package files.impl

import com.intellij.util.io.createSymbolicLink
import io.mockk.*
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths


internal class AttachSymbolicLinkDirectorySourcesProviderTest {

    private val attachSymbolicLinkDirectorySourcesProvider = AttachSymbolicLinkDirectorySourcesProvider()
    private val targetPath = Paths.get("/find-file-test-data/test-folder-1")


    @Test
    fun `createLink should create a link to a target folder in provided directory`() {
        val rootSourceDirectoryPath = mockk<Path>()
        mockkStatic(Path::createSymbolicLink)
        every { rootSourceDirectoryPath.resolve(any<Path>()) } returns rootSourceDirectoryPath
        every { rootSourceDirectoryPath.createSymbolicLink(any()) } returns rootSourceDirectoryPath

        attachSymbolicLinkDirectorySourcesProvider.attachSourceDirectory(targetPath, rootSourceDirectoryPath)

        verify { rootSourceDirectoryPath.resolve(targetPath.fileName) }
        verify { rootSourceDirectoryPath.createSymbolicLink(targetPath) }
    }

}