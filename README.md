# Cannon Game - Trabalho de Programacao III

Aplicativo Android completo do jogo "Cannon Game", desenvolvido a partir do
Capitulo 6 do livro *Android 6 for Programmers: An App-Driven Approach*
(3rd Edition), com recursos extras de jogabilidade, graficos, som, persistencia
e matematica aplicada.

## Integrantes

- Pedro de Souza Oliveira
- Cassio Eduardo Madeira

## Objetivo

Implementar a aplicacao do capitulo, reproduzindo fielmente os conceitos
estudados na disciplina e evoluindo o jogo com recursos extras:

- animacao quadro a quadro (frame-by-frame);
- desenho de graficos com `Canvas` e `Paint`;
- execucao de sons com `SoundPool` e `MediaPlayer`;
- game loop executado em uma thread separada;
- renderizacao com `SurfaceView` e `SurfaceHolder`;
- modo imersivo e tela cheia (orientacao paisagem);
- interacao por toque na tela.

## Requisitos de ambiente

- Android Studio (o projeto foi compilado com AGP 9.4.1 / Gradle 9.6.0).
- JDK 17 ou superior. A compilacao validada usou o JDK 25 que acompanha o
  Android Studio.
- Android SDK com a plataforma `compileSdk 37` instalada.
- Um dispositivo Android ou um emulador com Android 7.0 (API 24) ou superior.

## Como executar

### Pelo Android Studio

1. Abra o Android Studio e escolha **File > Open**, apontando para a pasta do
   projeto (a que contem o arquivo `settings.gradle`).
2. Aguarde a sincronizacao do Gradle (a primeira sincronizacao baixa o Gradle e
   as dependencias do AndroidX).
3. Crie ou selecione um dispositivo em **Device Manager** (um emulador serve).
4. Clique em **Run 'app'** (Shift+F10).

### Pela linha de comando

No Windows:

```
gradlew.bat assembleDebug
```

No Linux ou macOS:

```
./gradlew assembleDebug
```

O APK de depuracao e gerado em:

```
app/build/outputs/apk/debug/app-debug.apk
```

Para instalar em um dispositivo conectado:

