package com.sultanik.ui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

public class JCurses implements UserInterface {

    PrintStream out;
    StringBuffer[] prevBuffer;
    StringBuffer[] buffer;
    int row, col;
    Object swapMutex = new Object();
    int height, width;
    FocusProvider focusProvider;
    LinkedHashSet<RepaintListener> listeners;
    LinkedHashSet<KeyListener> keyListeners;
    CursesGraphics cg;

    static Class<?> toolkit = null;
    static Class<?> charColor = null;
    static Class<?> inputChar = null;
    static Method clearMethod = null;
    static Method heightMethod = null;
    static Method widthMethod = null;
    static Method printMethod = null;
    static Method readMethod = null;
    Color color;
    static Map<Color, Object> charColors = new HashMap<>();

    static {
        try {
            toolkit = Class.forName("jcurses.system.Toolkit");
            charColor = Class.forName("jcurses.system.CharColor");
            clearMethod = toolkit.getMethod("clearScreen", charColor);
            heightMethod = toolkit.getMethod("getScreenHeight");
            widthMethod = toolkit.getMethod("getScreenWidth");
            printMethod = toolkit.getMethod("printString", String.class, int.class, int.class, charColor);
            readMethod = toolkit.getMethod("readCharacter");
            inputChar = Class.forName("jcurses.system.InputChar");
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /* TODO: make this higher once I implement the intelligent update mechanism */
    private static final double CLEAR_THRESHOLD = 0.0; /* the
     * percentage
     * of changed
     * characters
     * above which
     * a complete
     * clear-and-repaint
     * is
     * performed */


    @Override
    public void setFocusProvider(FocusProvider focusProvider) {
        this.focusProvider = focusProvider;
    }

    @Override
    public void addKeyListener(KeyListener listener) {
        keyListeners.add(listener);
    }

    @Override
    public void removeKeyListener(KeyListener listener) {
        keyListeners.remove(listener);
    }

    @Override
    public void addRepaintListener(RepaintListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeRepaintListener(RepaintListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void repaint() {
        //clear();
        height = getRealHeight();
        width = getRealWidth();
        cg.setWidth(getWidth());
        cg.setHeight(getHeight());
        clearCommand();

        if (focusProvider != null) {
            java.awt.geom.Point2D p = focusProvider.getFocalPoint();
            cg.xOffset = p.getX() - cg.getWidth() / 2.0;
            cg.yOffset = p.getY() - cg.getHeight() / 2.0;
            if (cg.xOffset < 0.0) {
                cg.xOffset = 0.0;
            }
            if (cg.yOffset < 0.0) {
                cg.yOffset = 0.0;
            }
        }

        for (RepaintListener listener : listeners) {
            listener.paint(cg);
            //refresh();
        }
    }

    public JCurses() {
        this(System.out);
    }

    @SuppressWarnings("unused")
    private static class KeyThread extends Thread {

        JCurses jc;

        KeyThread(JCurses jc) {
            super();
            this.jc = jc;
            if (readMethod != null) {
                start();
            }
        }

        @Override
        public void run() {
            for (;;) {
                try {
                    Object ic = readMethod.invoke(null);
                    boolean hasCharacter = false;
                    char c = ' ';
                    int code = 0;
                    try {
                        Method m = inputChar.getMethod("getCharacter");
                        c = (Character)m.invoke(ic);
                        hasCharacter = true;
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    }
                    if (!hasCharacter) {
                        try {
                            Method m = inputChar.getMethod("getCode");
                            code = (Integer)m.invoke(ic);
                        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    KeyboardEvent e;
                    if (hasCharacter) {
                        e = new KeyboardEvent(c);
                    } else {
                        e = new KeyboardEvent(code);
                    }
                    for (KeyListener l : jc.keyListeners) {
                        l.keyPressed(e);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JCurses(PrintStream out) {
        listeners = new LinkedHashSet<>();
        keyListeners = new LinkedHashSet<>();
        try {
            initializeTerminal();
        } catch (IOException | InterruptedException e) {
        }
        color = Color.BLACK;
        this.out = out;
        height = getRealHeight();
        width = getRealWidth();
        clear();
        row = 0;
        col = 0;
        //KeyThread kt = new KeyThread(this);
        cg = new CursesGraphics(this, 0.6, getWidth(), getHeight(), 0.0, 0.0);
    }

    /**
     * Execute the stty command with the specified arguments against the current
     * active terminal.
     */
    private static String stty(final String args) throws IOException, InterruptedException {
        return exec("stty " + args + " < /dev/tty").trim();
    }

    /**
     * Execute the specified command and return the output (both stdout and
     * stderr).
     */
    private static String exec(final String cmd) throws IOException, InterruptedException {
        return exec(new String[]{"sh", "-c", cmd});
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Execute the specified command and return the output (both stdout and
     * stderr).
     */
    private static String exec(final String[] cmd) throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in;

        in = p.getInputStream();
        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();
        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        String result = new String(bout.toByteArray());
        return result;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private static int getRealWidth() {
        if (widthMethod != null) {
            try {
                return (Integer)widthMethod.invoke(null);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            }
        }
        //return Integer.parseInt(System.getenv("COLS"));
        int val = 80;
        try {
            String size = stty("size");
            if (size.length() != 0 && size.contains(" ")) {
                val = Integer.parseInt(size.substring(size.indexOf(' ') + 1));
            }
        } catch (IOException | InterruptedException | NumberFormatException e) {
        }
        return val;
    }

    private static int getRealHeight() {
        if (heightMethod != null) {
            try {
                return (Integer)heightMethod.invoke(null);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            }
        }
        //System.out.println(System.getenv());
        //return Integer.parseInt(System.getenv("LINES"));
        int val = 24;
        try {
            String size = stty("size");
            if (size.length() != 0 && size.contains(" ")) {
                val = Integer.parseInt(size.substring(0, size.indexOf(' ')));
            }
        } catch (IOException | InterruptedException | NumberFormatException e) {
        }
        return val;
    }

    public void clear() {
        buffer = new StringBuffer[height];
        prevBuffer = new StringBuffer[height];
        for (int i = 0; i < height; i++) {
            buffer[i] = new StringBuffer();
            prevBuffer[i] = new StringBuffer();
        }
        refresh();
        row = 0;
        col = 0;
    }

    void clearCommand() {
        row = 0;
        col = 0;
        if (clearMethod != null) {
            try {
                clearMethod.invoke(null, getCharColor(Color.WHITE));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            }
            return;
        }
        //out.print("\033[2J");
        out.print("\033[0;0H");
    }

    Object getCharColor(Color c) {
        Object cc = charColors.get(c);
        if (cc != null) {
            return cc;
        }
        
        if (c == null) {
            return null;
        }
        
        String fieldName = "BLACK";        
        if (c == Color.RED) {
            fieldName = "RED";
        } else if (c == Color.WHITE) {
            fieldName = "WHITE";
        } else if (c == Color.BLUE) {
            fieldName = "BLUE";
        } else if (c == Color.GREEN) {
            fieldName = "GREEN";
        } else if (c == Color.MAGENTA) {
            fieldName = "MAGENTA";
        } else if (c == Color.CYAN) {
            fieldName = "CYAN";
        } else if (c == Color.YELLOW) {
            fieldName = "YELLOW";
        } else {
            fieldName = "BLACK";
        }
        try {
            cc = charColor.getConstructor(short.class, short.class).newInstance(charColor.getField("WHITE").getShort(null), charColor.getField(fieldName).getShort(null));
            charColors.put(c, cc);
        } catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return cc;
    }

    void initializeTerminal() throws IOException, InterruptedException {
        if (toolkit != null) {
            try {
                toolkit.getMethod("init").invoke(null);
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void start() {
                        try {
                            toolkit.getMethod("shutdown").invoke(null);
                        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            // save the initial tty configuration
            final String ttyConfig = stty("-g");

            // sanity check
            if (ttyConfig.length() == 0
                    || (!ttyConfig.contains("=") && !ttyConfig.contains(":"))) {
                throw new IOException("Unrecognized stty code: " + ttyConfig);
            }

            // set the console to be character-buffered instead of line-buffered
            stty("-icanon min 1");

            // disable character echoing
            stty("-echo");

            /* hide the cursor */
            out.print("\033[?25l");

            // at exit, restore the original tty configuration (for JDK 1.3+)
            try {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void start() {
                        try {
                            stty(ttyConfig);
                            /* show the cursor */
                            out.print("\033[?25h");
                        } catch (IOException | InterruptedException e) {
                        }
                    }
                });
            } catch (AbstractMethodError ame) {
            }
        }
    }

    void drawString(String text, int x, int y) {
        try {
            printMethod.invoke(null, text, x, y, getCharColor(color));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void print(String text, int x, int y) {
        synchronized (swapMutex) {
            for (int i = 0; i < text.length(); i++) {
                if (y >= getHeight()) {
                    return;
                }
                while (buffer[y].length() <= x) {
                    buffer[y].append(' ');
                }
                char c = text.charAt(i);
                if (c == '\n') {
                    x = 0;
                    y++;
                } else {
                    buffer[y].setCharAt(x, text.charAt(i));
                    x++;
                }
            }
        }
    }

    public void print(String text) {
        synchronized (swapMutex) {
            for (int i = 0; i < text.length(); i++) {
                if (row >= getHeight()) {
                    return;
                }
                while (buffer[row].length() <= col) {
                    buffer[row].append(' ');
                }
                char c = text.charAt(i);
                if (c == '\n') {
                    col = 0;
                    row++;
                } else {
                    buffer[row].setCharAt(col, text.charAt(i));
                    col++;
                }
            }
        }
    }

    public void println(String text) {
        print(text + "\n");
    }

    private static class CharUpdate {
//        public int row, col;
//        public char newChar;

        CharUpdate(int row, int col, char newChar) {
//            this.row = row;
//            this.col = col;
//            this.newChar = newChar;
        }
    }

    public void moveTo(int newRow, int newCol) {
        row = newRow;
        col = newCol;
        /* TODO: actually get this to move the real cursor */
    }

    public synchronized void refresh() {
        LinkedList<CharUpdate> newChars = new LinkedList<>();

        height = getRealHeight();
        width = getRealWidth();

        synchronized (swapMutex) {
            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    char newChar = ' ';
                    if (y < buffer.length && x < buffer[y].length()) {
                        newChar = buffer[y].charAt(x);
                    }
                    char oldChar = ' ';
                    if (y < prevBuffer.length && x < prevBuffer[y].length()) {
                        oldChar = prevBuffer[y].charAt(x);
                    }
                    if (newChar != oldChar) {
                        /* we need to update this character */
                        newChars.addLast(new CharUpdate(y, x, newChar));
                        if (prevBuffer.length > y) {
                            while (prevBuffer[y].length() <= x) {
                                prevBuffer[y].append(' ');
                            }
                            prevBuffer[y].setCharAt(x, newChar);
                        }
                    }
                }
            }

            StringBuffer[] tmp = prevBuffer;
            prevBuffer = buffer;
            if (tmp.length < getHeight()) {
                buffer = new StringBuffer[height];
                for (int i = 0; i < height; i++) {
                    buffer[i] = new StringBuffer();
                }
            } else {
                buffer = tmp;
            }
        }

        if ((double)newChars.size() / (double)(getWidth() * getHeight()) > CLEAR_THRESHOLD) {
            /* just clear the whole screen and repaint from scratch */
            clearCommand();
            for (int y = 0; y < getHeight(); y++) {
                if (printMethod != null) {
                    if (y < buffer.length && buffer[y] != null) {
                        try {
                            printMethod.invoke(null, buffer[y].toString(), 0, y, getCharColor(color));
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (int x = 0; x < getWidth(); x++) {
                        char newChar = ' ';
                        if (y < buffer.length && x < buffer[y].length()) {
                            newChar = buffer[y].charAt(x);
                        }
                        out.print(newChar);
                    }
                    out.print("\n");
                }
            }
        } else if (!newChars.isEmpty()) {
            /* print the new characters one-by-one */
//            int curX = col;
//            int curY = row;
//            for(CharUpdate c : newChars) {
//                /* TODO: finish this! */
//            }
        }
    }

    private static class KeyHandler extends KeyAdapter {

        String s;

        KeyHandler(String s) {
            this.s = s;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            s += e.getKeyChar();
        }

        public String getString() {
            return s;
        }
    }

    public static void main(String[] args) {
        JCurses jc = new JCurses();
        KeyHandler kh = new KeyHandler("This is a test!");
        jc.addKeyListener(kh);
        for (int i = 0; i < jc.getHeight(); i++) {
            jc.clear();
            jc.moveTo(i, 10);
            jc.print(kh.getString());
            jc.refresh();
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        jc.clear();
        jc.refresh();
    }
}
