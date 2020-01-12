package com.mrivanplays.twitch.api;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class FileRequestBody extends RequestBody {

    private RequestParams requestParams;

    public FileRequestBody(RequestParams requestParams) {
        this.requestParams = requestParams;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("multipart/form-data");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        String EOL = "\r\n";
        String boundary = "===" + System.currentTimeMillis() + "===";
        try (OutputStream out = sink.outputStream()) {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out))) {
                for (Map.Entry<String, String> param : requestParams.stringEntrySet()) {
                    writer.append("--").append(boundary).append(EOL);
                    writer.append("Content-Disposition: form-data; name=\"").append(param.getKey()).append("\"").append(EOL);
                    writer.append("Content-Type: text/plain; charset=").append(requestParams.getCharset().name()).append(EOL);
                    writer.append(EOL).append(param.getValue()).append(EOL);
                    writer.flush();
                }
                for (Map.Entry<String, File> param : requestParams.fileEntrySet()) {
                    String fileName = param.getValue().getName();
                    writer.append("--").append(boundary).append(EOL);
                    writer.append("Content-Disposition: form-data; name=\"").append(param.getKey()).append("\"; filename=\"")
                            .append(fileName).append("\"").append(EOL);
                    writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(EOL);
                    writer.append("Content-Transfer-Encoding: binary").append(EOL);
                    writer.append(EOL);
                    writer.flush();
                    // Send file
                    Files.copy(param.getValue().toPath(), out);
                    out.flush();
                    writer.append(EOL);
                    writer.flush();
                }

                writer.append(EOL).flush();
                writer.append("--").append(boundary).append("--").append(EOL);
            }
        }
    }
}