```
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Como testar a aplicacao

Esta secao descreve como repetir, do zero, o teste que foi feito no
desenvolvimento. O aplicativo foi validado em um emulador Android 17 (API 37),
resolucao 1080x2400.

### 1. Preparar um dispositivo

Pelo Android Studio:

1. Abra o **Device Manager** (icone de celular na barra lateral direita).
2. Clique em **Create Device**, escolha um modelo de telefone (por exemplo,
   Pixel) e avance.
3. Em **System Image**, escolha uma imagem com Android 7.0 (API 24) ou superior.
   Se ela ainda nao estiver baixada, clique em **Download** e aguarde.
4. Finalize a criacao e clique no botao **Play** para ligar o emulador. Aguarde
   o sistema terminar de iniciar (a tela inicial do Android aparece).

Alternativa com celular fisico: ative o modo desenvolvedor
(**Configuracoes > Sobre o telefone > toque 7 vezes em Numero da versao**) e
depois ligue a **Depuracao USB** em **Opcoes do desenvolvedor**. Conecte o
aparelho por cabo e autorize o computador quando ele perguntar.

### 2. Compilar, instalar e abrir

Pelo Android Studio: clique em **Run 'app'** (Shift+F10) e escolha o
dispositivo na lista.

Pela linha de comando, com o emulador ou o celular ja conectado:

```
gradlew.bat installDebug
adb shell am start -n com.pedrou.cannongame/.MainActivity
```

### 3. O que deve acontecer

1. O aplicativo abre em **modo paisagem**, em tela cheia, com o canhao a
   esquerda, os alvos no centro e dois botoes no canto superior direito (o do
   tema e o do som).
2. No canto superior esquerdo aparece um cronometro que comeca em 10 segundos.
3. Tocar na tela faz o canhao apontar para o ponto tocado e disparar um
   projetil. Enquanto houver um projetil na tela, novos toques apenas mudam a
   mira.
4. Acertar um alvo soma pontos e devolve segundos ao cronometro. Acertar o
   obstaculo vermelho tira segundos.
5. Ao acertar, uma pequena explosao de particulas aparece no ponto do impacto.
6. Quando o tempo acaba, ou quando o ultimo nivel e vencido, aparece a **tela de
   resultados** com disparos efetuados, tempo total, pontuacao, nivel alcancado,
   precisao, recorde e o ranking das maiores pontuacoes, alem do botao
   **Reset Game**.

### 4. Roteiro de verificacao (checklist)

- [ ] O projeto compila sem erros (`gradlew.bat assembleDebug`).
- [ ] O aplicativo instala e abre sem fechar sozinho.
- [ ] Tocar na tela dispara o projetil.
- [ ] Acertar um alvo aumenta a pontuacao (visivel na tela de resultados).
- [ ] Errar o obstaculo vermelho custa segundos.
- [ ] Deixar o tempo acabar faz a tela de resultados aparecer.
- [ ] O botao **Reset Game** inicia uma nova partida.
- [ ] O botao de tema troca as cores do cenario entre o modo dia e o modo noite.
- [ ] O botao de som responde (o som so pode ser conferido em um dispositivo
      com audio).
- [ ] Pressionar a tecla Home e voltar ao aplicativo mantem o jogo funcionando
      (nao pode ficar uma tela preta).

## Como jogar

- Toque na tela para mirar e disparar: o canhao aponta para o ponto tocado e
  dispara um projetil.
- Arraste o dedo (swipe) para ajustar o angulo (movimento vertical) e a forca do
  disparo (movimento horizontal).
- Destrua os alvos azuis e amarelos antes que o tempo acabe.
- **Nao** acerte o obstaculo vermelho: cada acerto nele custa segundos.
- Use os botoes no canto superior direito para trocar o tema (dia/noite) e para
  ligar ou desligar o som.
- Ao final da partida, uma tela de resultados mostra as estatisticas e oferece o
  botao **Reset Game**.

## Funcionalidades do livro (Capitulo 6)

- Animacao quadro a quadro controlada por uma thread dedicada.
- Desenho completo com `Canvas` e `Paint`, incluindo o cenario, o horizonte, o
  canhao, os alvos e o obstaculo.
- Movimento dos alvos com colisao nas bordas da tela.
- Deteccao de colisao entre o projetil, os alvos e o obstaculo.
- Efeitos sonoros carregados com `SoundPool` a partir de `res/raw`.
- Contagem de tempo com acrescimo por acerto e penalidade por erro.
- HUD desenhado na tela com o tempo restante.
- Dialogo final com o total de disparos e o tempo de jogo, com botao para
  reiniciar.
- Modo imersivo e tela cheia, em orientacao paisagem.

## Recursos extras implementados

Os 11 itens sugeridos no material de recursos extras foram implementados:

1. **Sistema de pontuacao melhorado** (item 1) - pontos por acerto, bonus por
   sequencia (streak), multiplicador progressivo que aumenta a cada 3 acertos
   (limitado a 5x), percentual de precisao e HUD atualizado em tempo real.
2. **Dificuldade progressiva com niveis** (item 2) - a cada nivel aumentam o
   numero de alvos, a velocidade, o deslocamento lateral e a penalidade do
   obstaculo, enquanto o tamanho dos alvos diminui. Ao limpar um nivel o jogador
   recebe tempo extra e continua na partida. Sao 5 niveis.
3. **Novos sons e efeitos sonoros** (item 3) - som de tiro, de colisao com o
   alvo, de falha, de subida de nivel e uma trilha sonora continua, com botao
   para ligar e desligar o audio.
4. **Animacoes suaves no alvo** (item 4) - os alvos surgem com uma animacao de
   escala e opacidade (fade + scale) antes de comecar a se mover.
5. **Controle por toque direcional** (item 5) - swipe vertical ajusta o angulo do
   canhao, swipe horizontal ajusta a forca do disparo (`ACTION_MOVE`).
6. **Modo noturno e modo dia** (item 6) - troca dinamica de tema que altera as
   cores do cenario, do canhao, dos elementos e das particulas.
7. **Tela de Game Over e Restart aprimorada** (item 7) - tela final com pontuacao,
   estatisticas da partida, indicacao de novo recorde, animacao de fim de jogo e
   botao de reinicio.
8. **Particulas ao acertar o alvo** (item 8) - explosao com pequenos circulos
   coloridos, movimento radial e desaparecimento gradual (fade out).
9. **Ranking local de pontuacoes** (item 9) - historico das maiores pontuacoes
   persistido com `SharedPreferences`, exibido na tela final.
10. **Alvos com movimentacao complexa** (item 10) - alem do movimento vertical
    do livro, os alvos recebem deslocamento senoidal com aceleracao progressiva,
    conforme o nivel.
11. **Colisao avancada** (item 11) - a colisao entre o projetil (circulo) e os
    alvos e o obstaculo (retangulos) usa um teste geometrico de intersecao
    circulo/retangulo, em vez da comparacao simples de retangulos do livro.

## Estrutura do projeto

```
TrabalhoEduardo/
  build.gradle                     configuracao raiz (versao do plugin Android)
  settings.gradle                  modulos e repositorios
  gradle.properties                propriedades do Gradle
  gradlew / gradlew.bat            Gradle wrapper
  gradle/wrapper/                  wrapper jar e properties
  .gitignore                       arquivos que nao devem ser versionados
  .gitattributes                   politica de fim de linha e arquivos binarios
  README.md                        este arquivo
  app/
    build.gradle                   configuracao do modulo (SDK, Java, dependencias)
    proguard-rules.pro
    src/main/
      AndroidManifest.xml
      java/com/pedrou/cannongame/
        MainActivity.java
        MainActivityFragment.java
        CannonView.java
        GameElement.java
        Cannon.java
        Cannonball.java
        Target.java
        Blocker.java
        Particle.java
        ParticleSystem.java
        GameTheme.java
        LevelConfig.java
        HighScoreStore.java
        GameResultDialogFragment.java
      res/layout/
        activity_main.xml
        fragment_main.xml
      res/values/
        strings.xml
        colors.xml
        styles.xml
      res/raw/                      efeitos sonoros e trilha sonora (.wav)
      res/mipmap-*/                 icones do aplicativo
