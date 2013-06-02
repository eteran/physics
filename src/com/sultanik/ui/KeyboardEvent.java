package com.sultanik.ui;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

public class KeyboardEvent extends KeyEvent {
	private static final long serialVersionUID = 1L;
	static int id = 0;
    static Hashtable<Integer,Integer> keyMap = new Hashtable<Integer,Integer>();
    static int 	KEY_BACKSPACE = getField("KEY_BACKSPACE");
    static int 	KEY_DC = getField("KEY_DC");
    static int 	KEY_DOWN = getField("KEY_DOWN");
    static int 	KEY_END = getField("KEY_END");
    static int 	KEY_F1 = getField("KEY_F1");
    static int 	KEY_F10 = getField("KEY_F10");
    static int 	KEY_F11 = getField("KEY_F11");
    static int 	KEY_F12 = getField("KEY_F12");
    static int 	KEY_F2 = getField("KEY_F2");
    static int 	KEY_F3 = getField("KEY_F3");
    static int 	KEY_F4 = getField("KEY_F4");
    static int 	KEY_F5 = getField("KEY_F5");
    static int 	KEY_F6 = getField("KEY_F6");
    static int 	KEY_F7 = getField("KEY_F7");
    static int 	KEY_F8 = getField("KEY_F8");
    static int 	KEY_F9 = getField("KEY_F9");
    static int 	KEY_HOME = getField("KEY_HOME");
    static int 	KEY_IC = getField("KEY_IC");
    static int 	KEY_LEFT = getField("KEY_LEFT");
    static int 	KEY_NPAGE = getField("KEY_NPAGE");
    static int 	KEY_PPAGE = getField("KEY_PPAGE");
    static int 	KEY_PRINT = getField("KEY_PRINT");
    static int 	KEY_RIGHT = getField("KEY_RIGHT");
    static int 	KEY_UP = getField("KEY_UP");

    static int getField(String name) {
        try {
            return JCurses.inputChar.getField(name).getInt(null);
        } catch(Exception e) {
            System.exit(0);
            return 0;
        }
    }

    public KeyboardEvent(int key) {
        super(new Canvas(), id++, System.currentTimeMillis(), 0, lookupKeyCode(key), (char)key);
    }
    public KeyboardEvent(char c) {
        super(new Canvas(), id++, System.currentTimeMillis(), 0, (int)c, c);
    }
    public static int lookupKeyCode(int key) {
        Integer k = keyMap.get(key);
        if(k != null)
            return k;
        if(key == KEY_BACKSPACE)
            k = key;
        else if((char)key == ' ')
            k = VK_SPACE;
        else if(key == KEY_DOWN)
            k = VK_DOWN;
        else if(key == KEY_END)
            k = VK_END;
        else if(key == KEY_F1)
            k = VK_F1;
        else if(key == KEY_F10)
            k = VK_F10;
        else if(key == KEY_F11)
            k = VK_F11;
        else if(key == KEY_F12)
            k = VK_F12;
        else if(key == KEY_F2)
            k = VK_F2;
        else if(key == KEY_F3)
            k = VK_F3;
        else if(key == KEY_F4)
            k = VK_F4;
        else if(key == KEY_F5)
            k = VK_F5;
        else if(key == KEY_F6)
            k = VK_F6;
        else if(key == KEY_F7)
            k = VK_F7;
        else if(key == KEY_F8)
            k = VK_F8;
        else if(key == KEY_F9)
            k = VK_F9;
        else if(key == KEY_HOME)
            k = VK_HOME;
        else if(key == KEY_LEFT)
            k = VK_LEFT;
        else if(key == KEY_NPAGE)
            k = VK_PAGE_DOWN;
        else if(key == KEY_PPAGE)
            k = VK_PAGE_UP;
        else if(key == KEY_PRINT)
            k = VK_PRINTSCREEN;
        else if(key == KEY_RIGHT)
            k = VK_RIGHT;
        else if(key == KEY_UP)
            k = VK_UP;
        if(k == null)
            return -1;
        keyMap.put(key, k);
        return k;
    }
}