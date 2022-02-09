package plugin.ui

import plugin.actions.DownloadSourcesAction
import plugin.actions.SymlinkExistingSourcesAction
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import files.SourcesResultHandler

object PluginNotifications {

    fun notifyAboutAvailableSources(
        project: Project,
        source: FileEditorManager,
        initialFile: VirtualFile,
        sourceFiles: List<VirtualFile>
    ) {
        val notification = Notification(
            "Source Wizard Notification Group",
            "Sources available",
            "Source file available for ${initialFile.parent.name}/${initialFile.name}.",
            NotificationType.INFORMATION,
        )
        notification.addAction(NotificationAction.create("Open Sources") { _ ->
            SourcesResultHandler.processFoundSources(source, initialFile, sourceFiles)
        })
        Notifications.Bus.notify(notification, project)
    }

    fun notifyAboutSourcesAvailableToDownload(project: Project, fileName: String) {
        val notification = Notification(
            "Source Wizard Notification Group",
            "Sources available",
            "Download sources for $fileName file.",
            NotificationType.INFORMATION,
        )
        notification.addAction(DownloadSourcesAction()).addAction(SymlinkExistingSourcesAction())
        Notifications.Bus.notify(notification, project)
    }

    fun notifyAboutDownloadedRepository(project: Project) {
        val notification = Notification(
            "Source Wizard Notification Group",
            "Sources downloaded",
            "Poof! Repository with sources downloaded.",
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

    fun notifyAboutMissingRepository() = Notifications.Bus.notify(
        Notification(
            "Source Wizard Notification Group",
            "Sources wizard",
            "Sources failed to download. No repository url in package.json file",
            NotificationType.ERROR,
        )
    )
}