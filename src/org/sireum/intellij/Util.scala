/*
 Copyright (c) 2019, Robby, Kansas State University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum.intellij

import com.intellij.notification.{Notification, Notifications}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.{FileDocumentManager, FileEditorManager}
import com.intellij.openapi.project.Project

import scala.Option

object Util {
  def getFilePath(project: Project): Option[String] = {
    val fem = FileEditorManager.getInstance(project)
    val editor = FileEditorManager.
      getInstance(project).getSelectedTextEditor
    if (editor == null) return scala.None
    val fdm = FileDocumentManager.getInstance
    val file = fdm.getFile(editor.getDocument)
    if (file == null) return scala.None
    Some(file.getCanonicalPath)
  }

  def getFileExt(project: Project): String = {
    getFilePath(project) match {
      case Some(path) =>
        val i = path.lastIndexOf('.')
        if (i >= 0)
          path.substring(path.lastIndexOf('.') + 1)
        else ""
      case _ => ""
    }
  }

  def notify(n: Notification, project: Project, shouldExpire: Boolean): Unit =
    if (shouldExpire)
      new Thread() {
        override def run(): Unit = {
          Notifications.Bus.notify(n, project)

          Thread.sleep(5000)
          ApplicationManager.getApplication.invokeLater(() => n.expire())
        }
      }.start()
    else
      Notifications.Bus.notify(n, project)
}
