/*
 * Copyright 2017-2018 Nikita Shakarun
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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.nononsenseapps.filepicker.Utils;

import java.io.File;

import androidx.activity.result.ActivityResultLauncher;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import ru.playsoftware.j2meloaderexperimentalmod.R;
import ru.playsoftware.j2meloaderexperimentalmod.config.Config;
import ru.playsoftware.j2meloaderexperimentalmod.config.ProfilesActivity;
import ru.playsoftware.j2meloaderexperimentalmod.util.PickDirResultContract;

import static ru.playsoftware.j2meloaderexperimentalmod.util.Constants.PREF_EMULATOR_DIR;

public class SettingsFragment extends PreferenceFragmentCompat {
	private Preference prefFolder;
	private final ActivityResultLauncher<String> openDirLauncher = registerForActivityResult(
			new PickDirResultContract(),
			this::onPickDirResult);

	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.preferences);
		findPreference("pref_default_settings").setIntent(new Intent(getActivity(), ProfilesActivity.class));
		prefFolder = findPreference(PREF_EMULATOR_DIR);
		prefFolder.setSummary(Config.getEmulatorDir());
		prefFolder.setOnPreferenceClickListener(preference -> {
			openDirLauncher.launch(null);
			return true;
		});
	}

	private void applyChangeFolder(File file) {
		if (!file.canWrite()) {
			Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
			return;
		}
		String path = file.getAbsolutePath();
		getPreferenceManager().getSharedPreferences().edit()
				.putString(PREF_EMULATOR_DIR, path)
				.apply();
		prefFolder.setSummary(path);
	}

	private void onPickDirResult(Uri uri) {
		if (uri == null) {
			return;
		}
		File file = Utils.getFileForUri(uri);
		applyChangeFolder(file);
	}
}
