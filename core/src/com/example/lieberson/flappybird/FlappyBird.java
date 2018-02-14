package com.example.lieberson.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import javax.management.StringValueExp;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch; // A classe spriteBatch  é utilizada para criar as animações, cria imagens, texturas e colocar essas imagens para que a classe renderize as imagens
    private Texture[] passaros;
    private Texture fundo;
    private Texture gameOver;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Random numeroRandomico;
    private BitmapFont fonte;
    private BitmapFont mensagem;

    /*dando forma aos objetos no game para efetuar a colisao*/
    private Circle passaroCirculo;
    private Rectangle retanguloCanotopo;
    private Rectangle retanguloCanoBaixo;
//  private ShapeRenderer shapeRenderer; //ele funciona da mesma forma que o SpriteBatch usado para desenhar formas


    /*Atributos de configuraçao*/
    private int movimento = 0;
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int pontuacao = 0;

    private int estadoJogo = 0;  /* 0 -> jogo nao iniciado 1 -> jogo iniciado  2 -> GAME OVER*/

    private float variacao = 0;

    private float velocidadeQueda = 0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRandomica;
    private boolean marcouPonto; // por padrao, o valor de boolean é false

    /*Camera*/
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;


    @Override
    public void create () {

        batch = new SpriteBatch();
        numeroRandomico = new Random();
        passaroCirculo = new Circle();

        //retanguloCanoBaixo = new Rectangle();
        //retanguloCanotopo = new Rectangle();

        //shapeRenderer = new ShapeRenderer();

        // configurando a fonte(texto)
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);


        /*Configurando o passaro*/
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");

        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");

        gameOver = new Texture("game_over.png");

        /*CONFIGURAÇOES DA CAMERA*/
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);


        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;

        posicaoInicialVertical = alturaDispositivo / 2;

        posicaoMovimentoCanoHorizontal = larguraDispositivo;

        espacoEntreCanos = 300;


    }

    @Override
    public void render () { /*No metodo render a ordem das declaraçoes influencia no layout*/

        camera.update();

        //limpando os frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();

	    /*Controlando os movimentos do passaro*/
        variacao += deltaTime * 10;

        if (variacao > 2) {variacao = 0;}

        /*estados do jogo*/
        if (estadoJogo == 0){ /*nao iniciado ainda*/

            if (Gdx.input.justTouched()){
                estadoJogo = 1;
            }

        }else {/* jogo iniciado*/

            velocidadeQueda++;
            /*Se a posicao inicial vertical for maior que 0 ou a velocidade da queda for menor que 0, a velocidade passa a ser positiva, ou seja, maior que zero, entao a velocidade é decrementada*/
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
            }

            if (estadoJogo == 1){

                posicaoMovimentoCanoHorizontal -= deltaTime * 170; /*Controlando o movimento dos canos(velocidade)*/

                if (Gdx.input.justTouched()) { /*Se a tela for tocada, velocidade de queda recebe -10 e o passaro sobe*/
                    velocidadeQueda = -18;
                }

                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) { /*Verificando se o cano saiu totalmente da tela*/
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                if (posicaoMovimentoCanoHorizontal < 120){ //verifica a pontuação
                    if (!marcouPonto){
                        pontuacao ++;
                        marcouPonto = true;
                    }

                }

            }else { /* TELA DE GAME OVER - estadoJogo = 2 */
                if (Gdx.input.justTouched()){
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;

                }

            }

        }

        batch.setProjectionMatrix(camera.combined);/*configurando dados de projeçao da camera*/

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);  //o primeiro parametro é o objeto texture(imagem), o segundo é o eixo x(horizontal) e o terceiro é o eixo y(vertical).
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50); //convertendo a pontuação em string


        if (estadoJogo == 2){

            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            mensagem.draw(batch, "Toque para Reiniciar!", larguraDispositivo / 2 - 200, alturaDispositivo / 2 - gameOver.getHeight() / 2);
        }

        batch.end();

        passaroCirculo.set(120 + passaros[0].getWidth()/2, posicaoInicialVertical + passaros[0].getHeight()/2, passaros[0].getWidth() / 2);

        retanguloCanoBaixo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                canoBaixo.getWidth(),
                canoBaixo.getHeight()
        );
        retanguloCanotopo = new Rectangle(
                posicaoMovimentoCanoHorizontal,
                alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                canoTopo.getWidth(),
                canoTopo.getHeight()
        );


/*      Desenhar formas. funciona da mesma forma que o batch, a ordem em que os objetos sao declarados, tem relevancia
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); //dessa forma, as os objetos no game sao preenchidos
        shapeRenderer.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shapeRenderer.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
        shapeRenderer.rect(retanguloCanotopo.x, retanguloCanotopo.y, retanguloCanotopo.width, retanguloCanotopo.height);
        shapeRenderer.setColor(Color.RED);

         shapeRenderer.end();
*/

        //teste de colisao => TELA DE GAME OVER
        //metodos estaticos nao precisam ser instanciados.
        if (Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) ||
                Intersector.overlaps(passaroCirculo, retanguloCanotopo) ||
                posicaoInicialVertical <= 0 ||
                posicaoInicialVertical >= alturaDispositivo) {

            estadoJogo = 2;


        }


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);


    }
}
