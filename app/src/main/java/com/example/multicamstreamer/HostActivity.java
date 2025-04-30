package com.example.multicamstreamer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HostActivity extends AppCompatActivity {

    private static final int SERVER_PORT = 8080;

    private ServerSocket serverSocket;
    private ExecutorService serverExecutor;
    private Handler mainHandler;

    private RecyclerView recyclerView;
    private ClientFeedAdapter adapter;
    private final ArrayList<Bitmap> clientFeeds = new ArrayList<>();
    private final ArrayList<Integer> clientIndexes = new ArrayList<>(); // Maps socket order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        mainHandler = new Handler(Looper.getMainLooper());
        serverExecutor = Executors.newCachedThreadPool();

        recyclerView = findViewById(R.id.clientRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        adapter = new ClientFeedAdapter(clientFeeds);
        recyclerView.setAdapter(adapter);

        startServer();
    }

    private void startServer() {
        serverExecutor.execute(() -> {
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                runOnUiThread(() -> Toast.makeText(this, "Server started!", Toast.LENGTH_SHORT).show());

                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    Log.d("HostActivity", "Client connected: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleClient(Socket clientSocket) {
        int clientIndex = adapter.addClient(Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888));

        serverExecutor.execute(() -> {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                while (!clientSocket.isClosed()) {
                    byte[] lengthBytes = inputStream.readNBytes(4);
                    if (lengthBytes.length < 4) break;

                    int length = byteArrayToInt(lengthBytes);
                    byte[] imageBytes = inputStream.readNBytes(length);
                    if (imageBytes.length < length) break;

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    updateClientFeed(clientIndex, bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateClientFeed(int index, Bitmap bitmap) {
        mainHandler.post(() -> adapter.updateFeed(index, bitmap));
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
