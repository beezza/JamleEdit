// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.slicer

import com.intellij.psi.PsiElement
import com.intellij.slicer.SliceUsage
import com.intellij.usages.UsagePresentation
import com.intellij.util.Processor
import org.jetbrains.kotlin.idea.base.resources.KotlinBundle

class KotlinSliceDereferenceUsage(
    element: PsiElement,
    parent: KotlinSliceUsage,
    mode: KotlinSliceAnalysisMode
) : KotlinSliceUsage(element, parent, mode, false) {
    override fun processChildren(processor: Processor<in SliceUsage>) {
        // no children
    }

    override fun getPresentation() = object : UsagePresentation by super.getPresentation() {
        override fun getTooltipText() = KotlinBundle.message("slicer.tool.tip.text.variable.dereferenced")
    }
}
