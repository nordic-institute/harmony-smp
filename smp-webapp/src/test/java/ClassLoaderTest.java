/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class ClassLoaderTest {

    static int loadableClassesCnt = 0;
    static int notLoadableClassesCnt = 0;

    public static void main(String [] args) {
        try {
            List classList = null;
            String jarPath = "E:\\src\\smp\\smp-webapp\\target\\cipa-smp-full-webapp-4.0.0-SNAPSHOT\\WEB-INF\\lib\\spring-webmvc-5.0.2.RELEASE.jar";
            classList = getClassesFromJARFile (jarPath);
        }
        catch(Exception e) {
            System.out.println("Exception = " + e);
        }
        System.out.println("Loadable / not loadable classes: "+loadableClassesCnt + " / "+notLoadableClassesCnt);
    }

    private static List getClassesFromJARFile(String jar)
    {
        final List classes = new ArrayList();
        JarInputStream jarFile = null;
        try
        {
            jarFile = new JarInputStream(new FileInputStream(jar));
            JarEntry jarEntry = null;
            do
            {
                try
                {
                    jarEntry = jarFile.getNextJarEntry();
                }
                catch(Exception ioe)
                {
                    System.out.println("Unable to get next jar entry from jar file '"+jar+"'");
                    ioe.printStackTrace();
                }
                if (jarEntry != null)
                {
                    extractClassFromJar(jar, classes, jarEntry);
                }
            } while (jarEntry != null);
            closeJarFile(jarFile);
        }
        catch(Exception ioe)
        {
            System.out.println("Unable to get Jar input stream from '"+jar+"'");
            ioe.printStackTrace();
        }
        finally
        {
            closeJarFile(jarFile);
        }
        return classes;
    }

    private static void extractClassFromJar(final String jar, final List classes, JarEntry jarEntry) throws IOException
    {
        String className = jarEntry.getName();
        if (className.endsWith(".class"))
        {
            className = className.substring(0, className.length() - ".class".length());
            try
            {
                classes.add(Class.forName(className.replace('/', '.')));
                loadableClassesCnt++;
                System.out.println("Successfully loaded class " + className);
            } catch (ClassNotFoundException | NoClassDefFoundError ncdfe)
            {
                notLoadableClassesCnt++;

                System.err.println("Failed to load class " + className + "       reason: "+ncdfe.getMessage());
                //cnfe.printStackTrace();
                //throw new IOException("unable to find class named " + className.replace('/', '.') + "' within jar '" + jar + "'", cnfe);
            }
        }
    }

    private static void closeJarFile(final JarInputStream jarFile)
    {
        if(jarFile != null)
        {
            try
            {
                jarFile.close();
            }
            catch(Exception ioe)
            {
                System.out.println("Unable to Close Jar File '"+jarFile+"'");
                ioe.printStackTrace();
            }
        }
    }

} 