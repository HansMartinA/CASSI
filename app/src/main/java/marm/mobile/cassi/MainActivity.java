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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import marm.mobile.cassi.model.FileManager;

/**
 * Main activity of CASSI, call assistant.
 *
 * @author Martin Armbruster
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Layout of the MainActivity.
     */
    private DrawerLayout mDrawerLayout;
    /**
     * Object for (de-)activating the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     * The last selected menu item in the navigation drawer.
     */
    private MenuItem lastItem;

    /**
     * Hides the keyboard.
     *
     * @param view view that has the focus.
     * @param context context in which the view resides.
     */
    public static void hideKeyboard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tool = (Toolbar) findViewById(R.id.cassi_actionBar);
        setSupportActionBar(tool);
        getSupportFragmentManager().beginTransaction().add(R.id.cassi_main_fragment_container,
                new MainScreenFragment()).commit();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.cassi_nav_parent);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, tool,
                R.string.cassi_nav_open, R.string.cassi_nav_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                MainActivity.hideKeyboard(MainActivity.this.getCurrentFocus(), MainActivity.this);
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        NavigationView navView = (NavigationView) findViewById(R.id.cassi_nav);
        navView.setNavigationItemSelectedListener(new NavClickListener());
        lastItem = navView.getMenu().getItem(0);
        FileManager.setPlatformFileManager(new AndroidFileManager(this));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * Listener for menu items clicked in the navigation drawer.
     *
     * @author Martin Armbruster
     */
    private class NavClickListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            if(item.isChecked()) {
                mDrawerLayout.closeDrawers();
                return true;
            }
            switch(item.getItemId()) {
                case R.id.cassi_menu_main:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.cassi_main_fragment_container,
                                    new MainScreenFragment()).commit();
                    getSupportActionBar().setTitle(R.string.app_name);
                    break;
                case R.id.cassi_menu_pattern_settings:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.cassi_main_fragment_container,
                                    new PatternFragment()).commit();
                    getSupportActionBar().setTitle(R.string.cassi_patterns_title);
                    break;
                case R.id.cassi_menu_pattern_mappings:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.cassi_main_fragment_container,
                                    new PatternMappingFragment()).commit();
                    getSupportActionBar().setTitle(R.string.cassi_mapping_title);
                    break;
                case R.id.cassi_menu_network_ble:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.cassi_main_fragment_container,
                                    new BLESettingsFragment()).commit();
                    getSupportActionBar().setTitle(R.string.cassi_settings_ble_title);
                    break;
                default: return false;
            }
            item.setChecked(true);
            lastItem.setChecked(false);
            lastItem = item;
            mDrawerLayout.closeDrawers();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==MainScreenFragment.PERMISSION_REQUEST_ID) {
            for(int i=0; i<grantResults.length; i++) {
                if(grantResults[i]!= PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            ((Button) findViewById(R.id.start)).callOnClick();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==MainScreenFragment.ENABLE_BLUETOOTH&&resultCode==RESULT_OK) {
            ((Button) findViewById(R.id.start)).callOnClick();
        }
    }
}