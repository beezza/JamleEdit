// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.debugger.breakpoints

import com.intellij.debugger.SourcePosition
import com.intellij.debugger.ui.breakpoints.JavaLineBreakpointType
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.impl.XSourcePositionImpl
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.base.psi.getLineNumber
import org.jetbrains.kotlin.idea.base.psi.getTopmostElementAtOffset
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.util.findElementsOfClassInRange
import org.jetbrains.kotlin.idea.util.application.runReadAction
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.inline.INLINE_ONLY_ANNOTATION_FQ_NAME
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
import java.util.*
import org.jetbrains.kotlin.idea.debugger.core.findElementAtLine

interface KotlinBreakpointType

class ApplicabilityResult(val isApplicable: Boolean, val shouldStop: Boolean) {
    companion object {
        @JvmStatic
        fun definitely(result: Boolean) = ApplicabilityResult(result, shouldStop = true)

        @JvmStatic
        fun maybe(result: Boolean) = ApplicabilityResult(result, shouldStop = false)

        @JvmField
        val UNKNOWN = ApplicabilityResult(isApplicable = false, shouldStop = false)

        @JvmField
        val DEFINITELY_YES = ApplicabilityResult(isApplicable = true, shouldStop = true)

        @JvmField
        val DEFINITELY_NO = ApplicabilityResult(isApplicable = false, shouldStop = true)

        @JvmField
        val MAYBE_YES = ApplicabilityResult(isApplicable = true, shouldStop = false)
    }
}

fun isBreakpointApplicable(file: VirtualFile, line: Int, project: Project, checker: (PsiElement) -> ApplicabilityResult): Boolean {
    val psiFile = PsiManager.getInstance(project).findFile(file)

    if (psiFile == null || psiFile.virtualFile?.fileType != KotlinFileType.INSTANCE) {
        return false
    }

    val document = FileDocumentManager.getInstance().getDocument(file) ?: return false

    return runReadAction {
        var isApplicable = false
        val checked = HashSet<PsiElement>()

        XDebuggerUtil.getInstance().iterateLine(
            project, document, line,
            fun(element: PsiElement): Boolean {
                if (element is PsiWhiteSpace || element.getParentOfType<PsiComment>(false) != null || !element.isValid) {
                    return true
                }

                val parent = getTopmostParentOnLineOrSelf(element, document, line)
                if (!checked.add(parent)) {
                    return true
                }

                val result = checker(parent)

                if (result.shouldStop && !result.isApplicable) {
                    isApplicable = false
                    return false
                }

                isApplicable = isApplicable or result.isApplicable
                return !result.shouldStop
            },
        )

        return@runReadAction isApplicable
    }
}

private fun getTopmostParentOnLineOrSelf(element: PsiElement, document: Document, line: Int): PsiElement {
    var current = element
    var parent = current.parent
    while (parent != null && parent !is PsiFile) {
        val offset = parent.textOffset
        if (offset > document.textLength) break
        if (offset >= 0 && document.getLineNumber(offset) != line) break

        current = parent
        parent = current.parent
    }

    return current
}

fun computeLineBreakpointVariants(
    project: Project,
    position: XSourcePosition,
    kotlinBreakpointType: KotlinLineBreakpointType,
): List<JavaLineBreakpointType.JavaBreakpointVariant> {
    val file = PsiManager.getInstance(project).findFile(position.file) as? KtFile ?: return emptyList()

    val pos = SourcePosition.createFromLine(file, position.line)
    val lambdas = getLambdasAtLineIfAny(pos)
    if (lambdas.isEmpty()) return emptyList()

    val result = LinkedList<JavaLineBreakpointType.JavaBreakpointVariant>()

    val elementAt = pos.elementAt.parentsWithSelf.firstIsInstance<KtElement>()
    val mainMethod = PsiTreeUtil.getParentOfType(elementAt, KtFunction::class.java, false)

    var mainMethodAdded = false

    if (mainMethod != null) {
        val bodyExpression = mainMethod.bodyExpression
        val isLambdaResult = bodyExpression is KtLambdaExpression && bodyExpression.functionLiteral in lambdas

        if (!isLambdaResult) {
            val variantElement = getTopmostElementAtOffset(elementAt, pos.offset)
            result.add(kotlinBreakpointType.LineKotlinBreakpointVariant(position, variantElement, -1))
            mainMethodAdded = true
        }
    }

    lambdas.forEachIndexed { ordinal, lambda ->
        val positionImpl = XSourcePositionImpl.createByElement(lambda.bodyExpression)

        if (positionImpl != null) {
            result.add(kotlinBreakpointType.LambdaJavaBreakpointVariant(positionImpl, lambda, ordinal))
        }
    }

    if (mainMethodAdded && result.size > 1) {
        result.add(kotlinBreakpointType.KotlinBreakpointVariant(position, lambdas.size))
    }

    return result
}

fun getLambdasAtLineIfAny(sourcePosition: SourcePosition): List<KtFunction> {
    val file = sourcePosition.file as? KtFile ?: return emptyList()
    return getLambdasAtLineIfAny(file, sourcePosition.line)
}

inline fun <reified T : PsiElement> getElementsAtLineIfAny(file: KtFile, line: Int): List<T> {
    val lineElement = findElementAtLine(file, line) as? KtElement ?: return emptyList()

    val start = lineElement.startOffset
    var end = lineElement.endOffset
    var nextSibling = lineElement.nextSibling
    while (nextSibling != null && line == nextSibling.getLineNumber()) {
        end = nextSibling.endOffset
        nextSibling = nextSibling.nextSibling
    }

    return findElementsOfClassInRange(file, start, end, T::class.java).filterIsInstance<T>()
}

fun getLambdasAtLineIfAny(file: KtFile, line: Int): List<KtFunction> {
    val allLiterals = getElementsAtLineIfAny<KtFunction>(file, line)
        // filter function literals and functional expressions
        .filter { it is KtFunctionLiteral || it.name == null }
        .toSet()

    return allLiterals.filter {
        val statement = it.bodyBlockExpression?.statements?.firstOrNull() ?: it
        statement.getLineNumber() == line && statement.getLineNumber(false) == line
    }
}

internal fun KtCallableDeclaration.isInlineOnly(): Boolean {
    if (!hasModifier(KtTokens.INLINE_KEYWORD)) {
        return false
    }

    val inlineOnlyAnnotation = annotationEntries
        .firstOrNull { it.shortName == INLINE_ONLY_ANNOTATION_FQ_NAME.shortName() }
        ?: return false

    return runReadAction f@{
        val bindingContext = inlineOnlyAnnotation.analyze(BodyResolveMode.PARTIAL)
        val annotationDescriptor = bindingContext[BindingContext.ANNOTATION, inlineOnlyAnnotation] ?: return@f false
        return@f annotationDescriptor.fqName == INLINE_ONLY_ANNOTATION_FQ_NAME
    }
}
