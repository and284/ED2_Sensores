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

class MainActivity2 : AppCompatActivity(), SensorEventListener {

    private lateinit var tvTitulo1: TextView
    private lateinit var tvValores1: TextView
    private lateinit var tvEstado1: TextView
    private lateinit var btnContadores1: Button
    private lateinit var mainLayout: LinearLayout

    private lateinit var sensorManager: SensorManager
    private var sensorPresion: Sensor? = null

    // Contadores para cada caso de presión
    private var contadorBaja = 0
    private var contadorNormal = 0
    private var contadorAlta = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        tvTitulo1 = findViewById(R.id.tvTitulo1)
        tvEstado1 = findViewById(R.id.tvEstado1)
        tvValores1 = findViewById(R.id.tvValores1)
        btnContadores1 = findViewById(R.id.btnContadores1)
        mainLayout = findViewById(R.id.mainLayout)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorPresion = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        if (sensorPresion == null) {
            tvEstado1.text = "No hay sensor de presión :("
        }

        btnContadores1.setOnClickListener {
            val mensaje = "Contadores:\nBaja: $contadorBaja | Normal: $contadorNormal | Alta: $contadorAlta"
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // MÉTODOS OBLIGATORIOS PARA EL USO DE SENSORES

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
            val presion = event.values[0]
            tvValores1.text = "Presión: $presion hPa"

            // Evaluación de los rangos de presión
            if (presion < 1000f) {
                contadorBaja++
                tvEstado1.text = "Presión baja"
                mainLayout.setBackgroundColor(Color.BLUE)
                tvTitulo1.setTextColor(Color.WHITE)
                tvValores1.setTextColor(Color.WHITE)
                tvEstado1.setTextColor(Color.WHITE)
            } else if (presion <= 1020f) {
                contadorNormal++
                tvEstado1.text = "Presión normal"
                mainLayout.setBackgroundColor(Color.GREEN)
                tvTitulo1.setTextColor(Color.BLACK)
                tvValores1.setTextColor(Color.BLACK)
                tvEstado1.setTextColor(Color.BLACK)
            } else {
                contadorAlta++
                tvEstado1.text = "Presión alta"
                mainLayout.setBackgroundColor(Color.YELLOW)
                tvTitulo1.setTextColor(Color.BLACK)
                tvValores1.setTextColor(Color.BLACK)
                tvEstado1.setTextColor(Color.BLACK)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    // Registrar el sensor al reanudar la actividad
    override fun onResume() {
        super.onResume()
        sensorPresion?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    // Desregistrar el listener al pausar la actividad por buenas prácticas
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}