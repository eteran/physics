package com.sultanik.ui;

import java.awt.event.KeyListener;

public interface UserInterface {

    public void setFocusProvider(FocusProvider focusProvider);

    public void addKeyListener(KeyListener listener);

    public void removeKeyListener(KeyListener listener);

    public void addRepaintListener(RepaintListener listener);

    public void removeRepaintListener(RepaintListener listner);

    public void repaint();
}
