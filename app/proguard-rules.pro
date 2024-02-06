## Add project specific ProGuard rules here.
## You can control the set of applied configuration files using the
## proguardFiles setting in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
#
## Uncomment this to preserve the line number information for
## debugging stack traces.
##-keepattributes SourceFile,LineNumberTable
#
## If you keep the line number information, uncomment this to
## hide the original source file name.
##-renamesourcefileattribute SourceFile

#-keep class com.msandypr.thesandynews.models.** { *; }
#-keep class com.msandypr.thesandynews.ui.** { *; }
#-keep class com.msandypr.thesandynews.api.** { *; }
#-keep class com.msandypr.thesandynews.adapters.** { *; }
#-keep class com.msandypr.thesandynews.db.** { *; }
#-keep class com.msandypr.thesandynews.repository.** { *; }
#-keep class com.msandypr.thesandynews.util.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit.** { *; }
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okhttp3.internal.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

#Gson
-keep class com.google.gson.** { *; }

-keepattributes SourceFile,LineNumberTable
-keep class * {
    public private *;
}

-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

