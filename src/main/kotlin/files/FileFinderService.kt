package files

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

interface FileFinderService {

    fun findFile(project: Project, module: String, filename: String, fileDirectory: String): List<VirtualFile>
}