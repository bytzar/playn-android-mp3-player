package tr.bahri.playn;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.Objects;

public class CustomEditableTextView extends AppCompatAutoCompleteTextView {

    public CustomEditableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Set initial properties
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setClickable(true);

        this.setAdapter(null);

        setImeOptions(EditorInfo.IME_ACTION_DONE);
        setRawInputType(InputType.TYPE_CLASS_TEXT);
        setMaxLines(2); // Allow only two lines
        setImeOptions(EditorInfo.IME_ACTION_DONE); // Show "Done" button on keyboard
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCursorVisible(true);
            }
        });

        this.setOnFocusChangeListener((v, hasFocus) -> {
                Toast.makeText(getContext(), "sexlersdf", Toast.LENGTH_LONG).show();

        });

        this.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    System.out.println("JAO");
                    newMEth();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindowToken(), 0);
                    return true; // Return true to indicate that the event has been handled
                }
                return false; // Return false if you want the default behavior to be executed as well
            }
        });


        this.setOnFocusChangeListener((v, hasFocus) -> System.out.println("FOCUS CHANGE"));
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println(getText());
                setAdapter(null);
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(getText());
                setCursorVisible(true);
                setAdapter(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                setAdapter(null);
                if (Objects.requireNonNull(getText()).toString().length() == 0)
                {
                    setText(" ");
                }
                if (Objects.requireNonNull(getText()).toString().length() > 48) {
                    // Remove extra lines

                    setText(getText().toString().substring(0, 48));
                    setSelection(getText().length());
                }
            }

        });
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            newMEth();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void newMEth()
    {
        if (Objects.equals(Objects.requireNonNull(getText()).toString(), " "))
        {
            setText("this wont go empty");
        }

        this.setCursorVisible(false);
    }

    @Override
    public void setOnEditorActionListener(OnEditorActionListener l) {
        System.out.println("SECCCSSE  XXX");
        super.setOnEditorActionListener(l);
    }


    @Override
    public void onEditorAction(int actionCode) {
        super.onEditorAction(actionCode);
    }

}
