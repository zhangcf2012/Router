package com.chenenyu.router.matcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.chenenyu.router.RouteOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Support for <strong>implicit intent</strong> exclude scheme "http(s)",
 * cause we may want to resolve them in custom matcher, such as {@link SchemeMatcher},
 * otherwise the {@link BrowserMatcher} will bring up the rear.
 * <p>
 * Created by Cheney on 2017/01/08.
 */
public class ImplicitMatcher extends Matcher {
    public ImplicitMatcher(int priority) {
        super(priority);
    }

    @Override
    public boolean match(Context context, Uri uri, @Nullable String route, RouteOptions routeOptions) {
        if (uri.toString().toLowerCase().startsWith("http://")
                || uri.toString().toLowerCase().startsWith("https://")) {
            return false;
        }
        if (context.getPackageManager().resolveActivity(
                new Intent(Intent.ACTION_VIEW, uri), PackageManager.MATCH_DEFAULT_ONLY) != null) {
            if (uri.getQuery() != null) {
                Map<String, String> map = new HashMap<>();
                parseParams(map, uri.getQuery());
                if (!map.isEmpty()) {
                    Bundle bundle = routeOptions.getBundle();
                    if (bundle == null) {
                        bundle = new Bundle();
                        routeOptions.setBundle(bundle);
                    }
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        bundle.putString(entry.getKey(), entry.getValue());
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Intent onMatched(Context context, Uri uri, @Nullable Class<? extends Activity> target) {
        return new Intent(Intent.ACTION_VIEW, uri);
    }

}
