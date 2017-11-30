// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.java.decompiler.struct.attr;

import org.jetbrains.java.decompiler.struct.consts.ConstantPool;
import org.jetbrains.java.decompiler.util.DataInputFullStream;

import java.io.IOException;

public class StructSourceFileAttribute extends StructGeneralAttribute {

  private String fileName;

  @Override
  public void initContent(DataInputFullStream data, ConstantPool pool) throws IOException {
    int index = data.readUnsignedShort();
    fileName = pool.getPrimitiveConstant(index).getString();
  }

  public String getFileName() {
    return fileName;
  }
}
