/*
 * Copyright 2000-2016 JetBrains s.r.o.
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
package com.intellij.openapi.keymap;

import com.intellij.util.ui.UIUtil;
import org.intellij.lang.annotations.JdkConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Utility class to display action shortcuts in Mac menus
 *
 * @author Konstantin Bulenkov
 */
@SuppressWarnings("UnusedDeclaration")
public class MacKeymapUtil {
  public static final String ESCAPE	 = "\u238B";
  public static final String TAB	 = "\u21E5";
  public static final String TAB_BACK	 = "\u21E4";
  public static final String CAPS_LOCK	 = "\u21EA";
  public static final String SHIFT	 = "\u21E7";
  public static final String CONTROL	 = "\u2303";
  public static final String OPTION      = "\u2325";
  public static final String APPLE       = "\uF8FF";
  public static final String COMMAND     = "\u2318";
  public static final String SPACE	 = "\u2423";
  public static final String RETURN	 = "\u23CE";
  public static final String BACKSPACE	 = "\u232B";
  public static final String DELETE	 = "\u2326";
  public static final String HOME        = "\u2196";
  public static final String END	 = "\u2198";
  public static final String PAGE_UP	 = "\u21DE";
  public static final String PAGE_DOWN	 = "\u21DF";
  public static final String UP	         = "\u2191";
  public static final String DOWN	 = "\u2193";
  public static final String LEFT	 = "\u2190";
  public static final String RIGHT	 = "\u2192";
  public static final String CLEAR	 = "\u2327";
  public static final String NUMBER_LOCK = "\u21ED";
  public static final String ENTER	 = "\u2324";
  public static final String EJECT	 = "\u23CF";
  public static final String POWER3	 = "\u233D";
  public static final String NUM_PAD     = "\u2328";

  public static String getModifiersText(@JdkConstants.InputEventMask int modifiers) {
    StringBuilder buf = new StringBuilder();
    if ((modifiers & InputEvent.CTRL_MASK) != 0) buf.append(get(CONTROL, "Ctrl+"));
    if ((modifiers & InputEvent.ALT_MASK) != 0) buf.append(get(OPTION, "Alt+"));
    if ((modifiers & InputEvent.SHIFT_MASK) != 0) buf.append(get(SHIFT, "Shift+"));
    if ((modifiers & InputEvent.ALT_GRAPH_MASK) != 0) buf.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
    if ((modifiers & InputEvent.BUTTON1_MASK) != 0) buf.append(Toolkit.getProperty("AWT.button1", "Button1"));
    if ((modifiers & InputEvent.META_MASK) != 0) buf.append(get(COMMAND, "Cmd+"));
    return buf.toString();

  }

  public static String getKeyText(int code) {
    switch (code) {
      case KeyEvent.VK_BACK_SPACE:     return get(BACKSPACE, "Backspace");
      case KeyEvent.VK_ESCAPE:         return get(ESCAPE, "Escape");
      case KeyEvent.VK_CAPS_LOCK:      return get(CAPS_LOCK, "Caps Lock");
      case KeyEvent.VK_TAB:            return get(TAB, "Tab");
      case KeyEvent.VK_SPACE:          return "Space";
      case KeyEvent.VK_DELETE:         return get(DELETE, "Delete");
      case KeyEvent.VK_HOME:           return get(HOME, "Home");
      case KeyEvent.VK_END:            return get(END, "End");
      case KeyEvent.VK_PAGE_UP:        return get(PAGE_UP, "Page Up");
      case KeyEvent.VK_PAGE_DOWN:      return get(PAGE_DOWN, "Page Down");
      case KeyEvent.VK_UP:             return get(UP, "Up Arrow");
      case KeyEvent.VK_DOWN:           return get(DOWN, "Down Arrow");
      case KeyEvent.VK_LEFT:           return get(LEFT, "Left Arrow");
      case KeyEvent.VK_RIGHT:          return get(RIGHT, "Right Arrow");
      case KeyEvent.VK_NUM_LOCK:       return get(NUMBER_LOCK, "Num Lock");
      case KeyEvent.VK_ENTER:          return get(RETURN, "Return");
      case KeyEvent.VK_NUMBER_SIGN:    return get(NUM_PAD, "NumPad");
      case KeyEvent.VK_MULTIPLY:       return get(NUM_PAD, "NumPad") + " *";
      case KeyEvent.VK_SUBTRACT:       return "-";
      case KeyEvent.VK_ADD:            return "+";
      case KeyEvent.VK_MINUS:          return "-";
      case KeyEvent.VK_PLUS:           return "+";
      case KeyEvent.VK_DIVIDE:         return "NumPad /";
      case KeyEvent.VK_NUMPAD0:        return "0";
      case KeyEvent.VK_NUMPAD1:        return "1";
      case KeyEvent.VK_NUMPAD2:        return "2";
      case KeyEvent.VK_NUMPAD3:        return "3";
      case KeyEvent.VK_NUMPAD4:        return "4";
      case KeyEvent.VK_NUMPAD5:        return "5";
      case KeyEvent.VK_NUMPAD6:        return "6";
      case KeyEvent.VK_NUMPAD7:        return "7";
      case KeyEvent.VK_NUMPAD8:        return "8";
      case KeyEvent.VK_NUMPAD9:        return "9";
      case 0:                          return "fn";
    }
    return KeyEvent.getKeyText(code);
  }

  public static String getKeyStrokeText(KeyStroke keyStroke) {
    final String modifiers = getModifiersText(keyStroke.getModifiers());
    final String key = KeymapUtil.getKeyText(keyStroke.getKeyCode());
    return modifiers + key;
  }

  private static String get(String value, String replacement) {
    Font font = UIUtil.getLabelFont();
    return font == null || font.canDisplayUpTo(value) == -1 ? value : replacement;
  }
}
