/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.lang.resolve.android.test;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class AndroidSyntheticPropertyDescriptorTestGenerated extends AbstractAndroidSyntheticPropertyDescriptorTest {
    public void testAllFilesPresentInDescriptors() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("plugins/android-extensions/android-extensions-compiler/testData/descriptors"), Pattern.compile("^([^\\.]+)$"), false);
    }

    @TestMetadata("escapedLayoutName")
    public void testEscapedLayoutName() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/escapedLayoutName/");
        doTest(fileName);
    }

    @TestMetadata("fqNameInAttr")
    public void testFqNameInAttr() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/fqNameInAttr/");
        doTest(fileName);
    }

    @TestMetadata("fqNameInTag")
    public void testFqNameInTag() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/fqNameInTag/");
        doTest(fileName);
    }

    @TestMetadata("layoutVariants")
    public void testLayoutVariants() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/layoutVariants/");
        doTest(fileName);
    }

    @TestMetadata("multiFile")
    public void testMultiFile() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/multiFile/");
        doTest(fileName);
    }

    @TestMetadata("noIds")
    public void testNoIds() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/noIds/");
        doTest(fileName);
    }

    @TestMetadata("sameIds")
    public void testSameIds() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/sameIds/");
        doTest(fileName);
    }

    @TestMetadata("severalResDirs")
    public void testSeveralResDirs() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/severalResDirs/");
        doTest(fileName);
    }

    @TestMetadata("singleFile")
    public void testSingleFile() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/singleFile/");
        doTest(fileName);
    }

    @TestMetadata("specialTags")
    public void testSpecialTags() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/specialTags/");
        doTest(fileName);
    }

    @TestMetadata("supportSingleFile")
    public void testSupportSingleFile() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/supportSingleFile/");
        doTest(fileName);
    }

    @TestMetadata("supportSpecialTags")
    public void testSupportSpecialTags() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/supportSpecialTags/");
        doTest(fileName);
    }

    @TestMetadata("unresolvedFqName")
    public void testUnresolvedFqName() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/unresolvedFqName/");
        doTest(fileName);
    }

    @TestMetadata("unresolvedWidget")
    public void testUnresolvedWidget() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/unresolvedWidget/");
        doTest(fileName);
    }

    @TestMetadata("viewStub")
    public void testViewStub() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("plugins/android-extensions/android-extensions-compiler/testData/descriptors/viewStub/");
        doTest(fileName);
    }
}
