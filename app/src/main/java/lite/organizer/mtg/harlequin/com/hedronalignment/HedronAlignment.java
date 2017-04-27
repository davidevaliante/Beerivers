package lite.organizer.mtg.harlequin.com.hedronalignment;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


public class HedronAlignment extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //libreria per i font personalizzati
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("src/main/fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //picasso caching
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
