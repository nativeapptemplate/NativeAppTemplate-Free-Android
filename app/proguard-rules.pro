# Fix for Retrofit issue https://github.com/square/retrofit/issues/3751
# Keep the generic signature of Call (R8 full mode strips signatures from non-kept items).
# Retrofit's own rules already keep Response and kotlin.coroutines.Continuation.
-keep,allowobfuscation,allowshrinking interface retrofit2.Call

# Keep DataStore fields
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite* {
   <fields>;
}

-keep @kotlinx.parcelize.Parcelize public class *
