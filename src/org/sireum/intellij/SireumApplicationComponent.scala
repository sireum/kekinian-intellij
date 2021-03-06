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

import java.awt.Color

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.{FileEditorManager, FileEditorManagerListener, TextEditor}
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.util.{IconLoader, Key}
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

object EditorOpened

object SireumApplicationComponent {
  type FileResourceUri = String
  val gutterErrorIcon: Icon = IconLoader.getIcon("/gutter-error.png")
  val gutterWarningIcon: Icon = IconLoader.getIcon("/gutter-warning.png")
  val gutterInfoIcon: Icon = IconLoader.getIcon("/gutter-info.png")
  val verifiedInfoIcon: Icon = IconLoader.getIcon("/logika-verified-info.png")
  var tooltipMessageOpt: scala.Option[String] = scala.None
  var tooltipBalloonOpt: scala.Option[Balloon] = scala.None
  val tooltipDefaultBgColor: Color = new Color(0xFF, 0xFF, 0xCC)
  val tooltipDarculaBgColor: Color = new Color(0x5C, 0x5C, 0x42)
  val editorOpenedKey: Key[EditorOpened.type] = new Key[EditorOpened.type]("Editor Opened")

  def gutterIconRenderer(tooltipText: String, icon: Icon, action: AnAction): GutterIconRenderer =
    new GutterIconRenderer {
      override val getTooltipText: String = tooltipText

      override def getIcon: Icon = icon

      override def equals(other: Any): Boolean = false

      override def hashCode: Int = System.identityHashCode(this)

      override def getClickAction: AnAction = action
    }

  def getEditorOpt(source: FileEditorManager, file: VirtualFile): Option[Editor] = {
    source.getSelectedEditor(file) match {
      case te: TextEditor => Some(te.getEditor)
      case _ => None
    }
  }
}

import SireumApplicationComponent._

final class SireumApplicationComponent extends Disposable {

  {
    ApplicationManager.getApplication.invokeLater(() => org.sireum.lang.FrontEnd.libraryReporter)
    val mb = ApplicationManager.getApplication.getMessageBus.connect(this)
    mb.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER,
      new FileEditorManagerListener {
        override def fileOpened(source: FileEditorManager,
                                file: VirtualFile): Unit = {
          getEditorOpt(source, file).foreach(Slang.editorOpened(source.getProject, file, _))
        }
      })
    mb.subscribe(FileEditorManagerListener.Before.FILE_EDITOR_MANAGER,
      new FileEditorManagerListener.Before {
        override def beforeFileClosed(source: FileEditorManager, file: VirtualFile): Unit = {
          getEditorOpt(source, file).foreach(Slang.editorClosed(source.getProject, file, _))
        }
      })
  }

  override def dispose(): Unit = {}
}
