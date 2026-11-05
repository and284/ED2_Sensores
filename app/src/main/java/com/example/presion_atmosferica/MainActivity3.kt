package com.example.presion_atmosferica

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale
import kotlin.math.abs

class MainActivity3 : AppCompatActivity(), SensorEventListener {

    private lateinit var tvValores: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvTitulo: TextView
    private lateinit var btnVerContadores: Button
    private lateinit var mainu: LinearLayout

    private lateinit var sensorManager: SensorManager
    private var sensorAcelerometro: Sensor? = null

    private var contadorBrusco = 0
    private var contadorNormal = 0
    private var estadoActual: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)

        tvTitulo = findViewById(R.id.tvTitulo)
        tvEstado = findViewById(R.id.tvEstado)
        tvValores = findViewById(R.id.tvValores)
        btnVerContadores = findViewById(R.id.btnVerContadores)
        mainu = findViewById(R.id.mainu)

        // PERMITIR EL USO DE LOS SERVICIOS DE HARDWARE DEL DISPOSITIVO
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // SELECCIONAR EL SENSOR DE ACELERÓMETRO
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (sensorAcelerometro == null) {
            tvEstado.text = "NO HAY SENSOR DE ACELERÓMETRO"
        }

        btnVerContadores.setOnClickListener {
            Toast.makeText(
                this,
                "Movimientos Bruscos: $contadorBrusco\nMovimientos Normales: $contadorNormal",
                Toast.LENGTH_SHORT
            ).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // MÉTODOS OBLIGATORIOS PARA EL USO DE SENSORES
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            tvValores.text = String.format(Locale.getDefault(), "X: %.2f\nY: %.2f\nZ: %.2f", x, y, z)

            val absX = abs(x)
            val absY = abs(y)
            val absZ = abs(z)

            if (absX > 12f && absY > 12f && absZ > 12f) {
                if (estadoActual != "BRUSCO") {
                    estadoActual = "BRUSCO"
                    contadorBrusco++
                    tvEstado.text = "Movimiento brusco detectado"
                    mainu.setBackgroundColor(Color.RED)
                    actualizarColorTexto(Color.WHITE)
                }
            } else if (absX < 12f && absY < 12f && absZ < 12f) {
                if (estadoActual != "NORMAL") {
                    estadoActual = "NORMAL"
                    contadorNormal++
                    tvEstado.text = "Movimiento normal"
                    mainu.setBackgroundColor(Color.GREEN)
                    actualizarColorTexto(Color.BLACK)
                }
            }
        }
    }

    private fun actualizarColorTexto(color: Int) {
        tvTitulo.setTextColor(color)
        tvValores.setTextColor(color)
        tvEstado.setTextColor(color)
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorAcelerometro?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}