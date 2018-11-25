package eu.hywse.libv1_9.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WseFileUtil {

    private static WseFileUtil instance;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public static WseFileUtil getInstance() {
        if (instance == null) {
            instance = new WseFileUtil();
        }

        return instance;
    }

    /*
     * Download File Async Constructors
     */
    public void downloadFileAsync(URL url, DownloadResult result) {
        downloadFileAsync(url.toString(), result);
    }

    public void downloadFileAsync(URL url, String out, DownloadResult result) {
        downloadFileAsync(url.toString(), out, result);
    }

    public void downloadFileAsync(String urlStr, DownloadResult result) {
        executor.execute(() -> downloadFile(urlStr, result));
    }

    public void downloadFileAsync(String urlStr, String out, DownloadResult result) {
        executor.execute(() -> downloadFile(urlStr, out, result));
    }

    /*
     * Download File Functions
     */

    public void downloadFile(URL url, DownloadResult result) {
        downloadFile(url.toString(), result);
    }

    public void downloadFile(String urlStr, DownloadResult result) {
        String out = urlStr;
        if (out.contains("/")) {
            out = out.split("/")[out.split("/").length - 1];
        }
        downloadFile(urlStr, out, result);
    }

    public void downloadFile(URL url, String out, DownloadResult result) {
        downloadFile(url.toString(), out, result);
    }

    public void downloadFile(String urlStr, String out, DownloadResult result) {
        FileDownloadInfo info = new FileDownloadInfo();
        info.startedAt = (System.currentTimeMillis());

        // Basic
        info.urlStr = (urlStr);
        info.outStr = (out);

        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            result.onError(e.getMessage(), info);
            return;
        }
        info.url = (url);

        try (BufferedInputStream in = new BufferedInputStream(url.openStream()); FileOutputStream fos = new FileOutputStream(out)) {

            info.bytesRead = write(in, fos);

            File file = new File(out);
            info.file = (file);

            result.onSuccess(file, info);
        } catch (IOException e) {
            result.onError(e.getMessage(), info);
        }
    }


    public void downloadFileWithAuthAsync(URL url, String user, String pass, DownloadResult result) {
        downloadFileWithAuthAsync(url.toString(), user, pass, result);
    }

    public void downloadFileWithAuthAsync(URL url, String out, String user, String pass, DownloadResult result) {
        downloadFileWithAuthAsync(url.toString(), out, user, pass, result);
    }

    public void downloadFileWithAuthAsync(String urlStr, String user, String pass, DownloadResult result) {
        executor.execute(() -> downloadFileWithAuth(urlStr, user, pass, result));
    }

    public void downloadFileWithAuthAsync(String url, String out, String user, String pass, DownloadResult result) {
        executor.execute(() -> downloadFileWithAuth(url, out, user, pass, result));
    }

    public void downloadFileWithAuth(String urlStr, String user, String pass, DownloadResult result) {
        String out = urlStr;
        if (out.contains("/")) {
            out = out.split("/")[out.split("/").length - 1];
        }

        downloadFileWithAuth(urlStr, out, user, pass, result);
    }

    public void downloadFileWithAuth(String urlStr, String out, String user, String pass, DownloadResult result) {
        FileDownloadInfo info = new FileDownloadInfo();
        info.startedAt = (System.currentTimeMillis());

        // Basic
        info.urlStr = (urlStr);
        info.outStr = (out);

        String userNameAndColon = user + ":" + pass;
        String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(userNameAndColon.getBytes());

        File file = new File(out);

        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        /*
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }

        BufferedInputStream in = null;
        FileOutputStream fos = null;
        */

        try {
            URL serverUrl = new URL(urlStr);
            info.url = serverUrl;

            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Authorization", basicAuthPayload);

            Files.copy(urlConnection.getInputStream(), Paths.get(file.getParentFile().getPath(), file.getName()), StandardCopyOption.REPLACE_EXISTING);

            /*
            in = new BufferedInputStream(urlConnection.getInputStream());
            fos = new FileOutputStream(file);

            info.bytesRead = write(in, fos);
            */

            info.file = (file);

            result.onSuccess(file, info);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            result.onError(ioe.getMessage(), info);
        } /*finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }*/
    }

    public int write(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[512]; // optimize the size of buffer to your need
        int num;
        while ((num = is.read(buf)) != -1) {
            os.write(buf, 0, num);
        }
        return num;
    }

    public interface DownloadResult {
        void onSuccess(File file, FileDownloadInfo info);

        void onError(String message, FileDownloadInfo info);
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class FileDownloadInfo {
        private String urlStr, outStr, username, password;
        private URL url;
        private File file;
        private long startedAt;
        private int bytesRead;

        public long getDuration() {
            return System.currentTimeMillis() - startedAt;
        }

        public boolean fileExists() {
            return file.exists();
        }
    }

}