package com.example.multicamstreamer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HostActivity extends AppCompatActivity {

    private static final String TAG = "HostActivity";
    private static final int SERVER_PORT = 8080;

    private ServerSocket serverSocket;
    private ExecutorService serverExecutor;
    private Handler mainHandler;

    private ImageView imageView;  // To show received frame

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        imageView = findViewById(R.id.imageView);
        mainHandler = new Handler(Looper.getMainLooper());
        serverExecutor = Executors.newCachedThreadPool(); // Handles multiple clients

        startServer();
    }

    private void startServer() {
        serverExecutor.execute(() -> {
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                runOnUiThread(() -> Toast.makeText(this, "Server started!", Toast.LENGTH_SHORT).show());

                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    Log.d(TAG, "Client connected: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleClient(Socket clientSocket) {
        serverExecutor.execute(() -> {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                while (!clientSocket.isClosed()) {
                    // First, read the 4-byte size header
                    byte[] lengthBytes = inputStream.readNBytes(4);
                    if (lengthBytes.length < 4) break;

                    int length = byteArrayToInt(lengthBytes);

                    // Then, read the JPEG image data
                    byte[] imageBytes = inputStream.readNBytes(length);
                    if (imageBytes.length < length) break;

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    updateImage(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateImage(Bitmap bitmap) {
        mainHandler.post(() -> {
            imageView.setImageBitmap(bitmap);
        });
    }

    private static int byteArrayToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (serverSocket != null) serverSocket.close();
            if (serverExecutor != null) serverExecutor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
