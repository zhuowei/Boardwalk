package net.zhuoweizhang.boardwalk.util;

/**
 * This code is based on JarJar: copyright shown below.
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
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
    public static List<File> shardZip(File from, File tmpDir, int numClasses) throws IOException {
        byte[] buf = new byte[0x2000];
        List<File> files = new ArrayList<File>();
        ZipInputStream zis = new ZipInputStream(new FileInputStream(from));
        File tmpTo = null;
        ZipOutputStream out = null;
        int currentCount = 0;
        try {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().startsWith("META-INF") || entry.isDirectory()) continue;
                if (tmpTo == null || currentCount == numClasses) {
                    if (out != null) out.close();
                    tmpTo = File.createTempFile(from.getName(), ".jar", tmpDir);
                    files.add(tmpTo);
                    out = new ZipOutputStream(new FileOutputStream(tmpTo));
                    currentCount = 0;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                entry.setCompressedSize(-1);
                out.putNextEntry(entry);
                IoUtil.pipe(zis, out, buf);
                if (entry.getName().endsWith(".class")) currentCount++;
            }

        }
        finally {
            //in.close();
            zis.close();
            out.close();
        }
        return files;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(shardZip(new File(args[0]), new File(args[1]), Integer.parseInt(args[2])));
    }
}
