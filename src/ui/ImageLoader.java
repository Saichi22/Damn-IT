package ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

/*
 * Loads product pictures from their web link in the background,
 * so the window never freezes. Each picture is:
 *   1) kept in memory after the first load,
 *   2) saved to images/products/<id>.jpg, so the next run works offline.
 * Tip: run the program once with internet before presenting.
 */
public final class ImageLoader {
    private static final ConcurrentHashMap<String, BufferedImage> MEMORY = new ConcurrentHashMap<>();
    private static final Set<String> FAILED = ConcurrentHashMap.newKeySet();
    private static final File CACHE_DIR = new File("images/products");

    private ImageLoader() { }

    /* onLoaded runs on the Swing thread once the picture is ready. */
    public static void load(String id, String url, Consumer<BufferedImage> onLoaded) {
        BufferedImage cached = MEMORY.get(id);
        if (cached != null) {
            onLoaded.accept(cached);
            return;
        }
        if (FAILED.contains(id)) return;

        new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                File file = new File(CACHE_DIR, id + ".jpg");
                if (file.exists()) {
                    BufferedImage fromDisk = ImageIO.read(file);
                    if (fromDisk != null) return fromDisk;
                }
                HttpURLConnection con = (HttpURLConnection) URI.create(url).toURL().openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setConnectTimeout(8000);
                con.setReadTimeout(8000);
                try (InputStream in = con.getInputStream()) {
                    BufferedImage img = ImageIO.read(in);
                    if (img != null) {
                        try {
                            CACHE_DIR.mkdirs();
                            if (!ImageIO.write(img, "jpg", file)) file.delete();
                        } catch (Exception ignored) {
                            // saving is optional
                        }
                    }
                    return img;
                }
            }

            @Override
            protected void done() {
                try {
                    BufferedImage img = get();
                    if (img != null) {
                        MEMORY.put(id, img);
                        onLoaded.accept(img);
                    } else {
                        FAILED.add(id);
                    }
                } catch (Exception ex) {
                    FAILED.add(id);   // no internet or bad link: card keeps its placeholder
                }
            }
        }.execute();
    }
}