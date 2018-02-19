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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import marm.mobile.cassi.model.PSMapping;
import marm.mobile.cassi.model.Pattern;

/**
 * Fragment for setting the mapping between services and patterns.
 *
 * @author Martin Armbruster
 */
public class PatternMappingFragment extends Fragment {
    /**
     * Stores the actual mapping.
     */
    private PSMapping map;

    /**
     * Creates a new instance.
     */
    public PatternMappingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View resultingView = inflater.inflate(R.layout.fragment_pattern_mapping, container, false);
        map = new PSMapping();
        Spinner spin = resultingView.findViewById(R.id.cassi_mapping_calls);
        prepareSpinner(spin, PSMapping.CALL);
        spin = resultingView.findViewById(R.id.cassi_mapping_sms);
        prepareSpinner(spin, PSMapping.SMS);
        return resultingView;
    }

    /**
     * Prepares a spinner for displaying patterns and updating the mapping.
     *
     * @param spin the spinner to prepare.
     * @param service the service for which the spinner is prepared.
     */
    private void prepareSpinner(Spinner spin, final String service) {
        spin.setAdapter(new ArrayAdapter<Pattern>(this.getContext(),
                R.layout.patterns_spinner_text, map.getPatternManager().getPatterns()));
        spin.setSelection(map.getPatternManager().getPatterns().indexOf(
                map.getMapping(service)));
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                map.setMapping(service, map.getPatternManager().getPatterns().get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}