package com.ap

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class SendFileAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)

        if (project == null || editor == null) {
            return
        }

        val document = editor.document
        val fileContent = document.text

        // Send HTTP request in background thread
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val client = HttpClient.newHttpClient()
                val request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9999"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(fileContent))
                    .build()

                val response = client.send(request, HttpResponse.BodyHandlers.ofString())

                // Show result on EDT
                ApplicationManager.getApplication().invokeLater {
                    if (response.statusCode() == 200) {
                        //Messages.showInfoMessage(project, "File content sent successfully!", "Success")
                    } else {
                        //Messages.showErrorDialog(project, "Server responded with status: ${response.statusCode()}", "Error")
                    }
                }

            } catch (e: IOException) {
                ApplicationManager.getApplication().invokeLater {
                    Messages.showErrorDialog(project, "Failed to send request: ${e.message}", "Connection Error")
                }
            } catch (e: Exception) {
                ApplicationManager.getApplication().invokeLater {
                    Messages.showErrorDialog(project, "Unexpected error: ${e.message}", "Error")
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null
    }
}