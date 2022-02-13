package files

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

interface SourcesFinderService {
    fun find(project: Project, file: VirtualFile): List<VirtualFile>
}