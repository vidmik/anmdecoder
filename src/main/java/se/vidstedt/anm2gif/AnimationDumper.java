package se.vidstedt.anm2gif;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class AnimationDumper {
    private final Animation animation;

    public AnimationDumper(Animation animation) {
        this.animation = animation;
    }

    void dump(File outDirectory) throws IOException {
        byte[] pixels = new byte[animation.getHeader().getWidth() * animation.getHeader().getHeight()];
        for (int i = 0; i < animation.getRecords().length; i++) {
            Record record = animation.getRecord(i);
            if (record != null) {
                record.decode(pixels);
            }
            Files.createDirectories(outDirectory.toPath());
            File file = new File(outDirectory, String.format("record%03d.ppm", i));
            dumpFrame(file, pixels);
        }
    }

    void dumpGif(File gif) throws IOException {
        File dir = gif.getParentFile();
        if (!dir.exists()) {
            Files.createDirectories(dir.toPath());
        }

        new GifWriter(animation, gif).write();
    }

    private void dumpFrame(File file, byte[] pixels) throws IOException {
        byte[] ppmData = new byte[pixels.length * 3];
        for (int i = 0; i < pixels.length; i++) {
            byte[] colors = animation.getPalette().getPaletteColorComponents(pixels[i]);
            ppmData[i * 3] = colors[0];
            ppmData[i * 3 + 1] = colors[1];
            ppmData[i * 3 + 2] = colors[2];
        }
        new PpmWriter(animation.getHeader().getWidth(), animation.getHeader().getHeight(), file, ppmData).write();
    }
}
