package com.miraclink.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.miraclink.R;

public class EditLineLayout extends ConstraintLayout {
    private TextView textFunction;
    private EditText editInfo;
    private ImageView imgMore;
    private View viewTopLine;
    private View viewBottomLine;

    public EditLineLayout(Context context) {
        super(context);
    }

    public EditLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_edit_line_layout, this);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditLineLayout);
        if (typedArray != null) {
            textFunction.setText(typedArray.getString(R.styleable.EditLineLayout_functionText));
            editInfo.setHint(typedArray.getString(R.styleable.EditLineLayout_initInfoText));
            if (typedArray.getInt(R.styleable.EditLineLayout_imageMore, 0) == 1) {
                imgMore.setImageResource(R.drawable.ic_store_line_more);
            }
            if ("num".equals(typedArray.getString(R.styleable.EditLineLayout_numType))) {
                editInfo.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            boolean topLineIsVisible = typedArray.getBoolean(R.styleable.EditLineLayout_topLine, true);
            boolean bottomLineIsVisible = typedArray.getBoolean(R.styleable.EditLineLayout_bottom, true);
            if (!topLineIsVisible) {
                viewTopLine.setVisibility(View.GONE);
            }
            if (!bottomLineIsVisible) {
                viewBottomLine.setVisibility(View.GONE);
            }
        }
    }

    private void init() {
        textFunction = findViewById(R.id.textEditLineLayoutFunction);
        editInfo = findViewById(R.id.editEditLineLayoutInfo);
        imgMore = findViewById(R.id.imgEditLineLayoutMore);
        viewTopLine = findViewById(R.id.viewEditLineLayoutTopLine);
        viewBottomLine = findViewById(R.id.viewEditLineLayoutBottomLine);
    }

    public EditText getInfoEditText() {
        return editInfo;
    }

    public ImageView getImgMore() {
        return imgMore;
    }
}
