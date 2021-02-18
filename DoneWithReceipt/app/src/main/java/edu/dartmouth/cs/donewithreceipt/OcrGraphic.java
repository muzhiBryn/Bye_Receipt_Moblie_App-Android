package edu.dartmouth.cs.donewithreceipt;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import edu.dartmouth.cs.donewithreceipt.uicamera.GraphicOverlay;

import java.util.List;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private int mId;

    private static final int TEXT_COLOR = Color.WHITE;

    private static final int BLOCK_SETTING = 1; //Break the lines into its component words and draw each one according to its own bounding box.

    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private final TextBlock mText;
    private int tblock;


    OcrGraphic(GraphicOverlay overlay, TextBlock text) {
        super(overlay);

        mText = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(54.0f);
        }
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();

        tblock = BLOCK_SETTING;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public TextBlock getTextBlock() {
        return mText;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     *
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        TextBlock text = mText;
        if (text == null) {
            return false;
        }
        RectF rect = new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
    }


    public String getText(float x, float y) {
        final TextBlock text = mText;
        switch (tblock) {
            case 0:
                return mText.getValue();
            case 1:
                List<? extends Text> textComponents = text.getComponents();
                for (Text currentText : textComponents) {
                    final RectF rect1 = new RectF(currentText.getBoundingBox());

                    rect1.left = translateX(rect1.left);
                    rect1.top = translateY(rect1.top);
                    rect1.right = translateX(rect1.right);
                    rect1.bottom = translateY(rect1.bottom);
                    if (rect1.left < x && rect1.right > x && rect1.top < y && rect1.bottom > y) {
                        return currentText.getValue();
                    }
                }
                return "";
            case 2:
                List<? extends Text> textComponents1 = text.getComponents();
                for (Text currentText : textComponents1) {
                    List<? extends Text> lineComponents = currentText.getComponents();
                    for (Text currentText1 : lineComponents) {
                        final RectF rect1 = new RectF(currentText1.getBoundingBox());

                        rect1.left = translateX(rect1.left);
                        rect1.top = translateY(rect1.top);
                        rect1.right = translateX(rect1.right);
                        rect1.bottom = translateY(rect1.bottom);
                        if (rect1.left < x && rect1.right > x && rect1.top < y && rect1.bottom > y) {
                            return currentText1.getValue();
                        }
                    }
                }
                return "";
            default:
                return "";
        }

    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(final Canvas canvas) {
        final TextBlock text = mText;
        if (text == null) {
            return;
        }

        // Draws the bounding box around the TextBlock.

        Log.d("OcrGraphic", "Selected tblock option: " + Integer.toString(tblock));

        switch (tblock) {

            case 0:
                //Draw the text blocks without breaking the text
                final RectF rect = new RectF(text.getBoundingBox());
                rect.left = translateX(rect.left);
                rect.top = translateY(rect.top);
                rect.right = translateX(rect.right);
                rect.bottom = translateY(rect.bottom);
                canvas.drawRect(rect, sRectPaint);

                canvas.drawText(text.getValue(), rect.left, rect.bottom, sTextPaint);
                break;

            case 2:
                // Break the text into multiple lines and draw each one according to its own bounding box.

                List<? extends Text> textComponents1 = text.getComponents();
                for (Text currentText : textComponents1) {
                    //float left = translateX(currentText.getBoundingBox().left);
                    //float bottom = translateY(currentText.getBoundingBox().bottom);
                    //canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);

                    List<? extends Text> lineComponents = currentText.getComponents();
                    for (Text currentText1 : lineComponents) {
                        final RectF rect1 = new RectF(currentText1.getBoundingBox());

                        rect1.left = translateX(rect1.left);
                        rect1.top = translateY(rect1.top);
                        rect1.right = translateX(rect1.right);
                        rect1.bottom = translateY(rect1.bottom);
                        canvas.drawRect(rect1, sRectPaint);

                        float left = translateX(currentText1.getBoundingBox().left);
                        float bottom = translateY(currentText1.getBoundingBox().bottom);
                        canvas.drawText(currentText1.getValue(), left, bottom, sTextPaint);
                    }
                }
                break;

            default:
                // Break the lines into its component words and draw each one according to its own bounding box.

                List<? extends Text> textComponents = text.getComponents();
                for (Text currentText : textComponents) {
                    final RectF rect1 = new RectF(currentText.getBoundingBox());

                    rect1.left = translateX(rect1.left);
                    rect1.top = translateY(rect1.top);
                    rect1.right = translateX(rect1.right);
                    rect1.bottom = translateY(rect1.bottom);
                    canvas.drawRect(rect1, sRectPaint);

                    float left = translateX(currentText.getBoundingBox().left);
                    float bottom = translateY(currentText.getBoundingBox().bottom);
                    canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
                }
                break;
        }
    }
}
