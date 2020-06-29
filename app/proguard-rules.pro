# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class MID to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file MID.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5

-dontusemixedcaseclassnames

-dontskipnonpubliclibraryclasses

-dontoptimize

-dontpreverify

-verbose

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*

-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
#support librariesv4*v7
-dontwarn android.support.**
-keep class android.support.** { *;}
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep public class android.support.v7.** { *; }


-ignorewarnings

-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt


-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

-keep class com.hop.pirate.activity.** {*;}
-keep class com.hop.pirate.activity.OperationBean {*;}
-keep class com.hop.pirate.util.Utils {*;}
-keep class com.hop.pirate.util.DensityUtil {*;}
-keep class com.hop.pirate.util.AccountUtils {*;}
-keep class com.hop.pirate.EthereumAccount {*;}




-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
  }

-keepclasseswithmembernames class * {
      native <methods>;
 }

-keepclasseswithmembers class * {
      public <init>(android.content.Context, android.util.AttributeSet);
}

   -keepclassmembers class * extends android.app.Activity {
      public void *(android.view.View);
}

-keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }

-keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
    }

-keepclassmembers class * {
        public void *ButtonClicked(android.view.View);
    }

-keepclassmembers class **.R$* {
        public static <fields>;
    }

    -keep class com.google.zxing.** { *; }
    -keep class com.google.zxing.**


#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }


-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}