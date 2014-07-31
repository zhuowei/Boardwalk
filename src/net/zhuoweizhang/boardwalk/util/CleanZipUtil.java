package net.zhuoweizhang.boardwalk.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class CleanZipUtil {
    public static void process(final File inputFile, final File outputFile) throws IOException
    {
        final byte[] buf = new byte[0x2000];

        final ZipFile inputZip = new ZipFile(inputFile);
        final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(outputFile));
        try
        {
            // read a the entries of the input zip file and sort them
            final Enumeration<? extends ZipEntry> e = inputZip.entries();
            final ArrayList<ZipEntry> sortedList = new ArrayList<ZipEntry>();
            while (e.hasMoreElements()) {
                final ZipEntry entry = e.nextElement();
                if (entry.getName().startsWith("META-INF")) continue;
                sortedList.add(entry);
            }

            Collections.sort(sortedList, new Comparator<ZipEntry>()
            {
                public int compare(ZipEntry o1, ZipEntry o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            // treat them again and write them in output, wenn they not are empty directories
            for (int i = sortedList.size()-1; i>=0; i--)
            {
                final ZipEntry inputEntry = sortedList.get(i);
                final String name = inputEntry.getName();
                final boolean isEmptyDirectory;
                if (inputEntry.isDirectory())
                {
                    if (i == sortedList.size()-1)
                    {
                        // no item afterwards; it was an empty directory
                        isEmptyDirectory = true;
                    }
                    else
                    {
                        final String nextName = sortedList.get(i+1).getName();
                        isEmptyDirectory  = !nextName.startsWith(name);
                    }
                }
                else
                {
                    isEmptyDirectory = false;
                }


                // write the entry
                if (isEmptyDirectory)
                {
                    sortedList.remove(inputEntry);
                }
                else
                {
                    final ZipEntry outputEntry = new ZipEntry(inputEntry);
                    outputStream.putNextEntry(outputEntry);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final InputStream is = inputZip.getInputStream(inputEntry);
                    IoUtil.pipe(is, baos, buf);
                    is.close();
                    outputStream.write(baos.toByteArray());
                }
            }
        } finally {
            outputStream.close();
        }

    }
}
