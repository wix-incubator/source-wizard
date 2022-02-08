package repository.impl

import com.intellij.openapi.project.Project
import repository.GitHubUtils
import repository.RepositoryService
import infra.SourceCacheDirectory.getPathToSourceCacheDirectory
import infra.GitClient


class GitRepositoryService(project: Project) : RepositoryService {

    private val gitClient: GitClient = project.getService(GitClient::class.java)

    private fun getRepositoryPath(repositoryUrl: String): String {
        val repositoryName = Regex(".*/(.*)").find(repositoryUrl.replace(".git", ""))!!.destructured.component1()
        return "${getPathToSourceCacheDirectory()}/$repositoryName"
    }

    override fun cloneRepository(repositoryUrl: String): String {
        val repositoryPath = getRepositoryPath(repositoryUrl)
        val sshUrl = GitHubUtils.convertToSshUrl(repositoryUrl)

        gitClient.clone(sshUrl, repositoryPath)
        return repositoryPath
    }

    override fun pullRepository(repositoryUrl: String) {
        val repositoryPath = getRepositoryPath(repositoryUrl)
        val sshUrl = GitHubUtils.convertToSshUrl(repositoryUrl)

        gitClient.pull(sshUrl, repositoryPath)
    }
}