package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.NearbyPartner

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OsmMapView(
  centerLat: Double,
  centerLon: Double,
  partners: List<NearbyPartner>,
  selectedPartner: NearbyPartner? = null,
  modifier: Modifier = Modifier
) {
  val markersJs = remember(partners, selectedPartner) {
    buildString {
      append("[")
      partners.forEachIndexed { index, p ->
        val safeName = p.name.replace("'", "\\'")
        val safeAddr = p.address.replace("'", "\\'")
        val safeCat = p.category.replace("'", "\\'")
        val safeAvail = p.donationAvailability.replace("'", "\\'")
        val isSel = selectedPartner?.id == p.id
        append("{")
        append("lat: ${p.latitude}, lon: ${p.longitude}, name: '$safeName', address: '$safeAddr', category: '$safeCat', distance: '${p.distanceKm} km', availability: '$safeAvail', selected: $isSel")
        append("}")
        if (index < partners.size - 1) append(",")
      }
      append("]")
    }
  }

  val focusLat = selectedPartner?.latitude ?: centerLat
  val focusLon = selectedPartner?.longitude ?: centerLon
  val zoomLevel = if (selectedPartner != null) 15 else 13

  val htmlContent = remember(markersJs, focusLat, focusLon, zoomLevel) {
    """
    <!DOCTYPE html>
    <html>
    <head>
      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
      <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
      <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
      <style>
        html, body, #map { height: 100%; width: 100%; margin: 0; padding: 0; background-color: #f7fbf7; font-family: sans-serif; }
        .partner-popup h4 { margin: 0 0 4px 0; color: #1B5E20; font-size: 14px; }
        .partner-popup p { margin: 2px 0; font-size: 12px; color: #333; }
        .badge { display: inline-block; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: bold; background: #D4ECD5; color: #002204; margin-bottom: 4px; }
      </style>
    </head>
    <body>
      <div id="map"></div>
      <script>
        var map = L.map('map').setView([$focusLat, $focusLon], $zoomLevel);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          maxZoom: 19,
          attribution: '&copy; OpenStreetMap'
        }).addTo(map);

        // User center marker
        var userIcon = L.divIcon({
          className: 'user-pin',
          html: '<div style="background-color:#1B5E20; width:16px; height:16px; border-radius:50%; border:3px solid white; box-shadow:0 0 6px rgba(0,0,0,0.4);"></div>',
          iconSize: [22, 22],
          iconAnchor: [11, 11]
        });
        L.marker([$centerLat, $centerLon], {icon: userIcon}).addTo(map)
          .bindPopup('<b>Your Location</b>');

        var partners = $markersJs;
        partners.forEach(function(p) {
          var color = p.category === 'Restaurant' ? '#E65100' :
                      p.category === 'Bakery' ? '#8D6E63' :
                      p.category === 'NGO' ? '#2E7D32' : '#00897B';
          
          var pinIcon = L.divIcon({
            className: 'partner-marker',
            html: '<div style="background-color:' + color + '; width:22px; height:22px; border-radius:50% 50% 50% 0; transform:rotate(-45deg); border:2px solid white; box-shadow:0 2px 5px rgba(0,0,0,0.3); display:flex; align-items:center; justify-content:center;">' +
                  '<div style="width:6px; height:6px; background:white; border-radius:50%;"></div></div>',
            iconSize: [26, 26],
            iconAnchor: [13, 26],
            popupAnchor: [0, -26]
          });

          var marker = L.marker([p.lat, p.lon], {icon: pinIcon}).addTo(map);
          var content = '<div class="partner-popup">' +
            '<span class="badge">' + p.category + ' • ' + p.distance + '</span>' +
            '<h4>' + p.name + '</h4>' +
            '<p><b>Address:</b> ' + p.address + '</p>' +
            '<p><b>Status:</b> ' + p.availability + '</p>' +
            '</div>';
          marker.bindPopup(content);
          if (p.selected) {
            marker.openPopup();
          }
        });
      </script>
    </body>
    </html>
    """.trimIndent()
  }

  AndroidView(
    modifier = modifier
      .fillMaxSize()
      .clip(RoundedCornerShape(16.dp)),
    factory = { ctx ->
      WebView(ctx).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        webViewClient = WebViewClient()
        loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
      }
    },
    update = { webView ->
      webView.loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
    }
  )
}
