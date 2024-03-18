package com.dam.pm_proyecto

import android.app.AlertDialog
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewStub
import android.widget.ImageButton
import android.widget.TextView
import java.util.ArrayList
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private var tablero: ArrayList<Int> =
        arrayListOf(
            R.id.img_R1_C1,
            R.id.img_R1_C2,
            R.id.img_R1_C3,

            R.id.img_R2_C1,
            R.id.img_R2_C2,
            R.id.img_R2_C3,

            R.id.img_R3_C1,
            R.id.img_R3_C2,
            R.id.img_R3_C3,

            R.id.img_R4_C1,
            R.id.img_R4_C2,
            R.id.img_R4_C3
        )
    private lateinit var arrayCartas: ArrayList<Carta>

    private var turno: Int = 1

    private var puntosJug1 = 0
    private var puntosJug2 = 0

    private lateinit var elecciones: ArrayList<Carta>

    private var muteSonido = false

    private var contadorCartas = 0

    private lateinit var stubTablero: ViewStub

    private var mensajeFinal = ""

    private var imgCards = arrayOf(
        R.drawable.card_diamond,
        R.drawable.card_heart,
        R.drawable.card_joker_r,
        R.drawable.card_joker_b,
        R.drawable.card_picas,
        R.drawable.card_trebol
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arrayCartas = ArrayList<Carta>()

        elecciones = ArrayList<Carta>()

        turno = 1

        puntosJug1 = 0
        puntosJug2 = 0

        muteSonido = false

        contadorCartas = 0

        stubTablero = findViewById<ViewStub>(R.id.vsTablero)
        stubTablero.inflate();
    }

    fun onClickMute(vista: View) {
        muteSonido = if (muteSonido) {
            findViewById<ImageButton>(R.id.btnVol).setImageResource(R.drawable.baseline_volume_off_24)
            false
        } else {
            findViewById<ImageButton>(R.id.btnVol).setImageResource(R.drawable.baseline_volume_up_24)
            true
        }
    }

    fun sonidos(nombre: String) {
        var currentSound: Int = 0
        val flips = arrayOf(R.raw.flip1, R.raw.flip2, R.raw.flip3)
        when (nombre) {
            "flip" -> currentSound = flips.random()
            "background" -> currentSound = R.raw.flip1
            "correcto" -> currentSound = R.raw.rightanswer
            "incorrecto" -> currentSound = R.raw.wronganswer
        }

        var sonido = MediaPlayer.create(this, currentSound)
        sonido.setVolume(1f, 1f)
        if (muteSonido) {
            sonido.start()
        } else {
            sonido.stop()
        }
    }

    fun clickCard(vista: View) {
        var currentImgBtn = findViewById<ImageButton>(vista.id)
        var currentImgCard = imgCards.random()
        var contador = 0
        var continuar = true

        try {
            sonidos("flip")
            while (continuar) {
                if (arrayCartas.find { item -> item.imgBtnId == vista.id } != null) {
                    if (elecciones.filter { item -> item.imgBtnId == vista.id }.size < 2) {
                        elecciones.add(
                            Carta(
                                vista.id,
                                arrayCartas.find { item -> item.imgBtnId == vista.id }!!.img_card
                            )
                        )
                    }
                    continuar = false
                    currentImgBtn.setImageResource(arrayCartas.find { item -> item.imgBtnId == vista.id }!!.img_card)
                } else {
                    if (arrayCartas.isNotEmpty()) {
                        for (carta in arrayCartas) {
                            if (carta.img_card == currentImgCard) {
                                contador++
                            }
                        }
                    }
                    if (contador < 2) {
                        currentImgBtn.setImageResource(currentImgCard)
                        currentImgBtn.tag = "close"
                        arrayCartas.add(Carta(vista.id, currentImgCard))
                        if (elecciones.filter { item -> item.imgBtnId == vista.id }.size < 2) {
                            elecciones.add(Carta(vista.id, currentImgCard))
                        }
                        continuar = false
                    } else {
                        contador = 0
                        currentImgCard = imgCards.random()
                    }
                }
            }

            if (elecciones.size >= 2) {
                for (item in tablero) {
                    findViewById<ImageButton>(item).isEnabled = false
                }
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ validar() }, 1000)
            }


        } catch (e: Exception) {
            print(e.message + e.stackTrace + e.cause)
        }
    }

    private fun validar() {
        if (elecciones.size >= 2) {
            when (elecciones[0].img_card == elecciones[1].img_card) {
                true -> {
                    sonidos("correcto")
                    tablero.remove(elecciones[0].imgBtnId)
                    tablero.remove(elecciones[1].imgBtnId)
                    if (turno == 1) {
                        puntosJug1++
                        findViewById<TextView>(R.id.txtP1).text = "PLAYER 1: $puntosJug1 PUNTOS"
                        findViewById<TextView>(R.id.txtP1).setBackgroundColor(Color.GREEN)
                        findViewById<TextView>(R.id.txtP2).setBackgroundColor(Color.RED)
                    } else {
                        puntosJug2++
                        findViewById<TextView>(R.id.txtP2).text = "PLAYER 2: $puntosJug2 PUNTOS"
                        findViewById<TextView>(R.id.txtP2).setBackgroundColor(Color.GREEN)
                        findViewById<TextView>(R.id.txtP1).setBackgroundColor(Color.RED)
                    }
                    contadorCartas++
                }

                false -> {
                    sonidos("incorrecto")
                    if (turno == 1) {
                        findViewById<TextView>(R.id.txtP1).setBackgroundColor(Color.RED)
                        findViewById<TextView>(R.id.txtP2).setBackgroundColor(Color.GREEN)
                        turno = 2
                    } else {
                        findViewById<TextView>(R.id.txtP2).setBackgroundColor(Color.RED)
                        findViewById<TextView>(R.id.txtP1).setBackgroundColor(Color.GREEN)
                        turno = 1
                    }
                }
            }
            elecciones.clear()
            for (item in tablero) {
                findViewById<ImageButton>(item).isEnabled = true
                findViewById<ImageButton>(item).setImageResource(R.drawable.card_back)
            }

            if (contadorCartas == 6) {
                if (puntosJug1 < puntosJug2) {
                     mensajeFinal = "GANADOR JUGADOR 2: \n $puntosJug2"
                } else {
                     mensajeFinal =  "GANADOR JUGADOR 1: \n $puntosJug1"
                }
                val alert = AlertDialog.Builder(this)
                alert.setMessage(mensajeFinal)
                    .setPositiveButton("Reiniciar") { dialog, id ->
                        repetir()
                    }
                    .setNegativeButton("Salir") { dialog, id ->
                        salir()
                    }
                alert.create().show()
            }
        }
    }

    fun salir() {
        exitProcess(0)
    }

    fun repetir() {
        finish();
        startActivity(getIntent());
    }
}