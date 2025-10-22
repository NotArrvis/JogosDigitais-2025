package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Texture fundo;
    Texture personagem;
    TextureRegion personagemRegiao;

    float posX = 0;
    float posY = 0;
    float velY = 0;
    boolean olhandoDireita = true;
    boolean noChao = true;

    float gravidade = -15f;
    float forcaPulo = 7f;
    float velocidade = 3f;  // velocidade fixa de movimento

    float fundoOffsetx = 0;
    float velocidadeParalaxe = 0.5f;

    char ultimaTecla = ' ';
    float tempoUltimaTecla = 0;
    float tempoMaximoPedalada = 0.5f; // tempo máximo entre toques S/D para continuar andando
    boolean andando = false;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8f, 5f);
        fundo = new Texture("background.png");
        personagem = new Texture("robopng/Run(1).png");
        personagemRegiao = new TextureRegion(personagem);
    }

    @Override
    public void resize(int width, int height) {
        if (width > 0 && height > 0)
            viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        float delta = Gdx.graphics.getDeltaTime();
        tempoUltimaTecla += delta;

        boolean teclaS = Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S);
        boolean teclaD = Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.D);

        // Movimento de pedalada
        if (teclaS || teclaD) {
            char teclaAtual = teclaS ? 'S' : 'D';

            // Se a tecla for diferente da anterior -> continua andando
            if (ultimaTecla != teclaAtual) {
                andando = true;
                tempoUltimaTecla = 0;
            } 
            // Se for a mesma tecla -> muda de lado
            else {
                olhandoDireita = !olhandoDireita;
            }

            ultimaTecla = teclaAtual;
        }

        // Se demorar muito tempo sem apertar nada, para
        if (tempoUltimaTecla > tempoMaximoPedalada) {
            andando = false;
        }

        // PULAR
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP) && noChao) {
            velY = forcaPulo;
            noChao = false;
        }
    }

    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();

        if (andando) {
            if (olhandoDireita) {
                posX += velocidade * delta;
                fundoOffsetx -= velocidade * delta * velocidadeParalaxe;
            } else {
                posX -= velocidade * delta;
                fundoOffsetx += velocidade * delta * velocidadeParalaxe;
            }
        }

        if (!noChao) {
            velY += gravidade * delta;
            posY += velY * delta;

            if (posY <= 0) {
                posY = 0;
                velY = 0;
                noChao = true;
            }
        }

        float worldWidth = viewport.getWorldWidth();
        posX = MathUtils.clamp(posX, 0, worldWidth - 1);
    }

    private void draw() {
        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        float fundoWidth = viewport.getWorldWidth();
        float fundoHeight = viewport.getWorldHeight();
        float x1 = fundoOffsetx % fundoWidth;
        if (x1 > 0) x1 -= fundoWidth;

        spriteBatch.draw(fundo, x1, 0, fundoWidth, fundoHeight);
        spriteBatch.draw(fundo, x1 + fundoWidth, 0, fundoWidth, fundoHeight);

        // Personagem (espelhado conforme o lado)
        if (olhandoDireita) {
            spriteBatch.draw(personagemRegiao, posX, posY, 1f, 1f);
        } else {
            spriteBatch.draw(personagemRegiao, posX + 1f, posY, -1f, 1f);
        }

        spriteBatch.end();
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        fundo.dispose();
        personagem.dispose();
    }
}
