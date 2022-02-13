package sourcecache

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import plugin.PluginUtils.executeBackgroundTask
import repository.RepositoryService

class SourceCacheRefresher(val project: Project) {

    private val repositoryService: RepositoryService = project.service()

    fun refreshSourceCache(repositoryUrl: String) =
        executeBackgroundTask(project, "Refreshing source cache") {
            repositoryService.pullRepository(
                repositoryUrl
            )
        }

}