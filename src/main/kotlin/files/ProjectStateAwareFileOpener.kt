package files

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import plugin.ui.PluginNotifications
import java.nio.file.Paths

/**
 * This class is aware of project state, when files are refreshed and fully indexed.
 * It knows when search can be performed for newly added files, so they would be
 * present in Filename index.
 */
class ProjectStateAwareFileOpener(project: Project) {

    private val sourcesFinderService = project.service<SourcesFinderService>()

    /**
     * Searches for the sources of opened file when project is ready - cache repository is refreshed and
     * (in some cases) source files are indexed.
     *
     * This function searches for a file twice - first when source cache directory is refreshed,
     * in most cases that is enough, and you don't need to wait till indexing is finished to find the needed file.
     * If after the first search source file is not found, second search is scheduled to be performed when
     * sources are fully indexed.
     */
    fun findAndOpenSourceFile(project: Project, source: FileEditorManager, openedFile: VirtualFile) =
        executeAfterRefresh {
            val sourceFiles = sourcesFinderService.find(project, openedFile)
            if (sourceFiles.isEmpty()) {
                PluginNotifications.notifyAboutAddedSources(project)
                DumbService.getInstance(project).runWhenSmart {
                    SourcesResultHandler.processFoundSources(
                        source,
                        openedFile,
                        sourcesFinderService.find(project, openedFile),
                        true
                    )
                }
            } else {
                SourcesResultHandler.processFoundSources(source, openedFile, sourceFiles, true)
            }
        }

    private fun executeAfterRefresh(runnable: Runnable) {
        ReadAction.run<Throwable> {
            val homePath = Paths.get(FileUtil.expandUserHome("~/"))
            VfsUtil.findFile(homePath, false)?.refresh(true, true, runnable)
        }
    }
}