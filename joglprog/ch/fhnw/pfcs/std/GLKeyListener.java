package ch.fhnw.pfcs.std;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

/**
 * Created by Claudio on 07.11.2016.
 */
public abstract class GLKeyListener implements KeyListener {

    private final HashMap<Integer, Boolean> down;
    private final HashMap<Integer, Boolean> already;

    public GLKeyListener() {
        down = new HashMap<>();
        already = new HashMap<>();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        down.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        down.put(e.getKeyCode(), false);
    }

    public abstract void update();

    public boolean isThisDown(int dis) {
        down.putIfAbsent(dis, false);
        return down.get(dis);
    }

    public boolean isLeftDown() {
        return isThisDown(KeyEvent.VK_LEFT);
    }

    public boolean isRightDown() {
        return isThisDown(KeyEvent.VK_RIGHT);
    }

    public boolean isUpDown() {
        return isThisDown(KeyEvent.VK_UP);
    }

    public boolean isDownDown() {
        return isThisDown(KeyEvent.VK_DOWN);
    }


    public boolean isSpaceDown() {
        return isThisDown(KeyEvent.VK_SPACE);
    }

    public boolean isShiftDown() {
        return isThisDown(KeyEvent.VK_SHIFT);
    }


    public boolean isADown() {
        return isThisDown(KeyEvent.VK_A);
    }

    public boolean isSDown() {
        return isThisDown(KeyEvent.VK_S);
    }

    public boolean isDDown() {
        return isThisDown(KeyEvent.VK_D);
    }

    public boolean isFDown() {
        return isThisDown(KeyEvent.VK_F);
    }

    public boolean isGDown() {
        return isThisDown(KeyEvent.VK_G);
    }


    public boolean isQDown() {
        return isThisDown(KeyEvent.VK_Q);
    }

    public boolean isWDown() {
        return isThisDown(KeyEvent.VK_W);
    }

    public boolean isEDown() {
        return isThisDown(KeyEvent.VK_E);
    }

    public boolean isRDown() {
        return isThisDown(KeyEvent.VK_R);
    }

    public boolean isTDown() {
        return isThisDown(KeyEvent.VK_T);
    }

    public boolean isZDown() {
        return isThisDown(KeyEvent.VK_Z);
    }


    public boolean isThisDownOnce(int dis) {
        if (isThisDown(dis)) {
            if (!already.containsKey(dis)) {
                already.put(dis, true);
                return true;
            } else {
                return false;
            }
        } else {
            if (already.containsKey(dis)) {
                already.remove(dis);
            }
            return false;
        }
    }

    public boolean isUpDownOnce() {
        return isThisDownOnce(KeyEvent.VK_UP);
    }

    public boolean isDownDownOnce() {
        return isThisDownOnce(KeyEvent.VK_DOWN);
    }

    public boolean isLeftDownOnce() {
        return isThisDownOnce(KeyEvent.VK_LEFT);
    }

    public boolean isRightDownOnce() {
        return isThisDownOnce(KeyEvent.VK_RIGHT);
    }


    public boolean isSpaceDownOnce() {
        return isThisDownOnce(KeyEvent.VK_SPACE);
    }


    public boolean isQDownOnce() {
        return isThisDownOnce(KeyEvent.VK_Q);
    }

    public boolean isWDownOnce() {
        return isThisDownOnce(KeyEvent.VK_W);
    }

    public boolean isEDownOnce() {
        return isThisDownOnce(KeyEvent.VK_E);
    }

    public boolean isRDownOnce() {
        return isThisDownOnce(KeyEvent.VK_R);
    }

    public boolean isADownOnce() {
        return isThisDownOnce(KeyEvent.VK_A);
    }

    public boolean isSDownOnce() {
        return isThisDownOnce(KeyEvent.VK_S);
    }

    public boolean isDDownOnce() {
        return isThisDownOnce(KeyEvent.VK_D);
    }


}
