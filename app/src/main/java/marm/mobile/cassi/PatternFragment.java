/* ########################################################
 * #####    CASSI, Call Assistant - The MIT-License    ####
 * ########################################################
 *
 * Copyright (C) 2018, Martin Armbruster
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*/

package marm.mobile.cassi;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import marm.mobile.cassi.model.FileManager;
import marm.mobile.cassi.model.Pattern;
import marm.mobile.cassi.model.PatternManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatternFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {
    /**
     * Stores the manager of the patterns.
     */
    private PatternManager pManager;
    /**
     * Stores a blank pattern that always generate a new pattern.
     */
    private Pattern empty;
    /**
     * Stores the current selected pattern.
     */
    private Pattern selectedPattern;
    /**
     * The used spinner for choosing a pattern.
     */
    private Spinner pSpinner;
    /**
     * Input field for the pattern name.
     */
    private TextView nameInput;
    /**
     * Layout for the parts' content.
     */
    private LinearLayout partsContent;

    /**
     * Creates a new instance.
     */
    public PatternFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View resultingView = inflater.inflate(R.layout.fragment_pattern, container,
                false);
        pManager = new PatternManager();
        pManager.load(FileManager.getInternalPatternDirectory());
        empty = new Pattern("["+getString(R.string.cassi_patterns_empty)+"]");
        pSpinner = resultingView.findViewById(R.id.cassi_patterns_selection);
        ArrayAdapter<Pattern> pSpinnerAdapter = new ArrayAdapter<Pattern>(this.getContext(),
                R.layout.patterns_spinner_text, pManager.getPatterns());
        pSpinnerAdapter.insert(empty, 0);
        pSpinner.setAdapter(pSpinnerAdapter);
        pSpinner.setOnItemSelectedListener(this);
        selectedPattern = empty;
        nameInput = resultingView.findViewById(R.id.cassi_patterns_name);
        ((Button) resultingView.findViewById(R.id.cassi_patterns_new_part))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newPartClicked(view);
                    }
                });
        ((Button) resultingView.findViewById(R.id.cassi_patterns_save))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveClicked(view);
                    }
                });
        ((Button) resultingView.findViewById(R.id.cassi_patterns_update))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateClicked(view);
                    }
                });
        ((Button) resultingView.findViewById(R.id.cassi_patterns_delete))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteClicked(view);
                    }
                });
        partsContent = (LinearLayout) resultingView.findViewById(R.id.cassi_patterns_content_actual);
        return resultingView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedPattern = pManager.getPatterns().get(pos);
        showSelectedPattern();
    }

    /**
     * Shows the current selected pattern.
     */
    private void showSelectedPattern() {
        nameInput.setText(selectedPattern.getName());
        partsContent.removeAllViews();
        int numberParts = selectedPattern.getNumberOfPatternParts();
        for(int i=0; i<numberParts; i++) {
            Pattern.PatternPart pp = selectedPattern.getPatternPart(i);
            newPartClicked(partsContent);
            View v = partsContent.getChildAt(partsContent.getChildCount()-1);
            EditText editor = v.findViewById(R.id.cassi_patterns_duration);
            editor.setText(String.format("%d", pp.getDuration()));
            editor = v.findViewById(R.id.cassi_patterns_m1);
            editor.setText(String.format("%d", pp.getValues()[0]));
            editor = v.findViewById(R.id.cassi_patterns_m2);
            editor.setText(String.format("%d", pp.getValues()[1]));
            editor = v.findViewById(R.id.cassi_patterns_m3);
            editor.setText(String.format("%d", pp.getValues()[2]));
            editor = v.findViewById(R.id.cassi_patterns_m4);
            editor.setText(String.format("%d", pp.getValues()[3]));
            editor = v.findViewById(R.id.cassi_patterns_m5);
            editor.setText(String.format("%d", pp.getValues()[4]));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
        selectedPattern = empty;
    }

    /**
     * Handles a click on the save button.
     *
     * @param v view.
     */
    public void saveClicked(View v) {
        String name = nameInput.getText().toString();
        for(int i=0; i<pManager.getPatterns().size(); i++) {
            if(name.equals(pManager.getPatterns().get(i).getName())) {
                Snackbar.make(pSpinner,
                        R.string.cassi_patterns_error_unique_name, Snackbar.LENGTH_LONG).show();
                return;
            }
        }
        Pattern newPattern = new Pattern(name);
        for(int i=0; i<partsContent.getChildCount(); i++) {
            try {
                View child = partsContent.getChildAt(i);
                EditText editor = child.findViewById(R.id.cassi_patterns_duration);
                int duration = Integer.parseInt(editor.getText().toString());
                byte[] values = new byte[5];
                editor = child.findViewById(R.id.cassi_patterns_m1);
                values[0] = Byte.parseByte(editor.getText().toString());
                editor = child.findViewById(R.id.cassi_patterns_m2);
                values[1] = Byte.parseByte(editor.getText().toString());
                editor = child.findViewById(R.id.cassi_patterns_m3);
                values[2] = Byte.parseByte(editor.getText().toString());
                editor = child.findViewById(R.id.cassi_patterns_m4);
                values[3] = Byte.parseByte(editor.getText().toString());
                editor = child.findViewById(R.id.cassi_patterns_m5);
                values[4] = Byte.parseByte(editor.getText().toString());
                newPattern.addPart(duration, values);
            } catch(NumberFormatException nfE) {
            }
        }
        pManager.save(FileManager.getInternalPatternDirectory(), newPattern);
        pManager.getPatterns().add(newPattern);
        selectedPattern = newPattern;
        pSpinner.setSelection(pManager.getPatterns().size()-1);
        Snackbar.make(pSpinner,
                R.string.cassi_patterns_success_save, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Handles a click on the update button.
     *
     * @param v view.
     */
    public void updateClicked(View v) {
        deleteClicked(v);
        saveClicked(v);
    }

    /**
     * Handles a click on the delete button.
     *
     * @param v view.
     */
    public void deleteClicked(View v) {
        if(selectedPattern==empty) {
            return;
        }
        pManager.delete(FileManager.getInternalPatternDirectory(), selectedPattern);
        pSpinner.setSelection(0);
        Snackbar.make(pSpinner,
                R.string.cassi_patterns_success_delete, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Handles a click on the new part button.
     *
     * @param v view.
     */
    public void newPartClicked(View v) {
        getLayoutInflater().inflate(R.layout.patterns_part, partsContent);
        final View newPart = partsContent.getChildAt(partsContent.getChildCount()-1);
        ((TextView) newPart.findViewById(R.id.cassi_patterns_lay_part))
                .setText(String.format(getString(R.string.cassi_patterns_part),
                        partsContent.getChildCount()));
        ((AppCompatImageButton) newPart.findViewById(R.id.cassi_patterns_delete_part))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletePartClicked(newPart);
                    }
                });
    }

    /**
     * Handles a click on the delete part button.
     *
     * @param v view.
     */
    public void deletePartClicked(View v) {
        partsContent.removeView(v);
        for(int i=0; i<partsContent.getChildCount(); i++) {
            ((TextView) partsContent.getChildAt(i).findViewById(R.id.cassi_patterns_lay_part))
                    .setText(String.format(getString(R.string.cassi_patterns_part), i));
        }
    }
}