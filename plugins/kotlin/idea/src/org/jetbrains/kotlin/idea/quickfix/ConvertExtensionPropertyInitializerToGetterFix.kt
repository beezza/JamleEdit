// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.base.resources.KotlinBundle
import org.jetbrains.kotlin.idea.codeinsight.api.classic.quickfixes.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.intentions.ConvertPropertyInitializerToGetterIntention
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

class ConvertExtensionPropertyInitializerToGetterFix(element: KtExpression) : KotlinQuickFixAction<KtExpression>(element) {
    override fun getText(): String = KotlinBundle.message("convert.extension.property.initializer.to.getter")

    override fun getFamilyName(): String = text

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        val property = element.getParentOfType<KtProperty>(true) ?: return
        ConvertPropertyInitializerToGetterIntention.convertPropertyInitializerToGetter(property, editor)
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): IntentionAction? {
            val expression = diagnostic.psiElement as? KtExpression ?: return null
            val property = expression.getParentOfType<KtProperty>(true)!!

            if (property.getter != null) {
                return null
            }

            return ConvertExtensionPropertyInitializerToGetterFix(expression)
        }
    }
}