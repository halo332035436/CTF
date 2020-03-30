package com.bullb.ctf.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;

import java.lang.reflect.Field;

/**
 * Created by oscarlaw on 6/10/2016.
 */

public class MyEditText extends EditText {


    @Override
    public boolean isTextSelectable() {
        return false;
    }

    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }

    public MyEditText(Context context)
    {
        super(context);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        this.setCustomSelectionActionModeCallback(new ActionModeCallbackInterceptor());
        this.setLongClickable(false);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // setInsertionDisabled when user touches the view
            this.setInsertionDisabled();
        }
        return super.onTouchEvent(event);
    }

    /**
     * This method sets TextView#Editor#mInsertionControllerEnabled field to false
     * to return false from the Editor#hasInsertionController() method to PREVENT showing
     * of the insertionController from EditText
     * The Editor#hasInsertionController() method is called in  Editor#onTouchUpEvent(MotionEvent event) method.
     */

    private void setInsertionDisabled() {
        try {
            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editorObject = editorField.get(this);

            Class editorClass = Class.forName("android.widget.Editor");
            Field mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled");
            mInsertionControllerEnabledField.setAccessible(true);
            mInsertionControllerEnabledField.set(editorObject, false);
        }
        catch (Exception ignored) {
            // ignore exception here
        }
    }

    boolean canPaste()
    {
        return false;
    }

    /**
     * Prevents the action bar (top horizontal bar with cut, copy, paste, etc.) from appearing
     * by intercepting the callback that would cause it to be created, and returning false.
     */
    private class ActionModeCallbackInterceptor implements ActionMode.Callback
    {
        private final String TAG = MyEditText.class.getSimpleName();

        public boolean onCreateActionMode(ActionMode mode, Menu menu) { return false; }
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) { return false; }
        public void onDestroyActionMode(ActionMode mode) {}
    }

}
