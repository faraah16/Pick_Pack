package com.example.pick_pack.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pick_pack.OrderManager;
import com.example.pick_pack.R;
import com.example.pick_pack.bluetooth.BluetoothService;
import com.google.firebase.firestore.FirebaseFirestore;


public class CommanderRobotActivity extends AppCompatActivity {
    Button btnUp, btnDown, btnLeft, btnRight;

    private BluetoothService bluetoothService;
    private TextView tvStatus, tvMode;

    private String orderId = null;
    private boolean isOrderMode = false;



    private SeekBar[] servos = new SeekBar[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commander_robot);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });



        bluetoothService = new BluetoothService();

        btnUp.setOnClickListener(v -> bluetoothService.sendCommand("MOVE:UP"));
        btnDown.setOnClickListener(v -> bluetoothService.sendCommand("MOVE:DOWN"));
        btnLeft.setOnClickListener(v -> bluetoothService.sendCommand("MOVE:LEFT"));
        btnRight.setOnClickListener(v -> bluetoothService.sendCommand("MOVE:RIGHT"));

        tvStatus = findViewById(R.id.tvStatus);
        tvMode = findViewById(R.id.tvOrderMode);

        orderId = getIntent().getStringExtra("orderId");
        if (orderId != null) {
            isOrderMode = true;
            tvMode.setText("Commande : " + orderId);
        } else {
            tvMode.setText("Mode test");
        }

        servos[0] = findViewById(R.id.servo0);
        servos[1] = findViewById(R.id.servo1);
        servos[2] = findViewById(R.id.servo2);
        servos[3] = findViewById(R.id.servo3);
        servos[4] = findViewById(R.id.servo4);
        servos[5] = findViewById(R.id.servo5);

        for (int i = 0; i < servos.length; i++) {
            final int index = i;
            servos[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    bluetoothService.sendCommand("S" + index + ":" + progress);
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        Button btnConnect = findViewById(R.id.btnConnect);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnFinish = findViewById(R.id.btnFinish);
        Button btn_G_Open = findViewById(R.id.btnGopen);
        Button btn_G_Closed = findViewById(R.id.btnGclose);




        btnConnect.setOnClickListener(v -> {
            boolean ok = bluetoothService.connectToDevice("HC-06");
            tvStatus.setText(ok ? "Connecté" : "Échec connexion");
        });

        // ▶ START
        btnStart.setOnClickListener(v -> {
            bluetoothService.sendCommand("INIT");

            if (!isOrderMode) {
                Toast.makeText(this, "Mode test", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("orders")
                    .document(orderId)
                    .update("status", "Preparing")
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this,
                                    "Commande en préparation",
                                    Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    "Erreur Firestore",
                                    Toast.LENGTH_LONG).show()
                    );

            OrderManager.updateStatus(orderId, "Preparing");
            OrderManager.saveOrdersToPrefs(this);
        });


        btn_G_Open.setOnClickListener(v ->
                bluetoothService.sendCommand("OPEN")
        );
        btn_G_Closed.setOnClickListener(v ->
                bluetoothService.sendCommand("CLOSE")
        );
        btnPlay.setOnClickListener(v ->
                bluetoothService.sendCommand("PLAY")
        );

        btnStop.setOnClickListener(v ->
                bluetoothService.sendCommand("STOP")
        );
        btnUp.setOnClickListener(v ->
                bluetoothService.sendCommand("MOVE:UP")
        );

        btnDown.setOnClickListener(v ->
                bluetoothService.sendCommand("MOVE:DOWN")
        );

        btnLeft.setOnClickListener(v ->
                bluetoothService.sendCommand("MOVE:LEFT")
        );

        btnRight.setOnClickListener(v ->
                bluetoothService.sendCommand("MOVE:RIGHT")
        );


        btnFinish.setOnClickListener(v -> {
            if (!isOrderMode) {
                Toast.makeText(this,
                        "Mode test : aucune commande",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 1️⃣ Firestore
            FirebaseFirestore.getInstance()
                    .collection("orders")
                    .document(orderId)
                    .update("status", "shipped")
                    .addOnSuccessListener(unused -> {

                        // 2️⃣ SUPPRIMER LOCAL (IMPORTANT 🔥)
                        OrderManager.removeOrder(orderId);
                        OrderManager.saveOrdersToPrefs(this);

                        Toast.makeText(this,
                                "Commande livrée",
                                Toast.LENGTH_LONG).show();

                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    "Erreur Firestore",
                                    Toast.LENGTH_LONG).show()
                    );
        });


    }
}
