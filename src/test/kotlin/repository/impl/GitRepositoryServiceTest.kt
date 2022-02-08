package repository.impl

import MockProject.mockProjectWithService
import io.mockk.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import repository.GitHubUtils
import repository.GitHubUtils.convertToSshUrl
import infra.SourceCacheDirectory.getPathToSourceCacheDirectory
import infra.GitClient

internal class GitRepositoryServiceTest {

    private val gitClient = mockk<GitClient>()
    private val project = mockProjectWithService(GitClient::class.java, gitClient)
    private val gitRepositoryService = GitRepositoryService(project)
    private val repositoryUrl = "git@github.com:expressjs/express.git"
    private val repositoryPath = "${getPathToSourceCacheDirectory()}/express"


    @Test
    fun `cloneRepository should clone repository and return its path`() {
        every { gitClient.clone(any(), any()) } returns Unit
        mockkObject(GitHubUtils)
        every { convertToSshUrl(any()) } returns repositoryUrl

        val actualRepository = gitRepositoryService.cloneRepository(repositoryUrl)

        assertEquals(repositoryPath, actualRepository)
        verify { gitClient.clone(repositoryUrl, repositoryPath) }
        verify { convertToSshUrl(repositoryUrl) }
    }

    @Test
    fun `pullRepository should pull repository`() {
        every { gitClient.pull(any(), any()) } returns Unit
        mockkObject(GitHubUtils)
        every { convertToSshUrl(any()) } returns repositoryUrl

        gitRepositoryService.pullRepository(repositoryUrl)

        verify { gitClient.pull(repositoryUrl, repositoryPath) }
        verify { convertToSshUrl(repositoryUrl) }
    }
}