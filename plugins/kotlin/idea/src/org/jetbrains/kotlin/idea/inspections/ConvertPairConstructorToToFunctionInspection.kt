// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.idea.base.resources.KotlinBundle
import org.jetbrains.kotlin.idea.intentions.getCallableDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.callExpressionVisitor
import org.jetbrains.kotlin.psi.createExpressionByPattern
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

import org.jetbrains.kotlin.idea.codeinsight.api.classic.inspections.AbstractKotlinInspection

class ConvertPairConstructorToToFunctionInspection : AbstractKotlinInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return callExpressionVisitor { expression ->
            if (expression.isPairConstructorCall()) {
                holder.registerProblem(
                    expression,
                    KotlinBundle.message("can.be.converted.to.to"),
                    ConvertPairConstructorToToFix()
                )
            }
        }
    }
}

private class ConvertPairConstructorToToFix : LocalQuickFix {
    override fun getName() = KotlinBundle.message("convert.pair.constructor.to.to.fix.text")

    override fun getFamilyName() = name

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val expression = descriptor.psiElement as? KtCallExpression ?: return
        val args = expression.valueArguments.mapNotNull { it.getArgumentExpression() }.toTypedArray()
        expression.replace(KtPsiFactory(expression).createExpressionByPattern("$0 to $1", *args))
    }
}

private fun KtCallExpression.isPairConstructorCall(): Boolean {
    if (valueArguments.size != 2) return false
    if (valueArguments.mapNotNull { it.getArgumentExpression() }.size != 2) return false
    return getCallableDescriptor()?.containingDeclaration?.fqNameSafe == FqName("kotlin.Pair")
}
