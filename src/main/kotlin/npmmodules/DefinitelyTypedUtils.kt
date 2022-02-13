package npmmodules

import infra.NpmRegistry

/**
 * Utility tools used to identify if dependencies are just type definitions shared
 * in common DefinitelyTyped repository and actual source repository cannot be find
 * in package.json file.
 */
class DefinitelyTypedUtils(
    private val npmRegistry: NpmRegistry = NpmRegistry(),
) {
    private val definitelyTypedRepositoryUrl = "https://github.com/DefinitelyTyped/DefinitelyTyped.git"

    fun isDefinitelyTypedModule(repositoryUrl: String?) = repositoryUrl == definitelyTypedRepositoryUrl

    fun findActualRepositoryUrl(npmModuleName: String): String {
        return npmRegistry.getRepositoryUrl(npmModuleName)
    }
}