# rule should be already part of logback android (https://github.com/tony19/logback-android/issues/344)
-keepclassmembers class ch.qos.logback.core.rolling.helper.* { <init>(); }