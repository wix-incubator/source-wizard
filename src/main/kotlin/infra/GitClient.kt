package infra

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.io.File

class GitClient(project: Project) {

    private val processBuilderProvider = project.service<ProcessBuilderProvider>()

    fun clone(repositoryUrl: String, repositoryPath: String) {
        val cloneResult = processBuilderProvider
            .getProcessBuilder()
            .command("git", "clone", repositoryUrl, repositoryPath)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start().waitFor()

        if (cloneResult != 0) {
            throw GitOperationException("Failed to clone repository $repositoryUrl")
        }
    }

    fun pull(repositoryUrl: String, repositoryPath: String) {
        val pullResult = processBuilderProvider
            .getProcessBuilder()
            .command("git", "pull", repositoryUrl)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .directory(File(repositoryPath))
            .start().waitFor()

        if (pullResult != 0) {
            throw GitOperationException("Failed to pull repository $repositoryUrl")
        }
    }
}