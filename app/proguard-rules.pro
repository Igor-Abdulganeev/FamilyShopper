# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
    <fields>;
}

# Сохраняем Companion-объекты и методы сериализатора
-keepclassmembers class * {
    *** Companion;
    *** serializer(...);
}

#Сохраняем Room
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.Entity

# Предотвращаем обфускацию имен типов в Retrofit
#-keep class retrofit2.** { *; }
-keepattributes Signature

-keep class ru.gorinih.familyshopper.data.db.models.** { *; }
-keep class ru.gorinih.familyshopper.data.remote.models.** { *; }
-keep class ru.gorinih.familyshopper.domain.models.** { *; }
-keep class ru.gorinih.familyshopper.ui.models.** { *; }
-keep class ru.gorinih.familyshopper.ui.screens.dictionary.models.** { *; }
-keep class ru.gorinih.familyshopper.ui.screens.editlist.models.** { *; }
-keep class ru.gorinih.familyshopper.ui.screens.lists.models.** { *; }
-keep class ru.gorinih.familyshopper.ui.screens.settings.models.** { *; }
-keep class ru.gorinih.familyshopper.ui.screens.strikelist.models.** { *; }
-keep class ru.gorinih.familyshopper.ui.widget.models.** { *; }