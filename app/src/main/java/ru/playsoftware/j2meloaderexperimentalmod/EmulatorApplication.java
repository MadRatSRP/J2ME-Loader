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

package ru.playsoftware.j2meloaderexperimentalmod;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;

import androidx.multidex.MultiDex;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.DialogConfigurationBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.microedition.util.ContextHolder;

public class EmulatorApplication extends Application {
	private static final String[] VALID_SIGNATURES = {
			"78EF7758720A9902F731ED706F72C669C39B765C", // GPlay
			"289F84A32207DF89BE749481ED4BD07E15FC268F", // F-Droid
			"FA8AA497194847D5715BAA62C6344D75A936EBA6" // Private
	};

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		if (BuildConfig.DEBUG) {
			MultiDex.install(this);
		}
		ContextHolder.setApplication(this);
		if (isSignatureValid() && !BuildConfig.FLAVOR.equals("dev")) {
			CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
			builder.withBuildConfigClass(BuildConfig.class)
					.withParallel(false)
					.withSendReportsInDevMode(false)
					.withEnabled(true);
			builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class)
					.withResTitle(R.string.crash_dialog_title)
					.withResText(R.string.crash_dialog_message)
					.withResPositiveButtonText(R.string.report_crash)
					.withResTheme(R.style.Theme_AppCompat_Dialog)
					.withEnabled(true);
			ACRA.init(this, builder);
		}
	}

	@SuppressLint("PackageManagerGetSignatures")
	private boolean isSignatureValid() {
		try {
			Signature[] signatures;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				PackageInfo info = getPackageManager()
						.getPackageInfo(getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
				signatures = info.signingInfo.getApkContentsSigners();
			} else {
				PackageInfo info = getPackageManager()
						.getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
				signatures = info.signatures;
			}
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			for (Signature signature : signatures) {
				md.update(signature.toByteArray());
				String sha1 = bytesToHex(md.digest());
				if (Arrays.asList(VALID_SIGNATURES).contains(sha1)) {
					return true;
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String bytesToHex(byte[] bytes) {
		final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F'};
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int i = 0; i < bytes.length; i++) {
			v = bytes[i] & 0xFF;
			hexChars[i * 2] = hexArray[v >>> 4];
			hexChars[i * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
