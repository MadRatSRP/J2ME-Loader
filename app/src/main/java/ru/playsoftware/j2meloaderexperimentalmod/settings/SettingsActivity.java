/*
 * Copyright 2017 Nikita Shakarun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.playsoftware.j2meloaderexperimentalmod.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceManager;
import ru.playsoftware.j2meloaderexperimentalmod.R;
import ru.playsoftware.j2meloaderexperimentalmod.base.BaseActivity;

import static ru.playsoftware.j2meloaderexperimentalmod.util.Constants.*;

public class SettingsActivity extends BaseActivity {
	private Map<String, ?> oldParams;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.action_settings);
		setResult(getIntent().getBooleanExtra(PREF_EMULATOR_DIR, false) ? RESULT_NEED_RECREATE : RESULT_OK);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		oldParams = preferences.getAll();
	}

	@Override
	public void finish() {
		if (!getIntent().getBooleanExtra(PREF_EMULATOR_DIR, false)
				&& preferences.getString(PREF_THEME, "light").equals(oldParams.get(PREF_THEME))
				&& preferences.getString(PREF_APP_SORT, "name").equals(oldParams.get(PREF_APP_SORT))
				&& TextUtils.equals(preferences.getString(PREF_EMULATOR_DIR, null),
				(CharSequence) oldParams.get(PREF_EMULATOR_DIR))) {
			setResult(RESULT_OK);
		} else {
			setResult(RESULT_NEED_RECREATE);
		}
		super.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
