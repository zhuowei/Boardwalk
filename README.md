ABRefrigerator/
├── settings.gradle
├── build.gradle
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/ab/refrigerator/MainActivity.java
│       └── res/
│           ├── layout/activity_main.xml
│           └── values/
│               ├── colors.xml
│               ├── strings.xml
│               └── themes.xml
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ABRefrigerator"
include(":app")
plugins {
    id 'com.android.application' version '8.6.1' apply false
}plugins {
    id 'com.android.application'
}

android {
    namespace 'com.ab.refrigerator'
    compileSdk 35

    defaultConfig {
        applicationId "com.ab.refrigerator"
        minSdk 23
        targetSdk 35
        versionCode 1
        versionName "1.0"
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
}<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.CALL_PHONE"/>

    <application
        android:theme="@style/Theme.ABRefrigerator"
        android:label="AB Refrigerator & Air Conditioner"
        android:allowBackup="true"
        android:supportsRtl="true">

        <activity
            android:name=".MainActivity"
            android:exported="true">

            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>

        </activity>

    </application>

</manifest>
package com.ab.refrigerator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String PHONE = "9721439186";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button callButton = findViewById(R.id.callButton);
        Button whatsappButton = findViewById(R.id.whatsappButton);
        Button bookingButton = findViewById(R.id.bookingButton);

        callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + PHONE));
            startActivity(intent);
        });

        whatsappButton.setOnClickListener(v -> {
            String message =
                    "Hello AB Refrigerator & Air Conditioner, " +
                    "मुझे AC/Refrigerator की service चाहिए।";

            try {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://wa.me/91" + PHONE +
                                "?text=" + Uri.encode(message))
                );
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "WhatsApp उपलब्ध नहीं है",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        bookingButton.setOnClickListener(v -> {
            String message =
                    "Service Booking Request\n\n" +
                    "Name: \n" +
                    "Address: \n" +
                    "AC/Refrigerator Problem: ";

            try {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://wa.me/91" + PHONE +
                                "?text=" + Uri.encode(message))
                );
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "WhatsApp उपलब्ध नहीं है",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
<?xml version="1.0" encoding="utf-8"?>

<ScrollView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="24dp"
        android:gravity="center_horizontal">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="AB"
            android:textSize="48sp"
            android:textStyle="bold"
            android:layout_marginTop="30dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Refrigerator & Air Conditioner"
            android:textSize="22sp"
            android:textStyle="bold"
            android:gravity="center"
            android:layout_marginTop="8dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="AC और Refrigerator की सभी प्रकार की Service"
            android:textSize="16sp"
            android:gravity="center"
            android:layout_marginTop="12dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="हमारी Services\n\n• AC Repair\n• AC Installation\n• AC Gas Charging\n• AC Servicing\n• Refrigerator Repair\n• Deep Freezer Repair\n• Compressor Checking\n• Cooling Problem"
            android:textSize="17sp"
            android:layout_marginTop="30dp"/>

        <Button
            android:id="@+id/bookingButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="📅 Service Booking"
            android:layout_marginTop="25dp"/>

        <Button
            android:id="@+id/callButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="📞 Call Now"
            android:layout_marginTop="12dp"/>

        <Button
            android:id="@+id/whatsappButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="💬 WhatsApp"
            android:layout_marginTop="12dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Arun Nishad\nRabale Sainagar\nService: 10 AM - 9 PM\nEmergency Service: Available"
            android:textSize="16sp"
            android:gravity="center"
            android:layout_marginTop="30dp"/>

    </LinearLayout>

</ScrollView>
<resources>
    <string name="app_name">AB Refrigerator & Air Conditioner</string>
</resources>
<resources>
    <color name="black">#000000</color>
    <color name="white">#FFFFFF</color>
</resources>
<resources xmlns:tools="http://schemas.android.com/tools">

    <style name="Theme.ABRefrigerator"
        parent="Theme.MaterialComponents.DayNight.DarkActionBar">

        <item name="android:fontFamily">sans</item>
        <item name="android:windowLightStatusBar">true</item>

    </style>

</resources>
