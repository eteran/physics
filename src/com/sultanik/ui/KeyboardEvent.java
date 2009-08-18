package com.sultanik.ui;

import java.awt.*;
import java.awt.event.*;

public class KeyboardEvent extends KeyEvent {
    static int id = 0;
    public KeyboardEvent(int key) {
        super(new Canvas(), id++, System.currentTimeMillis(), 0, key, (char)key);
    }
    public KeyboardEvent(char c) {
        super(new Canvas(), id++, System.currentTimeMillis(), 0, lookupKeyCode(c), c);
    }
    public static int lookupKeyCode(int key) {
        return 0;
    }
}