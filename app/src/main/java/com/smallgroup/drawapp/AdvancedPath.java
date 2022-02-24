package com.smallgroup.drawapp;

import android.graphics.Paint;
import android.graphics.Path;

public class AdvancedPath {

    private Path path;
    private Paint paint;

    public AdvancedPath() {}

    public AdvancedPath(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
