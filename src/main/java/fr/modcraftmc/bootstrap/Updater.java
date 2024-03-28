package fr.modcraftmc.bootstrap;

import fr.modcraftmc.api.ModcraftApiClient;
import fr.modcraftmc.api.ModcraftApiRequestsExecutor;
import fr.modcraftmc.api.exception.ParsingException;
import fr.modcraftmc.api.exception.RemoteException;
import fr.modcraftmc.api.models.LauncherInfo;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater {
    public static char FP = File.separatorChar;
    public static boolean windows = System.getProperty("os.name").toLowerCase().contains("windows");
    public static String BASE_PATH = windows ? System.getenv("appdata") : System.getenv("HOME");
    public static File DEFAULT_PATH = new File(BASE_PATH + FP + ".modcraftmc" + FP);
    public static File LAUNCHER_PATH = new File(DEFAULT_PATH, "launcher");
    public static File LAUNCHER_JAR = new File(LAUNCHER_PATH, "launcher.jar");
    public static File JAVA_PATH = new File(DEFAULT_PATH, "java");
    public static File JAVA_ZIP = new File(JAVA_PATH, "java.zip");
    public static File JAVA_EXE = new File(JAVA_PATH, "bin/java" + (windows ? ".exe" : ""));
    public static File LOGS_PATH = new File(LAUNCHER_PATH, "logs");

    private final ModcraftApiClient apiClient = new ModcraftApiClient("https://api.modcraftmc.fr/v1/");

    static {
        try {
            if (!DEFAULT_PATH.exists()) {
                DEFAULT_PATH.mkdirs();
            }
            if (!LAUNCHER_PATH.exists()) {
                LAUNCHER_PATH.mkdirs();
            }
            if (!JAVA_PATH.exists()) {
                JAVA_PATH.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {;
        LauncherInfo info = getLauncherInfo();

        if (!JAVA_EXE.exists()) {
            downloadJava();
            extractJava();
        }

        if (!LAUNCHER_JAR.exists()) {
            downloadProgess(info);
            launchJar();
        } else {
            if (!checkJar(info)) {
                downloadProgess(info);
            }
            launchJar();
        }
    }

    public LauncherInfo getLauncherInfo() {
        try {
            return apiClient.executeRequest(ModcraftApiRequestsExecutor.getLauncherInfo());
        } catch (ParsingException | IOException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void extractJava() {
        ModcraftBootstrap.LOGGER.info("extracting java");
        ModcraftBootstrap.getBootstrapFrame().getBootstrapPanel().updateTopText("extraction de java");
        ModcraftBootstrap.getBootstrapFrame().getBootstrapPanel().updateBottomText("");
        try {
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(JAVA_ZIP)));
            ZipEntry ze = null;
            while ((ze = zis.getNextEntry()) != null) {
                File f = new File(JAVA_PATH, ze.getName());
                if (ze.isDirectory()) {
                    f.mkdirs();
                    continue;
                }
                f.getParentFile().mkdirs();
                OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
                try {
                    try {
                        final byte[] buf = new byte[1024];
                        int bytesRead;
                        long nread = 0L;
                        long length = JAVA_ZIP.length();

                        while (-1 != (bytesRead = zis.read(buf))) {
                            fos.write(buf, 0, bytesRead);
                            nread += bytesRead;
                        }
                    } finally {
                        fos.close();
                    }
                } catch (final IOException ioe) {
                    f.delete();
                }
            }
            zis.close();
            JAVA_ZIP.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkJar(LauncherInfo launcherInfo) {
        ModcraftBootstrap.LOGGER.info("checking jar");

        try {
            MessageDigest md5Digest = MessageDigest.getInstance("SHA1");
            //Get the checksum
            String checksum = getFileChecksum(md5Digest, LAUNCHER_JAR);

            return checksum.equals(launcherInfo.sha1());


        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public void downloadProgess(LauncherInfo info) {
        ModcraftBootstrap.getBootstrapFrame().getBootstrapPanel().updateTopText("Mise à jour du launcher");
        //ModcraftBootstrap.getBootstrapFrame().setProgressVisible();
        download(info.downloadUrl(), LAUNCHER_JAR.getPath());
    }

    public static void download(String remotePath, String localPath) {
        BufferedInputStream in = null;
        FileOutputStream out = null;

        try {
            URL url = new URL(remotePath);
            URLConnection conn = catchForbidden1(url);
            int size = conn.getContentLength();

            if (size < 0) {
                System.out.println("Could not get the file size");
            } else {
                System.out.println("File size: " + size);
            }
            int mbFile = size / (1024 * 1024);

            in = new BufferedInputStream(catchForbidden(url));
            out = new FileOutputStream(localPath);
            byte data[] = new byte[1024];
            int count;
            double sumCount = 0.0;

            while ((count = in.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);

                sumCount += count;
                if (size > 0) {
                    double finalSumCount = sumCount;
                    long finalSumCountLong = (long) finalSumCount;
                    SwingUtilities.invokeLater(() -> {
                        int percent = (int) (finalSumCount / size * 100.0);
                        ModcraftBootstrap.getBootstrapFrame().getBootstrapPanel().updateBottomText(percent + "% " + "(" + (finalSumCountLong) / (1024 * 1024) + "/" + mbFile + "MB)");
                        //ModcraftBootstrap.getBootstrapFrame().getProgressBar().setValue((int) (finalSumCount / size * 100.0));
                    });
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            if (out != null)
                try {
                    out.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
        }
    }

    public void downloadJava() {
        ModcraftBootstrap.getBootstrapFrame().getBootstrapPanel().updateTopText("téléchargement de java");
        //ModcraftBootstrap.getBootstrapFrame().setProgressVisible();
        download("https://download.modcraftmc.fr/java_" + (windows ? "win" : "linux") + ".zip", JAVA_ZIP.getPath());
    }

    public static InputStream catchForbidden(URL url) throws IOException
    {
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
        connection.setInstanceFollowRedirects(true);
        return connection.getInputStream();
    }

    public static HttpURLConnection catchForbidden1(URL url) throws IOException
    {
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
        connection.setInstanceFollowRedirects(true);
        return connection;
    }

    public void launchJar() {
        try {
            ModcraftBootstrap.LOGGER.info("launching jar");
            JAVA_EXE.setExecutable(true);
            String bootstrapPath = ModcraftBootstrap.getLaunchPath().getPath();
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(LAUNCHER_PATH);
            builder.command(JAVA_PATH.getPath() + "/bin/java", "-DbootstrapPath=" + bootstrapPath, "-jar", "launcher.jar");
            builder.start();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        System.exit(0);

    }

    private String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        fis.close();

        byte[] bytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
