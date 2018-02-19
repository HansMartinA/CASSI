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

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import marm.mobile.cassi.model.FileManager;

/**
 * Main activity of CASSI, call assistant.
 *
 * @author Martin Armbruster
 */
public class MainActivity extends AppCompatActivity
        implements MainScreenFragment.OnFragmentInteractionListener {
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
                R.string.cassi_nav_open, R.string.cassi_nav_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        NavigationView navView = (NavigationView) findViewById(R.id.cassi_nav);
        navView.setNavigationItemSelectedListener(new NavClickListener());
        lastItem = navView.getMenu().getItem(0);
        FileManager.setPlatformFileManager(new AndroidFileManager(this));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
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
                default: return false;
            }
            item.setChecked(true);
            lastItem.setChecked(false);
            lastItem = item;
            mDrawerLayout.closeDrawers();
            return true;
        }
    }
}