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
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().add(R.id.cassi_main_fragment_container,
                    new MainScreenFragment()).commit();
        }
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
                    getSupportFragmentManager().popBackStack();
                    getSupportActionBar().setTitle(R.string.app_name);
                    break;
                case R.id.cassi_menu_pattern_settings:
                    getSupportFragmentManager().popBackStackImmediate();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.cassi_main_fragment_container,
                                    new PatternFragment()).addToBackStack(null).commit();
                    getSupportActionBar().setTitle(R.string.cassi_patterns_title);
                    break;
                case R.id.cassi_menu_pattern_mappings:
                    getSupportFragmentManager().popBackStackImmediate();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.cassi_main_fragment_container,
                                    new PatternMappingFragment()).addToBackStack(null).commit();
                    getSupportActionBar().setTitle(R.string.cassi_mapping_title);
                    break;
                case R.id.cassi_menu_network_ble:
                    getSupportFragmentManager().popBackStackImmediate();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.cassi_main_fragment_container,
                                    new BLESettingsFragment()).addToBackStack(null).commit();
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
        switch(requestCode) {
            case MainScreenFragment.PERMISSION_BLUETOOTH_REQUEST_ID:
                if(grantResults.length==1&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    ((Button) findViewById(R.id.cassi_start)).performClick();
                } else {
                    Snackbar.make(mDrawerLayout, getString(
                            R.string.cassi_permissions_error_bluetooth),
                            Snackbar.LENGTH_LONG).show();
                }
                break;
            case MainScreenFragment.PERMISSION_BLUETOOTH_ADMIN_REQUEST_ID:
                if(grantResults.length==1&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    ((Button) findViewById(R.id.cassi_start)).performClick();
                } else {
                    Snackbar.make(mDrawerLayout, getString(
                            R.string.cassi_permissions_error_bluetooth_admin),
                            Snackbar.LENGTH_LONG).show();
                }
                break;
            case MainScreenFragment.PERMISSION_ACCESS_COARSE_LOCATION_REQUEST_ID:
                if(grantResults.length==1&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    ((Button) findViewById(R.id.cassi_start)).performClick();
                } else {
                    Snackbar.make(mDrawerLayout, getString(
                            R.string.cassi_permissions_error_access_coarse_location),
                            Snackbar.LENGTH_LONG).show();
                }
                break;
            case MainScreenFragment.PERMISSION_RECEIVE_PHONE_STATE_REQUEST_ID:
                if(grantResults.length==1&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    ((Button) findViewById(R.id.cassi_start)).performClick();
                } else {
                    Snackbar.make(mDrawerLayout, getString(
                            R.string.cassi_permissions_error_receive_phone_state),
                            Snackbar.LENGTH_LONG).show();
                }
                break;
            case MainScreenFragment.PERMISSION_RECEIVE_SMS_REQUEST_ID:
                if(grantResults.length==1&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    ((Button) findViewById(R.id.cassi_start)).performClick();
                } else {
                    Snackbar.make(mDrawerLayout, getString(
                            R.string.cassi_permissions_error_receive_sms),
                            Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==MainScreenFragment.ENABLE_BLUETOOTH) {
            if(resultCode==RESULT_OK) {
                ((Button) findViewById(R.id.start)).performClick();
            } else {
                Snackbar.make(mDrawerLayout, getString(
                        R.string.cassi_permissions_error_bluetooth_not_enabled),
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(findViewById(R.id.cassi_nav))) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}