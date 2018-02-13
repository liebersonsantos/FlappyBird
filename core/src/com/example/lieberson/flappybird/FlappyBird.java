package com.example.lieberson.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch; // A classe spriteBatch  é utilizada para criar as animações, cria imagens, texturas e colocar essas imagens para que a classe renderize as imagens
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Random numeroRandomico;
    private BitmapFont fonte;


    /*Atributos de configuraçao*/
    private int movimento = 0;
    private int larguraDispositivo;
    private int alturaDispositivo;
    private int pontuacao = 0;

    private int estadoJogo = 0;  /* 0 -> jogo nao iniciado 1 -> jogo iniciado */

    private float variacao = 0;

    private float velocidadeQueda = 0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRandomica;
    private boolean marcouPonto; // por padrao, o valor de boolean é false


	
	@Override
	public void create () {

        batch = new SpriteBatch();
        numeroRandomico = new Random();

        // configurando a fonte(texto)
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);


        /*Configurando o passaro*/
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");

        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");

        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();

        posicaoInicialVertical = alturaDispositivo / 2;

        posicaoMovimentoCanoHorizontal = larguraDispositivo;

        espacoEntreCanos = 300;


	}

	@Override
	public void render () { /*No metodo render a ordem das declaraçoes influencia no layout*/

        deltaTime = Gdx.graphics.getDeltaTime();

	    /*Controlando os movimentos do passaro*/
        variacao += deltaTime * 10;

        if (variacao > 2) {variacao = 0;}

        if (estadoJogo == 0){ /*nao iniciado ainda*/

        if (Gdx.input.justTouched()){
            estadoJogo = 1;
        }

    }else {

        /*Controlando o movimento dos canos(velocidade)*/
        posicaoMovimentoCanoHorizontal -= deltaTime * 170;

        velocidadeQueda++;


        /*Se a tela for tocada, velocidade de queda recebe -10 e o passaro sobe*/
        if (Gdx.input.justTouched()) {

            velocidadeQueda = -15;
        }

        /*Se a posicao inicial vertical for maior que 0 ou a velocidade da queda for menor que 0,
        * a velocidade passa a ser positiva, ou seja, maior que zero, entao a velocidade é decrementada*/
        if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
            posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
        }

       /*Verificando se o cano saiu totalmente da tela*/
        if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
            posicaoMovimentoCanoHorizontal = larguraDispositivo;
            alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
            marcouPonto = false;
        }

        //verifica a pontuação
        if (posicaoMovimentoCanoHorizontal < 120){
            if (!marcouPonto){
                pontuacao ++;
                marcouPonto = true;
            }


        }


    }
        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);  //o primeiro parametro é o objeto texture(imagem), o segundo é o eixo x(horizontal) e o terceiro é o eixo y(vertical).
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50); //convertendo a pontuação em string

        batch.end();

	}







}
