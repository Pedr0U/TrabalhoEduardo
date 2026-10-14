# ProGuard rules for the Cannon Game app.
# This project does not enable minification by default, so these rules are
# only used if minifyEnabled is switched to true.

# Keep the custom View constructor used by the Android layout inflater.
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
