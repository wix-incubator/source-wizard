package npmmodules

import com.intellij.openapi.vfs.VirtualFile
import org.codehaus.jettison.json.JSONObject
import infra.MissingNpmModuleException


class NpmModuleFileExaminer {

    fun isFromNpmModule(file: VirtualFile): Boolean = file.path.contains("node_modules")

    fun extractRepositoryUrl(file: VirtualFile): String? {
        val npmModule = extractNpmModuleDirectory(file)
        val json =
            JSONObject(
                npmModule.children.find { it.name == "package.json" }?.contentsToByteArray()?.decodeToString()
                    ?: "{}"
            )
        val repository = json.optJSONObject("repository")
        val repositoryUrl = if (repository?.has("url") == true) repository.getString("url") else null

        val definitelyTypedUtils = DefinitelyTypedUtils()
        return if (definitelyTypedUtils.isDefinitelyTypedModule(repositoryUrl)) {
            val npmModuleName = extractNpmModuleDirectory(file).name
            definitelyTypedUtils.findActualRepositoryUrl(npmModuleName)
        } else repositoryUrl
    }

    fun extractNpmModuleDirectory(file: VirtualFile): VirtualFile {
        return when {
            file.children.any { it.name == "package.json" } -> file
            file.name == "node_modules" -> throw MissingNpmModuleException()
            else -> extractNpmModuleDirectory(file.parent)
        }
    }

    fun extractNpmModuleName(file: VirtualFile): String {
        return when {
            file.children.any { it.name == "package.json" } -> file.name
            file.name == "node_modules" -> ""
            else -> extractNpmModuleName(file.parent)
        }
    }
}