```

### Responsabilidade das classes

| Classe | Responsabilidade |
| --- | --- |
| `MainActivity` | Activity principal; hospeda o fragmento e recebe o pedido de reinicio da tela de resultados. |
| `MainActivityFragment` | Cria e gerencia o `CannonView`; controla pausa e retomada do jogo. |
| `CannonView` | Classe central: game loop, HUD, colisoes, sons, temas e telas. |
| `GameElement` | Classe base dos elementos do jogo (caixa delimitadora e desenho). |
| `Cannon` | Canhao: angulo, potencia do disparo e desenho. |
| `Cannonball` | Projetil: velocidade, posicao e colisao avancada. |
| `Target` | Alvos: movimento, animacao de surgimento e recompensa por acerto. |
| `Blocker` | Obstaculo: movimento horizontal e penalidade de tempo. |
| `Particle` / `ParticleSystem` | Sistema de particulas das explosoes. |
| `GameTheme` | Paletas do modo dia e do modo noite. |
| `LevelConfig` | Parametros de cada nivel (alvos, velocidade, recompensas). |
| `HighScoreStore` | Persistencia do ranking local com `SharedPreferences`. |
| `GameResultDialogFragment` | Dialogo final com estatisticas e botao de reinicio. |

## Tecnologias e configuracoes

- Linguagem: **Java**.
- Gerenciador de build: **Gradle 9.6.0** com **Android Gradle Plugin 9.4.1**.
- `compileSdk 37`, `targetSdk 36`, `minSdk 24` (Android 7.0).
- Compatibilidade de codigo: Java 17.
- Bibliotecas de suporte: **AndroidX** (`appcompat`, `fragment`, `core`).
- APIs exclusivas: `SurfaceView`, `SurfaceHolder`, `Canvas`, `Paint`, `Thread`,
  `SoundPool`, `MediaPlayer`, `SharedPreferences`, `DialogFragment`.
- `applicationId`: `com.pedrou.cannongame`.
- Os arquivos de som ficam em `app/src/main/res/raw`.

## Validacao realizada

- Compilacao concluida com sucesso (`assembleDebug`).
- Aplicativo instalado e executado em emulador **Android 17 (API 37)**,
  resolucao 1080x2400, sem falhas.
- Partida completa executada: disparos por toque, acertos nos alvos, pontuacao
  com multiplicador, subida de nivel, precisao, recorde salvo e exibicao da tela
  final com o botao de reinicio.
- Troca de tema (dia/noite) verificada.
- Pausa e retomada do jogo (tecla Home e retorno) verificadas.

## Observacoes

- O projeto foi escrito a partir do capitulo, e nao copiado de um repositorio
  pronto. O pacote, os textos da interface e a organizacao do codigo foram
  definidos pelo grupo. As diferencas em relacao ao livro estao documentadas nos
  comentarios das classes.
- O capitulo foi adaptado para a biblioteca **AndroidX** (o livro usa as antigas
  bibliotecas `android.support`), exigindo que o dialogo final seja um fragmento
  publico e estatico, conforme as regras atuais do `FragmentManager`.
- A compilacao foi alinhada com a versao de Android Studio utilizada
  (AGP 9.x / Gradle 9.x).
