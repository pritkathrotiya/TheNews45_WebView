package androidx.constraintlayout.solver.widgets;

public class Rectangle {
    public int height;
    public int width;

    /* renamed from: x */
    public int f19x;

    /* renamed from: y */
    public int f20y;

    public void setBounds(int x, int y, int width2, int height2) {
        this.f19x = x;
        this.f20y = y;
        this.width = width2;
        this.height = height2;
    }

    /* access modifiers changed from: 0000 */
    public void grow(int w, int h) {
        this.f19x -= w;
        this.f20y -= h;
        this.width += w * 2;
        this.height += h * 2;
    }

    /* access modifiers changed from: 0000 */
    public boolean intersects(Rectangle bounds) {
        int i = this.f19x;
        int i2 = bounds.f19x;
        if (i >= i2 && i < i2 + bounds.width) {
            int i3 = this.f20y;
            int i4 = bounds.f20y;
            if (i3 >= i4 && i3 < i4 + bounds.height) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int x, int y) {
        int i = this.f19x;
        if (x >= i && x < i + this.width) {
            int i2 = this.f20y;
            if (y >= i2 && y < i2 + this.height) {
                return true;
            }
        }
        return false;
    }

    public int getCenterX() {
        return (this.f19x + this.width) / 2;
    }

    public int getCenterY() {
        return (this.f20y + this.height) / 2;
    }
}
