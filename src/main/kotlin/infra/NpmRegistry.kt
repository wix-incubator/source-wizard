package infra

import org.codehaus.jettison.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class NpmRegistry {

    fun getRepositoryUrl(npmModuleName: String): String? {
        return try {
            val json = fetch("https://api.npms.io/v2/package/${npmModuleName}")
            json.getJSONObject("collected")
                .getJSONObject("metadata")
                .getJSONObject("repository")
                .getString("url")
                .replace("git+https", "https")
        } catch (ex: Exception) {
            null
        }
    }

    private fun fetch(url: String): JSONObject {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return JSONObject(response.body())
    }


}