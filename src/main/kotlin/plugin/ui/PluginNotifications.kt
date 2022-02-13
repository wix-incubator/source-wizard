package plugin.ui

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import files.SourcesResultHandler
import npmmodules.NpmModuleFileExaminer
import plugin.actions.DownloadSourcesAction
import plugin.actions.SymlinkExistingSourcesAction

object PluginNotifications {

    private val npmModuleFileExaminer: NpmModuleFileExaminer by lazy { NpmModuleFileExaminer() }

    fun notifyAboutAvailableSources(
        project: Project,
        source: FileEditorManager,
        initialFile: VirtualFile,
        sourceFiles: List<VirtualFile>
    ) {
        val notification = Notification(
            "Source Wizard Notification Group",
            "Sources available",
            "Source file available for ${npmModuleFileExaminer.extractNpmModuleName(initialFile)}/" +
                    "${initialFile.parent.name}/${initialFile.name}.",
            NotificationType.INFORMATION,
        )
        notification.addAction(NotificationAction.create("Open Sources") { _ ->
            SourcesResultHandler.processFoundSources(source, initialFile, sourceFiles)
        })
        Notifications.Bus.notify(notification, project)
    }

    fun notifyAboutSourcesAvailableToDownload(project: Project, initialFile: VirtualFile) {
        val notification = Notification(
            "Source Wizard Notification Group",
            "Sources available",
            "Download sources for ${npmModuleFileExaminer.extractNpmModuleName(initialFile)}/${initialFile.name} file.",
            NotificationType.INFORMATION,
        )
        notification.addAction(DownloadSourcesAction()).addAction(SymlinkExistingSourcesAction())
        Notifications.Bus.notify(notification, project)
    }

    fun notifyAboutAddedSources(project: Project) {
        val notification = Notification(
            "Source Wizard Notification Group",
            "Source repository added successfully",
            "Your file will be opened after indexing is finished",
            NotificationType.INFORMATION,
        )
        Notifications.Bus.notify(notification, project)
    }

    fun notifyAboutFailure() = Notifications.Bus.notify(
        Notification(
            "Source Wizard Notification Group",
            "Sources wizard",
            "Failed to find source file.",
            NotificationType.ERROR,
        )
    )

    fun notifyAboutMissingRepositoryUrl() = Notifications.Bus.notify(
        Notification(
            "Source Wizard Notification Group",
            "Sources wizard",
            "Sources failed to download. No repository url in package.json file",
            NotificationType.ERROR,
        )
    )

    fun notifyAboutMissingRepository() = Notifications.Bus.notify(
        Notification(
            "Source Wizard Notification Group",
            "Sources wizard",
            "Sources failed to download. Could not find source files repository",
            NotificationType.ERROR,
        )
    )
}