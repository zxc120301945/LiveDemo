# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\developer\android_studio\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#来疯sdk
-keep class com.laifeng.sopcastsdk.entity.UploadUrlInfo{*;}
-keep class com.laifeng.sopcastsdk.entity.UploadServerInfo{*;}
-keep class com.laifeng.sopcastsdk.entity.UploadServerResponse{*;}

#——自定义控件——#
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#——自定义控件——#

#fastJson
-keep class com.alibaba.fastjson.** { *; }

#eventbus框架
-keepclassmembers class ** {
    public void onEvent*(**);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#okio
-dontwarn okio.**
-keep class okio.** {*;}

#butterknife注入
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#Gson
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *;}
# Application classes that will be serialized/deserialized over Gson
-keep class com.antew.redditinpictures.library.imgur.** { *; }
-keep class com.antew.redditinpictures.library.reddit.** { *; }

-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.wechat.**{*;}
-keep class com.mob.**{*;}
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*

#（实体类不混淆）
-keep class com.you.edu.live.teacher.model.bean.**{*;}
-keep class com.you.edu.live.teacher.support.http.model.**{*;}