package fr.klemek.autologin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class Extractor {

    private static URI getJarURI()
            throws URISyntaxException {
        final ProtectionDomain domain;
        final CodeSource source;
        final URL url;
        final URI uri;

        domain = Extractor.class.getProtectionDomain();
        source = domain.getCodeSource();
        url = source.getLocation();
        uri = url.toURI();

        return (uri);
    }

    static URI getFile(final String fileName) throws IOException, URISyntaxException {
        final File location;
        final URI fileURI;

        URI where = getJarURI();

        location = new File(where);

        // not in a JAR, just return the path on disk
        if (location.isDirectory()) {
            fileURI = URI.create(where.toString() + fileName);
        } else {
            final ZipFile zipFile;

            zipFile = new ZipFile(location);

            try {
                fileURI = extract(zipFile, fileName);
            } finally {
                zipFile.close();
            }
        }

        return (fileURI);
    }

    private static URI extract(final ZipFile zipFile,
                               final String fileName)
            throws IOException {
        final File tempFile;
        final ZipEntry entry;

        tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
        tempFile.deleteOnExit();
        entry = zipFile.getEntry(fileName);

        if (entry == null) {
            throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
        }

        try (InputStream zipStream = zipFile.getInputStream(entry)) {
            try (OutputStream fileStream = new FileOutputStream(tempFile)) {
                final byte[] buf;
                int i;
                buf = new byte[1024];
                i = 0;
                while ((i = zipStream.read(buf)) != -1) {
                    fileStream.write(buf, 0, i);
                }
            }
        }

        return (tempFile.toURI());
    }
}
