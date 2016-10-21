package com.example.user.schatzkarteapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MBTilesFileArchive;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;


import java.io.File;
import java.util.List;

public class MainActivity extends Activity implements LocationListener {
    private static final String DEBUG_TAG = "mydebug";
    private LocationManager locationManager;
    private String provider;
    private Exception e;
    private List<Overlay> mapOverlays;
    private MapView mapViewMap;






    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //important! set your user agent to prevent getting banned from the osm servers
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);

        IMapController controller = map.getController();
        controller.setZoom(18);

// Die TileSource beschreibt die Eigenschaften der Kacheln die wir anzeigen
        XYTileSource treasureMapTileSource = new XYTileSource("mbtiles", 1, 20, 256, ".png",new String[] {"http://example.org/"});
try {
    File file = new File(Environment.getExternalStorageDirectory() /* entspricht /sdcard/ */, "hsr.mbtiles");

    Log.d("fd", file.getAbsolutePath());
/* Das verwenden von mbtiles ist leider ein wenig aufwändig, wir müssen
 * unsere XYTileSource in verschiedene Klassen 'verpacken' um sie dann
 * als TilesOverlay über der Grundkarte anzuzeigen.
 */
    if (file != null) {
        MapTileModuleProviderBase treasureMapModuleProvider = new MapTileFileArchiveProvider(new SimpleRegisterReceiver(this),
                treasureMapTileSource, new IArchiveFile[]{MBTilesFileArchive.getDatabaseFileArchive(file)});

        MapTileProviderBase treasureMapProvider = new MapTileProviderArray(treasureMapTileSource, null,
                new MapTileModuleProviderBase[]{treasureMapModuleProvider});

        TilesOverlay treasureMapTilesOverlay = new TilesOverlay(treasureMapProvider, getBaseContext());
        treasureMapTilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);

// Jetzt können wir den Overlay zu unserer Karte hinzufügen:
        map.getOverlays().add(treasureMapTilesOverlay);
    }}

     catch (SQLiteCantOpenDatabaseException e){
        Log.d(DEBUG_TAG, "SQLiteCantOpenDatabaseException thrown!");
        alertHsrMbtiles();
    }

        mapOverlays = mapViewMap.getOverlays();

       Log.d("fisdfsd", "i'm here");

    }



    private void alertHsrMbtiles() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String text = getResources().getString(R.string.no_hsr_mbtiles);
        builder.setMessage(text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                // finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}