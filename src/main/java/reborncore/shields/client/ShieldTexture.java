package reborncore.shields.client;

import net.minecraft.client.renderer.texture.AbstractTexture;
import reborncore.client.texture.InputStreamTexture;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by modmuss50 on 23/05/2016.
 */
public class ShieldTexture {

    DownloadState state = DownloadState.AVAILABLE;

    @Nullable
    AbstractTexture texture;

    String url;

    public ShieldTexture(String url) {
        this.url = url;
    }

    public void download(){
        if(state != DownloadState.AVAILABLE){
            return;
        }
        state = DownloadState.DOWNLOADING;

        new Thread(() ->
        {
            try {
                InputStream inputStream =  new URL(url).openStream();
                texture = new InputStreamTexture(inputStream, url);
                state = DownloadState.DOWNLOADED;
            } catch (IOException e) {
                e.printStackTrace();
                state = DownloadState.FAILED;
            }
        }).start();
    }

    public DownloadState getState() {
        return state;
    }

    @Nullable
    public AbstractTexture getTexture() {
        return texture;
    }
}
