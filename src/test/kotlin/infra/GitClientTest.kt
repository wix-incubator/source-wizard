package infra

import MockProject.mockProjectWithService
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

internal class GitClientTest {

    private val repositoryUrl = "git-url"
    private val repositoryPath = "/path"

    private val process = mockk<Process>()
    private val processBuilder = spyk(ProcessBuilder())
    private val processBuilderProvider = mockk<ProcessBuilderProvider>()
    private val project = mockProjectWithService(ProcessBuilderProvider::class.java, processBuilderProvider)
    private val gitClient = GitClient(project)


    @Test
    fun `clone should clone given git repository to provided path`() {
        every { processBuilderProvider.getProcessBuilder() } returns processBuilder
        every { processBuilder.start() } returns process
        every { process.waitFor() } returns 0

        gitClient.clone(repositoryUrl, repositoryPath)

        verify { processBuilder.command("git", "clone", repositoryUrl, repositoryPath) }
        verify { process.waitFor() }
    }

    @Test
    fun `clone should throw exception when process returns anything than 0`() {
        every { processBuilderProvider.getProcessBuilder() } returns processBuilder
        every { processBuilder.start() } returns process
        every { process.waitFor() } returns 1

        assertThrows<GitOperationException> { gitClient.clone(repositoryUrl, repositoryPath) }
    }

    @Test
    fun `pull should pull given git repository to provided path`() {
        every { processBuilderProvider.getProcessBuilder() } returns processBuilder
        every { processBuilder.start() } returns process
        every { process.waitFor() } returns 0

        gitClient.pull(repositoryUrl, repositoryPath)

        verify { processBuilder.command("git", "pull", repositoryUrl) }
        verify { processBuilder.directory(File(repositoryPath)) }
        verify { process.waitFor() }
    }

}