// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.io.FileUtil
import com.intellij.refactoring.rename.RenameProcessor
import org.jetbrains.kotlin.idea.base.resources.KotlinBundle
import org.jetbrains.kotlin.idea.codeinsight.api.classic.intentions.SelfTargetingRangeIntention
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.renderer.render

class RenameClassToContainingFileNameIntention : SelfTargetingRangeIntention<KtClassOrObject>(
    KtClassOrObject::class.java, KotlinBundle.lazyMessage("rename.class.to.containing.file.name")
) {
    override fun startInWriteAction() = false

    override fun applicabilityRange(element: KtClassOrObject): TextRange? {
        if (!element.isTopLevel()) return null
        val fileName = FileUtil.getNameWithoutExtension(element.containingKtFile.name)
        if (fileName == element.name
            || fileName.isEmpty()
            || fileName[0].isLowerCase()
            || !Name.isValidIdentifier(fileName)
            || Name.identifier(fileName).render() != fileName
            || element.containingKtFile.declarations.any { it is KtClassOrObject && it.name == fileName }
        ) return null
        setTextGetter(KotlinBundle.lazyMessage("rename.class.to.0", fileName))
        return element.nameIdentifier?.textRange
    }

    override fun applyTo(element: KtClassOrObject, editor: Editor?) {
        val file = element.containingKtFile
        val fileName = FileUtil.getNameWithoutExtension(element.containingKtFile.name)
        RenameProcessor(file.project, element, fileName, false, false).run()
    }
}