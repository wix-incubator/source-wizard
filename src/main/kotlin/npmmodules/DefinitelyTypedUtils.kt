package npmmodules

import infra.NpmRegistry

class DefinitelyTypedUtils(
    private val npmRegistry: NpmRegistry = NpmRegistry(),
) {
    private val definitelyTypedRepositoryUrl = "https://github.com/DefinitelyTyped/DefinitelyTyped.git"


    fun isDefinitelyTypedModule(repositoryUrl: String?) = repositoryUrl == definitelyTypedRepositoryUrl

    fun findActualRepositoryUrl(npmModuleName: String): String? {
        return npmRegistry.getRepositoryUrl(npmModuleName)
    }
}