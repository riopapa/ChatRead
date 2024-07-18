package biz.riopapa.chatread.func;

import static biz.riopapa.chatread.MainActivity.downloadFolder;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.packageDirectory;
import static biz.riopapa.chatread.MainActivity.tableFolder;
import static biz.riopapa.chatread.MainActivity.toDay;
import static biz.riopapa.chatread.MainActivity.todayFolder;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import biz.riopapa.chatread.common.Utils;

public class FileIO {

    public void readyFolders() {
        if (packageDirectory == null)
            packageDirectory = new File(Environment.getExternalStorageDirectory(), "_ChatTalkLog");

        if (!packageDirectory.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                packageDirectory.mkdirs();
            } catch (Exception e) {
                Log.e("Exception", "Package Folder "+ packageDirectory.toString() + "_" + e);
            }
        }
        downloadFolder = new File(Environment.getExternalStorageDirectory(), "download");
        tableFolder = new File(downloadFolder, "_ChatTalk");
    }

    public void append2Today(String filename, String textLine) {
        if (todayFolder == null) {
            todayFolder = new File(packageDirectory, toDay);
        }
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        File file = new File(todayFolder, filename);
        String timeInfo = timeFormat.format(new Date()) + " ";
        append2File(file, timeInfo, textLine);
    }

    public void append2File(File file, String timeInfo, String textLine) {
        new ReadyToday();
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    String s = "create file Error " + file;
                    Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                    Log.e("file " + file, s);
                }
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write("\n" + timeInfo + textLine + "\n");
        } catch (IOException e) {
            Log.e("append2File", "catch IOException "+e);
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException e) {
                Log.e("append2File", "finally IOException "+e);
            }
        }
    }

    public void writeFile(File targetFolder, String fileName, String outText) {
        try {
            File targetFile = new File(targetFolder, fileName);
            FileWriter fileWriter = new FileWriter(targetFile, false);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(outText);
            bufferedWriter.close();
        } catch (IOException ex) {
            new Utils().logE("editor", fileName + "'\n" + ex);
            Toast.makeText(mContext, "writeTextFile" + fileName, Toast.LENGTH_LONG).show();
        }
    }

    public String readFile(File targetFolder, String fileName) {
        File jFile = new File(targetFolder, fileName);
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(jFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
            }
            in.close();
        } catch (IOException e) {
            Log.e("readFile","IOException "+e);
        }
        return sb.toString();
    }

    public String[] readKR(String filename) {
        final int BUFFER_SIZE = 81920;
        final String code = "EUC-KR";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filename)), code), BUFFER_SIZE);
        } catch (Exception e) {
            Log.e("readKR","Exception "+e);
        }

        List<String> lines = new ArrayList<>();
        String line;
        while (true) {
            try {
                if ((line = bufferedReader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            lines.add(line);
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines.toArray(new String[0]);
    }

//    public static void writeKR(File file, String textLine) {
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            OutputStreamWriter OutputStreamWriter =
//                    new OutputStreamWriter(fileOutputStream, "EUC-KR");
//            BufferedWriter bufferedWriter = new BufferedWriter(OutputStreamWriter);
//            bufferedWriter.write(textLine);
//            bufferedWriter.close();
//        } catch (Exception e) {
//            Log.e("writeKR","IOException "+e);
//        }
//    }
}